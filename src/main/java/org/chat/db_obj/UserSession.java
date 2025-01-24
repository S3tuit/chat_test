package org.chat.db_obj;

import org.chat.ChatApp;

import java.sql.*;
import java.util.UUID;


public class UserSession {

    private UUID token;
    private UserProfile profile;
    private Thread threadUpdLastAccess = null;
    private ChatApp chatApp;

    public UserSession(UUID token, UserProfile userProfile, ChatApp chatApp) {
        this.token = token;
        this.profile = userProfile;
        this.chatApp = chatApp;
    }

    public UserSession(ChatApp chatApp) {
        this.chatApp = chatApp;
    }

    public String getUsername() {
        return profile.getUsername();
    }

    public long getUserId() {
        return profile.getUserID();
    }

    public void setUserProfile(UserProfile userProfile) {
        this.profile = userProfile;
    }

    public UUID getToken() {
        return token;
    }

    // If the username and password present in DB, assigns them to the current instance variables.
    // return logic similar to the one of isValidSession.
    public int validateLogin(String username, String password, UUID sessionToken) {
        try (Connection conn = DatabaseConnectionPool.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("select id from users where username = ? and password = ?");

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            // valid user
            if (rs.next()) {

                long userId = rs.getLong("id");
                int sessionValidity = isValidSession(userId, sessionToken);
                this.setSession(sessionToken, new UserProfile(username, userId));

                switch (sessionValidity) {
                    case 1:
                        insertNewSession(userId, sessionToken);
                        return 1;
                    case 2:
                        updateThisSession();
                        return 2;
                    case 3:
                        return 3;
                    default:
                        throw new IllegalStateException("Unexpected session validity: " + sessionValidity);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // not valid user
        return 0;
    }

    // return 1 if session with different token and a last_access older than 5 minutes ago... or no session.
    // return 2 if there already is a session with the same token.
    // return 3 if there already is a session with a different token and a last_access in 5 minutes ago.
    public int isValidSession(long userId, UUID sessionToken) {

        try(Connection conn = DatabaseConnectionPool.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("" +
                    "select token " +
                    "from session " +
                    "where user_id = ? and last_access > current_timestamp - interval '5 minutes';");
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                if (rs.getString("token").equals(sessionToken.toString())) {
                    return 2;
                };
                return 3;
            } else {
                return 1;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertNewSession(long userId, UUID sessionToken) {
        try(Connection conn = DatabaseConnectionPool.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("delete from session where user_id = ?;");
            ps.setLong(1, userId);
            ps.executeUpdate();

            ps = conn.prepareStatement(
                    "insert into session(user_id, token, last_access) " +
                            "values(?, ?::UUID, current_timestamp)");
            ps.setLong(1, userId);
            ps.setString(2, sessionToken.toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateThisSession() {
        try(Connection conn = DatabaseConnectionPool.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("" +
                    "update session " +
                    "set last_access = current_timestamp " +
                    "where user_id = ?;");
            ps.setLong(1, getUserId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setSession(UUID sessionToken, UserProfile userProfile) {
        this.token = sessionToken;
        this.profile = userProfile;

        if (threadUpdLastAccess == null || !threadUpdLastAccess.isAlive()) {
            threadUpdLastAccess = new Thread(new UpdateLastAccess());
            chatApp.addAppThread(threadUpdLastAccess);
            threadUpdLastAccess.start();
        }
    }

    public void invalidateOtherSessions() {

        try (Connection conn = DatabaseConnectionPool.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("" +
                    "delete " +
                    "from session " +
                    "where user_id = ?;");
            ps.setLong(1, this.getUserId());
            ps.executeUpdate();

            this.insertNewSession(this.getUserId(), this.token);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // returns true if db has a session with that userId and token
    public boolean authenticate() {
        if (token == null || profile == null) {
            return false;
        }

        try (Connection conn = DatabaseConnectionPool.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("" +
                    "select 1 " +
                    "from session " +
                    "where user_id = ? " +
                    "  and token = ?::UUID;");
            ps.setLong(1, profile.getUserID());
            ps.setString(2, token.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public class UpdateLastAccess implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    updateThisSession();
                    Thread.sleep(4 * 60 * 1000 + 30 * 1000);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                    break;
                }
            }
        }

    }
}
