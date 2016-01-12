package com.iagl.opl.medt;


public class DarkVador {
    public DarkVador() {
    }

    public java.lang.String sayImYourFather(java.lang.String name) {
        java.lang.String end = ", I\'m your father !";
        name.concat(end);
        return name;
    }

    public java.lang.String getSide() {
        java.lang.String side = "dark side";
       	side.substring(0, 4);
        side.toUpperCase();
        return side;
    }
}

