package com.mygdx.game.NakamaController;


public class PosicionInicio {
    float x;
    float y;

    public PosicionInicio() {
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "PosicionInicio{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
