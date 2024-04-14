package com.example.garage;

public class Item {
    private String name;
    private int image;

    public Item(String name, int image) {
        this.name = name;
        this.image = image;

    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }
}
