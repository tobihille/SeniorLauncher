package de.nodomain.tobihille.seniorlauncher;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import java.util.ArrayList;

public class LoadContactsTask extends Thread {

    private ContentResolver contentResolver = null;
    LoadContactsTask(ContentResolver cr) {
        contentResolver = cr;
    }

    private ArrayList<ContactRepresentation> contactList = null;

    @Override
    public void run() {
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
                    if (contactList == null) {
                        contactList = new ArrayList<>();
                    }

                    contactList.add(new ContactRepresentation(phoneNumber, imageUri));
                }
            }
        }
        if (contactsCursor!=null) {
            contactsCursor.close();
        }
    }

    public ArrayList<ContactRepresentation> getContactList() {
        return this.contactList;
    }
}
