package com.artinus.subscription.common.util;

public class PhoneNumberUtils {

    private PhoneNumberUtils() {}


    public static String mask(String phoneNumber) {
        if(phoneNumber == null) {
            return "****";
        }
        return phoneNumber.replaceAll("(\\d{3})\\d{4}(\\d+)", "$1****$2");
    }
}
