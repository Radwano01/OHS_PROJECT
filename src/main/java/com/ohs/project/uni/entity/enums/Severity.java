package com.ohs.project.uni.entity.enums;

public enum Severity {
    Airy, Light, Middle, Serious, Intense;


    public static Severity getValue(int val){
        return switch (val) {
            case 1 -> Airy;
            case 2 -> Light;
            case 3 -> Middle;
            case 4 -> Serious;
            case 5 -> Intense;
            default -> throw new IllegalArgumentException("Invalid value: " + val);
        };
    }
}
