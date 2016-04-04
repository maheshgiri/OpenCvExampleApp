package com.example.hemis.opencvapp.PoissonBlending;


public class Punt {

    private float val;
    private int x;
    private int y;
    boolean isOnBorder;
    float red;
    float green;
    float blue;

    int totalVpq_R;
    int totalVpq_G;
    int totalVpq_B;

    public Punt(int x,int y,int val) {
        this.x = x;
        this.y = y;
        this.val = val;
    }

    public Punt(int x,int y){
        this.x=x;
        this.y=y;

    }




    public float getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

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


    public float getRed() {
        return red;
    }

    public void setRed(float red) {
        this.red = red;
    }

    public float getGreen() {
        return green;
    }

    public void setGreen(float green) {
        this.green = green;
    }

    public float getBlue() {
        return blue;
    }

    public void setBlue(float blue) {
        this.blue = blue;
    }

    public int getTotalVpq_R() {
        return totalVpq_R;
    }

    public void setTotalVpq_R(int totalVpq_R) {
        this.totalVpq_R = totalVpq_R;
    }

    public int getTotalVpq_G() {
        return totalVpq_G;
    }

    public void setTotalVpq_G(int totalVpq_G) {
        this.totalVpq_G = totalVpq_G;
    }

    public int getTotalVpq_B() {
        return totalVpq_B;
    }

    public void setTotalVpq_B(int totalVpq_B) {
        this.totalVpq_B = totalVpq_B;
    }
}