package io.github.abdofficehour.appointmentsystem.pojo.data;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class UserClassification {
    private int classification;
    private String userId;
}
