package com.veek.callblocker.Model;

import android.telephony.PhoneNumberUtils;

/**
 * Crafted by veek on 18.06.16 with love ♥
 */
public class Blacklist {

    // Two mapping fields for the database table blacklist
    public long id;
    public String phoneNumber;
    public String phoneName;

    // Default constructor
    public Blacklist() {

    }

    // To easily create Blacklist object, an alternative constructor
    public Blacklist(final String phoneNumber, final String phoneName) {
        this.phoneName = phoneName;
        this.phoneNumber = phoneNumber;
    }

    // Overriding the default method to compare between the two objects bu phone number
    @Override
    public boolean equals(final Object obj) {

        // If passed object is an instance of Blacklist, then compare the phone numbers, else return false as they are not equal
        if(obj.getClass().isInstance(new Blacklist()))
        {
            // Cast the object to Blacklist
            final Blacklist bl = (Blacklist) obj;

            // Compare whether the phone numbers are same, if yes, it defines the objects are equal
            if(PhoneNumberUtils.compare(bl.phoneNumber, this.phoneNumber))
                return true;
        }
        return false;
    }
}
