package com.joel.gruppuppgiftitsakerhet.util;

public class MaskingUtils {

    /**
     * Kollar om email är godkänd
     * Gör en email anonym, geom att bara visa första och sista bokstaven av namndelen, allt emellan blir *
     */
    public static String anonymize(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address");
        }

        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];

        if (username.length() <= 2) {
            return username + "@" + domain;
        }

        StringBuilder anonymizedUsername = new StringBuilder();
        anonymizedUsername.append(username.charAt(0));
        for (int i = 1; i < username.length() - 1; i++) {
            anonymizedUsername.append("*");
        }
        anonymizedUsername.append(username.charAt(username.length() - 1));

        return anonymizedUsername + "@" + domain;
    }
}