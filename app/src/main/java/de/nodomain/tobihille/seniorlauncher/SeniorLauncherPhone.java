package de.nodomain.tobihille.seniorlauncher;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class SeniorLauncherPhone extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senior_launcher_phone);

        final ArrayList<ContactRepresentation> contactList =
                (ArrayList<ContactRepresentation>) getIntent().getSerializableExtra("contactList");

        LinearLayout verticalLayout = (LinearLayout) findViewById(R.id.verticalLayout);

        SeniorLauncher.createContactButtons(
                verticalLayout,
                contactList,
                this,
                SeniorLauncher.SOURCE_PHONE
        );

    }

    private void dialNumber(String phoneNumber) {
        Intent intent = new Intent(
                Intent.ACTION_DIAL,
                Uri.fromParts("tel", SeniorLauncher.cleanPhoneNumber(phoneNumber), null)
        );
        startActivity(intent);
    }

    //public neccesary, is called via reflection
    public void callNumber(String phoneNumber) {
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
