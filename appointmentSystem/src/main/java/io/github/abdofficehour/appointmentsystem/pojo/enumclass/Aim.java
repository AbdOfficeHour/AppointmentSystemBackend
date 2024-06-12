package io.github.abdofficehour.appointmentsystem.pojo.enumclass;

import lombok.Getter;

@Getter
public enum Aim {
    MEETING("会议"),
    DISCUSS("研讨"),
    LEAGUE("团建"),
    LECTURE("讲座"),
    OTHER("其他");

    private final String value;

    Aim(String value){
        this.value = value;
    }

    public static Aim fromValue(String value) {
        for (Aim aim: Aim.values()){
            if (aim.value.equals(value)){
                return aim;
            }
        }
        throw new IllegalArgumentException("找不到枚举类型"+value);
    }
}
