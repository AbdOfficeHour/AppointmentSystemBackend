package io.github.abdofficehour.appointmentsystem.pojo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class UserInfo {
    private String id;
    private String email;
    private String username;
    private String phone;
}
