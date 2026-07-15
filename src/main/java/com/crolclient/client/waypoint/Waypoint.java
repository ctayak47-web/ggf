package com.crolclient.client.waypoint;

/** Модель одной точки-метки (waypoint), сохраняемая в JSON между сессиями. */
public class Waypoint {
    public String name;
    public double x;
    public double y;
    public double z;
    public String dimension;
    /** Цвет метки на HUD, ARGB hex-строка. */
    public String color = "#3B82F6FF";

    public Waypoint() {
    }

    public Waypoint(String name, double x, double y, double z, String dimension) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
    }
}
