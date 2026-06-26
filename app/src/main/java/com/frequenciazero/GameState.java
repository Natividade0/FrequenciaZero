package com.frequenciazero;

public class GameState {
    private int a;
    private int b;
    private int c;
    private int d;
    private int e;
    private int f;
    private int g;
    private int energy;

    public GameState() {
        energy = 100;
    }

    public void addA(int value) { a += value; }
    public void addB(int value) { b += value; }
    public void addC(int value) { c += value; }
    public void addD(int value) { d += value; }
    public void addE(int value) { e += value; }
    public void addF(int value) { f += value; }
    public void addG(int value) { g += value; }
    public void useEnergy(int value) { energy = Math.max(0, energy - value); }

    public int getA() { return a; }
    public int getB() { return b; }
    public int getC() { return c; }
    public int getD() { return d; }
    public int getE() { return e; }
    public int getF() { return f; }
    public int getG() { return g; }
    public int getEnergy() { return energy; }

    public void setA(int value) { a = value; }
    public void setB(int value) { b = value; }
    public void setC(int value) { c = value; }
    public void setD(int value) { d = value; }
    public void setE(int value) { e = value; }
    public void setF(int value) { f = value; }
    public void setG(int value) { g = value; }
    public void setEnergy(int value) { energy = Math.max(0, value); }
}
