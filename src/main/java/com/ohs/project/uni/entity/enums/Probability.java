package com.ohs.project.uni.entity.enums;

public enum Probability {
    Tiny, Small, Middle, High, Extreme;

    public static Probability getValue(int val) {
        return switch (val) {
            case 1 -> Tiny;
            case 2 -> Small;
            case 3 -> Middle;
            case 4 -> High;
            case 5 -> Extreme;
            default -> throw new IllegalArgumentException("Invalid value: " + val);
        };
    }
}
