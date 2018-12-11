package de.nodomain.tobihille.seniorlauncher;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class SeniorLauncherImages extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senior_launcher_images);

        final ArrayList<ContactRepresentation> contactList =
                (ArrayList<ContactRepresentation>) getIntent().getSerializableExtra("contactList");

        LinearLayout verticalLayout = (LinearLayout) findViewById(R.id.verticalLayout);

        SeniorLauncher.createContactButtons(
                verticalLayout,
                contactList,
                this,
                SeniorLauncher.SOURCE_IMAGE);

        //add gallery button
        LinearLayout.LayoutParams horizontalLayoutParams = SeniorLauncher.getHorizontalLayoutParams();
        LinearLayout.LayoutParams buttonParams = SeniorLauncher.getButtonParams(SeniorLauncher.BUTTON_TYPE_DEFAULT, this);

        LinearLayout horizontalLayout = new LinearLayout(this);
        horizontalLayout.setLayoutParams(horizontalLayoutParams);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        verticalLayout.addView(horizontalLayout);

        Button b = new Button(this);
        b.setText(R.string.show_images);
        b.setCompoundDrawablesWithIntrinsicBounds( R.drawable.gallery, 0, 0, 0);
        b.setLayoutParams(buttonParams);
        b.setId(contactList.size());
        int id_ = b.getId();
        horizontalLayout.addView(b);
        View buttonInLayout = this.findViewById(id_);
        buttonInLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setType("image/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        SeniorLauncher.addGoBackButton(verticalLayout, this, contactList.size() + 1);
    }

    //public necessary, is called via reflection
    public void openMms(String number) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("mms:" + SeniorLauncher.cleanPhoneNumber(number)));
        startActivity(sendIntent);
    }
}
