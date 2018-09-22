package com.example.diprivi.g_hack;

public class ItemList {
    float lat;
    float lon;
    float decibel;

    public ItemList(float lat, float lon, float decibel) {
        this.lat = lat;
        this.lon = lon;
        this.decibel = decibel;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    public float getDecibel() {
        return decibel;
    }
}
