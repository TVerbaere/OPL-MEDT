package com.iagl.opl.medt;


public class A {
    private java.lang.String toto;

    private int john;

    public A() {
        toto = "toto";
        john = 1;
    }

    public void upJohn() {
        (this.john)--;
        java.lang.String a = "a";
        java.lang.String b = "b";
        java.lang.String res = a.concat(b);
        a = a.substring(0);
    }

    public java.lang.String getToto() {
        return toto;
    }

    public int getJohn() {
        return john;
    }

    public void eraseToto() {
        toto = null;
    }

    public int totoSize() {
        return toto.length();
    }
}

