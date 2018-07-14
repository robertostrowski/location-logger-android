package com.miketa.locationtracker;

/**
 * Created by Mietek on 2018-05-20.
 */

public class Devices {


    public final String id;
    public final String name;
    public final String uuid;
    public final String removed;
    public Devices(String id, String name, String uuid, String removed) {
        this.id = id;
        this.name = name;
        this.uuid = uuid;
        this.removed = removed;
    }

    @Override
    public String toString() {
        return name;
    }
}
