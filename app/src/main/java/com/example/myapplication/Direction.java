package com.example.myapplication;

public enum Direction {
    LEFT(1, 0),
    RIGHT(-1, 0),
    UP(0, -1),
    DOWN(0, -1),
    ;
    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
