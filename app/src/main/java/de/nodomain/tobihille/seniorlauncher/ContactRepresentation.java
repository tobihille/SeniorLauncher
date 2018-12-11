package de.nodomain.tobihille.seniorlauncher;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.Serializable;

class ContactRepresentation implements Serializable {

    private String phoneNumber = null;
    private String contactImageUri = null;

    ContactRepresentation(String phoneNumber, String contactImageUri) {
        this.phoneNumber = phoneNumber;
        this.contactImageUri = contactImageUri;
    }

    String getPhoneNumber() {
        return phoneNumber;
    }

    String getContactImageUri() {
        return contactImageUri;
    }
}
