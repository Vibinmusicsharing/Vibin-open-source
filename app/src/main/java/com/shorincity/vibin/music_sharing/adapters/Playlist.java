package com.shorincity.vibin.music_sharing.adapters;
// playlist class
public class Playlist {
    String name;
  int id;

    public Playlist(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public Playlist(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
