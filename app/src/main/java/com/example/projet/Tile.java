package com.example.projet;


public class Tile {
    boolean isPressed = false;


    public int x, y, width, height;
    public boolean isError = false;
    public boolean isTransparent = false;
    public Tile(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}



