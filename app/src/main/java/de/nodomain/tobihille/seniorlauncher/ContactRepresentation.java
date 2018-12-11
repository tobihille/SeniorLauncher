package de.nodomain.tobihille.seniorlauncher;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ContactRepresentation {

    private String phoneNumber = null;
    private Drawable contactImage = null;

    ContactRepresentation(String phoneNumber, Bitmap contactImage) {
        this.phoneNumber = phoneNumber;
        this.contactImage = new BitmapDrawable(null, contactImage);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Drawable getContactImage() {
        return contactImage;
    }
}
