package de.nodomain.tobihille.seniorlauncher;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import java.util.ArrayList;

public class SeniorLauncher extends AppCompatActivity {

    private static final int requestPermission = 1000;

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
        }
        return this.contactList;
    }

    private void readContacts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        ContentResolver contentResolver = getContentResolver();
        Cursor contactsCursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((contactsCursor != null ? contactsCursor.getCount() : 0) > 0) {
            int colIdxHasPhoneNumber = contactsCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
            int colIdxPhotoUri = contactsCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);
            //int colIdxName = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

            while (/*contactsCursor != null && */contactsCursor.moveToNext()) {
                String id = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts._ID));
                int hasPhoneNumber = contactsCursor.getInt(colIdxHasPhoneNumber);
                String imageUri = contactsCursor.getString(colIdxPhotoUri);
                //String name = contactsCursor.getString(colIdxName);

                String phoneNumber = null;
                if (hasPhoneNumber > 0) {
                    Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                    + " = ?", new String[] { id }, null);
                    int colIdxNormalizedNumber = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER);
                    int colIdxNumber = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(colIdxNormalizedNumber);
                        if (phoneNumber != null) {
                            break;
                        }
                    }
                    if (phoneNumber == null) { //if no number could be normalized we'll try the entered string
                        phoneCursor.moveToFirst();
                        while (phoneCursor.moveToNext()) {
                            phoneNumber = phoneCursor.getString(colIdxNumber);
                            if (phoneNumber != null) {
                                break;
                            }
                        }
                    }
                    phoneCursor.close();
                }

                if (phoneNumber != null && imageUri != null) {
                    if (this.contactList == null) {
                        this.contactList = new ArrayList<>();
                    }

                    this.contactList.add(new ContactRepresentation(phoneNumber, imageUri));
                }
            }
        }
        if (contactsCursor!=null) {
            contactsCursor.close();
        }
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
}
