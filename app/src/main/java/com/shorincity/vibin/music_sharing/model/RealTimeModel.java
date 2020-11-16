package com.shorincity.vibin.music_sharing.model;

import java.util.HashMap;

public class RealTimeModel {
    private HashMap<String, RealTimeSession> sessions = new HashMap<>();
    private HashMap<String, RealTimeUser> users = new HashMap<>();

    public HashMap<String, RealTimeSession> getSessions() {
        return sessions;
    }

    public void setSessions(HashMap<String, RealTimeSession> sessions) {
        this.sessions = sessions;
    }

    public HashMap<String, RealTimeUser> getUsers() {
        return users;
    }

    public void setUsers(HashMap<String, RealTimeUser> users) {
        this.users = users;
    }
}
