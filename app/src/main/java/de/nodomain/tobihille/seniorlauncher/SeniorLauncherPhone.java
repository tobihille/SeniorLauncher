package de.nodomain.tobihille.seniorlauncher;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.ArrayList;

public class SeniorLauncherPhone extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senior_launcher_phone);

        final ArrayList<ContactRepresentation> contactList =
                (ArrayList<ContactRepresentation>) getIntent().getSerializableExtra("contactList");

        LinearLayout verticalLayout = (LinearLayout) findViewById(R.id.verticalLayout);

        LinearLayout.LayoutParams horizontalLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout horizontalLayout = null;

        for (int i = 0; i < contactList.size(); i++) {
            if (i % 3 == 0) { //3 column layout works best i'd say
                horizontalLayout = new LinearLayout(this);
                horizontalLayout.setLayoutParams(horizontalLayoutParams);
                horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                verticalLayout.addView(horizontalLayout);
            }

            ContactRepresentation currentContact = contactList.get(i);

            int id_ = 0;
            try {
                Bitmap b = MediaStore.Images.Media
                        .getBitmap(this.getContentResolver(),
                                Uri.parse(currentContact.getContactImageUri()));
                ImageButton button = new ImageButton(this);
                button.setBackground(new BitmapDrawable(null, b));
                button.setId(i);
                id_ = button.getId();
                horizontalLayout.addView(button, buttonParams);
            } catch (IOException ioe) {
                Button button = new Button(this);
                button.setId(i);
                button.setText(currentContact.getPhoneNumber());
                id_ = button.getId();
                horizontalLayout.addView(button, buttonParams);
            }

            View buttonInLayout = findViewById(id_);
            buttonInLayout.setOnClickListener(new PhoneNumberOnClickListener(contactList.get(i).getPhoneNumber()) {
                @Override
                public void onClick(View view) {
                    callNumber(this.phoneNumber);
                }
            });
        }

        horizontalLayout = new LinearLayout(this);
        horizontalLayout.setLayoutParams(horizontalLayoutParams);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        verticalLayout.addView(horizontalLayout);
        Button b = new Button(this);
        b.setText(getString(R.string.go_back));
        b.setLayoutParams(buttonParams);
        b.setHeight(140);
        b.setId(contactList.size());
        int id_ = b.getId();
        horizontalLayout.addView(b);
        View buttonInLayout = findViewById(id_);
        buttonInLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void dialNumber(String phoneNumber) {
        Intent intent = new Intent(
                Intent.ACTION_DIAL,
                Uri.fromParts("tel", SeniorLauncher.cleanPhoneNumber(phoneNumber), null)
        );
        startActivity(intent);
    }

    private void callNumber(String phoneNumber) {
        try {
            Intent intent = new Intent(
                    Intent.ACTION_CALL,
                    Uri.fromParts("tel", SeniorLauncher.cleanPhoneNumber(phoneNumber), null)
            );

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                dialNumber(phoneNumber);
            } else {
                startActivity(intent);
            }
        } catch (RuntimeException e) {
            dialNumber(phoneNumber);
        }
    }
}
