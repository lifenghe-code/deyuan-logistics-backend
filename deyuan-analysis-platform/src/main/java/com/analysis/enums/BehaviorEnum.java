package com.analysis.enums;

public enum BehaviorEnum {
    NORMAL((byte) 0x01,"正常"),
    SMOKE((byte) 0x01,"吸烟"),
    TIRED((byte) 0x01,"疲劳驾驶");
    private final Byte behaviorCode;
    private final String behaviorName;

    public Byte getBehaviorCode() {
        return behaviorCode;
    }

    public String getBehaviorName() {
        return behaviorName;
    }

    BehaviorEnum(Byte behaviorCode, String behaviorName) {
        this.behaviorCode = behaviorCode;
        this.behaviorName = behaviorName;
    }
}
