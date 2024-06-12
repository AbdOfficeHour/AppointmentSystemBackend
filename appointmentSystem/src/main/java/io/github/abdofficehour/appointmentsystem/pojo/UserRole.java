package io.github.abdofficehour.appointmentsystem.pojo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class UserRole {
    private String userId;
    private int roleId;
}
