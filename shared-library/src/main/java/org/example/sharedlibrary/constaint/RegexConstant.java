package org.example.sharedlibrary.constaint;

import java.util.regex.Pattern;

public class RegexConstant {

    public static final Pattern REGEX_PHONE_NUMBER = Pattern.compile("([+84|0]+(3|5|7|8|9|1[2|689]))+([0-9]{8})\\b");
    public static final Pattern REGEX_EMAIL = Pattern.compile("^[a-zA-Z0-9_.±]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+$");
    public static final Pattern REGEX_IDENTITY_CARD = Pattern.compile("^\\d{9}$");
    public static final Pattern REGEX_CITIZEN_IDENTITY_CARD = Pattern.compile("^\\d{12}$");
    public static final Pattern REGEX_PASSPORT = Pattern.compile("^(?!0{3})[a-zA-Z0-9]{3,20}$");

    private RegexConstant() {
    }
}
