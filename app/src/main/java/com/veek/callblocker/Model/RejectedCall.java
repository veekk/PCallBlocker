package com.veek.callblocker.Model;

import android.telephony.PhoneNumberUtils;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

/**
 * Crafted by veek on 29.06.16 with love ♥
 */
public class RejectedCall {
    public long id;
    public String phoneNumber;
    public String phoneName;
    public long amountCalls;
    Calendar c = Calendar.getInstance();
    Date date = new Date();
    public Long time;

    // Default constructor
    public RejectedCall() {

    }

    // To easily create Blacklist object, an alternative constructor
    public RejectedCall(final String phoneNumber, final String phoneName) {
        this.phoneName = phoneName;
        this.phoneNumber = phoneNumber;
        this.time = date.getTime();
        this.amountCalls = 1;

    }

    public void incAmount (){
        amountCalls++;
    }

    public void updTime (Long time){
        this.time = time;
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
