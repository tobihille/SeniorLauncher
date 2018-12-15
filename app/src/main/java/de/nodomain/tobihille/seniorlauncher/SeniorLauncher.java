package de.nodomain.tobihille.seniorlauncher;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class SeniorLauncher extends AppCompatActivity {

    private static final int requestPermission = 1000;
    public static final int SOURCE_PHONE = 1;
    public static final int SOURCE_IMAGE = 2;
    public static final int BUTTON_TYPE_CONTACT = 1;
    public static final int BUTTON_TYPE_DEFAULT = 2;

    private ArrayList<ContactRepresentation> contactList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senior_launcher);

        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.READ_CONTACTS
                },
                SeniorLauncher.requestPermission);
    }

    private ArrayList getContacts() {
        if (this.contactList == null) {
            this.readContacts();
            //prevent crash on factory fresh phones without contacts
            if (this.contactList == null) {
                contactList = new ArrayList<ContactRepresentation>();
            }
        }
        return this.contactList;
    }

    private void showProgress() {
        Toast text = Toast.makeText(getApplicationContext(), R.string.loading, Toast.LENGTH_LONG);
        text.show();
    }

    private void readContacts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        boolean progressIndicatorTriggered = false;
        LoadContactsTask t = new LoadContactsTask(getContentResolver());

        t.start();
        while (t.isAlive()) {
            try {
                if (!progressIndicatorTriggered) {
                    this.showProgress();
                    progressIndicatorTriggered = true;
                }
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }

        this.contactList = t.getContactList();
    }

    public void startCallActivity(View sender) {
        ArrayList contactList = this.getContacts();
        Intent intent = new Intent(this, SeniorLauncherPhone.class);
        intent.putExtra("contactList", contactList);
        startActivity(intent);
    }

    public void startImagesActivity(View sender) {
        ArrayList contactList = this.getContacts();
        Intent intent = new Intent(this, SeniorLauncherImages.class);
        intent.putExtra("contactList", contactList);
        startActivity(intent);
    }

    static String cleanPhoneNumber(String number) {
        number = number.replace(" ", "");
        number = number.replace("(", "");
        number = number.replace(")", "");
        number = number.replace("/", "");
        number = number.replace("-", "");
        return number;
    }

    static LinearLayout.LayoutParams getHorizontalLayoutParams() {
        LinearLayout.LayoutParams horizontalLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        return horizontalLayoutParams;
    }

    static LinearLayout.LayoutParams getButtonParams(int type, AppCompatActivity context) {
        LinearLayout.LayoutParams buttonParams = null;
        if (type == SeniorLauncher.BUTTON_TYPE_DEFAULT) {
            buttonParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    SeniorLauncher.dpToPx(140));
        } else {
            Display display = context.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            buttonParams = new LinearLayout.LayoutParams(
                    width / 3,
                    SeniorLauncher.dpToPx(140));
        }

        return buttonParams;
    }

    static void createContactButtons(
            LinearLayout verticalLayout,
            ArrayList<ContactRepresentation> contactList,
            final AppCompatActivity context,
            final int source
    ) {
        LinearLayout.LayoutParams horizontalLayoutParams = SeniorLauncher.getHorizontalLayoutParams();
        LinearLayout.LayoutParams buttonParams = SeniorLauncher.getButtonParams(SeniorLauncher.BUTTON_TYPE_CONTACT, context);

        Resources resources = context.getResources();
        LinearLayout horizontalLayout = null;

        ArrayList<ContactRepresentation> contactListToUse = contactList;
        if (source == SeniorLauncher.SOURCE_PHONE) {
            contactListToUse = (ArrayList<ContactRepresentation>) contactList.clone();
            String emergencyNumber = context.getString(R.string.emergency_number);

            Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    resources.getResourcePackageName(R.drawable.ambulance) + '/' +
                    resources.getResourceTypeName(R.drawable.ambulance) + '/' +
                    resources.getResourceEntryName(R.drawable.ambulance) );
            contactListToUse.add(0, new ContactRepresentation(emergencyNumber, uri.toString()));
        }

        for (int i = 0; i < contactListToUse.size(); i++) {
            if (i % 3 == 0) { //3 column layout works best i'd say
                horizontalLayout = new LinearLayout(context);
                horizontalLayout.setLayoutParams(horizontalLayoutParams);
                horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                verticalLayout.addView(horizontalLayout);
            }

            ContactRepresentation currentContact = contactListToUse.get(i);

            int id_ = 0;
            try {
                Bitmap b = MediaStore.Images.Media
                        .getBitmap(context.getContentResolver(),
                                Uri.parse(currentContact.getContactImageUri()));
                BitmapDrawable bd = new BitmapDrawable(resources, b);
                ImageButton button = new ImageButton(context);
                button.setScaleType(ImageView.ScaleType.FIT_CENTER);
                button.setImageDrawable(bd);
                button.setId(i);
                id_ = button.getId();
                horizontalLayout.addView(button, buttonParams);
            } catch (IOException ioe) {
                Button button = new Button(context);
                button.setId(i);
                button.setText(currentContact.getPhoneNumber());
                id_ = button.getId();
                horizontalLayout.addView(button, buttonParams);
            }

            View buttonInLayout = context.findViewById(id_);
            buttonInLayout.setOnClickListener(new PhoneNumberOnClickListener(currentContact.getPhoneNumber()) {
                @Override
                public void onClick(View view) {
                    if (source == SeniorLauncher.SOURCE_PHONE) {
                        try {
                            Class<SeniorLauncherPhone> c = SeniorLauncherPhone.class;
                            Method method = c.getDeclaredMethod("callNumber", String.class);
                            method.invoke(context, this.phoneNumber);
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException failiure) {
                            failiure.printStackTrace();
                        }
                    }
                    if (source == SeniorLauncher.SOURCE_IMAGE) {
                        try {
                            Class<SeniorLauncherImages> c = SeniorLauncherImages.class;
                            Method method = c.getDeclaredMethod("openMms", String.class);
                            method.invoke(context, this.phoneNumber);
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException failiure) {
                            failiure.printStackTrace();
                        }
                    }
                }
            });
        }

        if (source == SeniorLauncher.SOURCE_IMAGE) {
            return;
        }

        SeniorLauncher.addGoBackButton(verticalLayout, context, contactListToUse.size());
    }

    static void addGoBackButton(LinearLayout verticalLayout, final AppCompatActivity context, int buttonId) {
        LinearLayout.LayoutParams horizontalLayoutParams = SeniorLauncher.getHorizontalLayoutParams();
        LinearLayout.LayoutParams buttonParams = SeniorLauncher.getButtonParams(SeniorLauncher.BUTTON_TYPE_DEFAULT, context);

        LinearLayout horizontalLayout = new LinearLayout(context);
        horizontalLayout.setLayoutParams(horizontalLayoutParams);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        verticalLayout.addView(horizontalLayout);

        Button b = new Button(context);
        b.setText(context.getString(R.string.go_back));
        b.setLayoutParams(buttonParams);
        b.setId(buttonId);
        int id_ = b.getId();
        horizontalLayout.addView(b);
        View buttonInLayout = context.findViewById(id_);
        buttonInLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.finish();
            }
        });
    }

    static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}
