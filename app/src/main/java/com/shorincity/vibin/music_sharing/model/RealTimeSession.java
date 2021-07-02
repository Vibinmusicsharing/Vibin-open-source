package com.shorincity.vibin.music_sharing.model;

public class RealTimeSession {

    private int adminId;
    private String session_token;
    private int playlist_id;
    private String playlist_time;
    private int invited = 0;
    private int joined;
    private String status;
    private int start_in = 0;
    private long elapsed_time = 0;
    private boolean read_elapsed;
    private String songID;
    private String songType;
    private int songPosiionInList = 0;
    private boolean song_changed;


    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getSession_token() {
        return session_token;
    }

    public void setSession_token(String session_token) {
        this.session_token = session_token;
    }

    public int getPlaylist_id() {
        return playlist_id;
    }

    public void setPlaylist_id(int playlist_id) {
        this.playlist_id = playlist_id;
    }

    public String getPlaylist_time() {
        return playlist_time;
    }

    public void setPlaylist_time(String playlist_time) {
        this.playlist_time = playlist_time;
    }

    public int getInvited() {
        return invited;
    }

    public void setInvited(int invited) {
        this.invited = invited;
    }

    public int getJoined() {
        return joined;
    }

    public void setJoined(int joined) {
        this.joined = joined;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStart_in() {
        return start_in;
    }

    public void setStart_in(int start_in) {
        this.start_in = start_in;
    }

    public long getElapsed_time() {
        return elapsed_time;
    }

    public void setElapsed_time(long elapsed_time) {
        this.elapsed_time = elapsed_time;
    }

    public boolean isRead_elapsed() {
        return read_elapsed;
    }

    public void setRead_elapsed(boolean read_elapsed) {
        this.read_elapsed = read_elapsed;
    }

    public String getSongID() {
        return songID;
    }

    public void setSongID(String songID) {
        this.songID = songID;
    }

    public String getSongType() {
        return songType;
    }

    public void setSongType(String songType) {
        this.songType = songType;
    }

    public int getSongPosiionInList() {
        return songPosiionInList;
    }

    public void setSongPosiionInList(int songPosiionInList) {
        this.songPosiionInList = songPosiionInList;
    }

    public boolean isSong_changed() {
        return song_changed;
    }

    public void setSong_changed(boolean song_changed) {
        this.song_changed = song_changed;
    }
}
