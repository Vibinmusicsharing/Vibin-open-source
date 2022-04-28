package com.shorincity.vibin.music_sharing.model.firebase;

public class RealTimeUser {

    private String session_id;

    private String session_token;

    private int user_id;

    private String joined_status;

    public RealTimeUser() {
    }

    ;

    public RealTimeUser(String session_id, String session_token, int user_id, String joined_status) {
        this.session_id = session_id;
        this.session_token = session_token;
        this.user_id = user_id;
        this.joined_status = joined_status;
    }

    /*public RealTimeUser(String session_id, int user_id, String joined_status) {
        this.session_id = session_id;
        this.user_id = user_id;
        this.joined_status = joined_status;
    }*/

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getJoined_status() {
        return joined_status;
    }

    public void setJoined_status(String joined_status) {
        this.joined_status = joined_status;
    }

    public String getSession_token() {
        return session_token;
    }

    public void setSession_token(String session_token) {
        this.session_token = session_token;
    }
}
