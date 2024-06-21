package com.snansidansi.settings;

public enum Setting {
    NONE("none", Type.BOOLEAN);

    private final String id;
    private final Type type;

    Setting(String id, Type type) {
        this.id = id;
        this.type = type;
    }

    public String getID() {
        return this.id;
    }

    public Type getType() {
        return this.type;
    }

    public static Setting getEnumFromID(String id) {
        for (Setting setting : values())
            if (id.equals(setting.id)) return setting;
        return null;
    }

    public enum Type {
        INTEGER, BOOLEAN, STRING
    }
}
