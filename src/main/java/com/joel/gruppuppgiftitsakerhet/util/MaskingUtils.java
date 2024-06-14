package com.joel.gruppuppgiftitsakerhet.util;

public class MaskingUtils {

    /**
     * Kollar om email är godkänd
     * Gör en email anonym, geom att bara visa första och sista bokstaven av namndelen, allt emellan blir *
     * Detta är användbart i scenarier där det inte är lämpligt att visa hela e-postadressen,
     * till exempel i loggar eller felmeddelanden.
     * Huvudmetoden, anonymize, tar en e-postadress som input och returnerar en maskerad version
     * där endast den första och sista bokstaven i användarnamnsdelen är synliga, och alla andra
     * tecken ersätts med asterisker (*). Domändelen av e-postadressen förblir oförändrad.

     * Exempel:
     * Input: "exempel@exempel.com"
     * Output: "e*****l@exempel.com"
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