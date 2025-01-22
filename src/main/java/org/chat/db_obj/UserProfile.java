package org.chat.db_obj;


public class UserProfile {

    private String userName;
    private long userID;

    public UserProfile(String userName, long userID) {
        this.userName = userName;
        this.userID = userID;
    }

    public String getUsername() {
        return userName;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }
}
