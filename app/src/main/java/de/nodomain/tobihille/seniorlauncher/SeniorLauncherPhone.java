package de.nodomain.tobihille.seniorlauncher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class SeniorLauncherPhone extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senior_launcher_phone);
    }

    public void goBack(View sender) {
        finish();
    }
}
