package org.chat.db_obj;

import java.util.Random;

public class UserProfile {

    private String userName;
    private long userID;

    public UserProfile(String userName, long userID) {
        this.userName = userName;
        this.userID = userID;
    }

    public  UserProfile() {
        this.userName = UserProfile.genRandomString(5);
        this.userID = 1;
    }

    public String getUsername() {
        return userName;
    }

    public static String genRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder();

        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            randomString.append(characters.charAt(index));
        }

        return randomString.toString();
    }
}
