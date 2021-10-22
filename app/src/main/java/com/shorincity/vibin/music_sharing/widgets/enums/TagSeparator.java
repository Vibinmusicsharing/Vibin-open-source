package com.shorincity.vibin.music_sharing.widgets.enums;

public enum TagSeparator {

    COMMA_SEPARATOR(","),
    PLUS_SEPARATOR("+"),
    MINUS_SEPARATOR("-"),
    SPACE_SEPARATOR(" "),
    AT_SEPARATOR("@"),
    HASH_SEPARATOR("#");

    private final String name;

    TagSeparator(String s) {
        name = s;
    }

    public String getValue() {
        return name;
    }
}
