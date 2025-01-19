package org.chat.db_obj;

import java.util.UUID;

public class UserSession {

    private UUID token;
    private UserProfile profile;

    public UserSession(UUID token, UserProfile userProfile) {
        this.token = token;
        this.profile = userProfile;
    }

    public String getUsername() {
        return profile.getUsername();
    }

    public class UpdateLastAccess implements Runnable {
        @Override
        public void run() {

        }
    }
}
