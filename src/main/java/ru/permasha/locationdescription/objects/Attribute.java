package ru.permasha.locationdescription.objects;

public class Attribute {

    int radius;
    String message;

    public Attribute(int radius, String message) {
        this.radius = radius;
        this.message = message;
    }

    public int getRadius() {
        return radius;
    }

    public String getMessage() {
        return message;
    }

    public Attribute toAttribute() {
        return new Attribute(radius, message);
    }
}
