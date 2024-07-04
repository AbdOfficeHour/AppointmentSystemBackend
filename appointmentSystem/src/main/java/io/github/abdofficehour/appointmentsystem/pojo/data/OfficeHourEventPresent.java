package io.github.abdofficehour.appointmentsystem.pojo.data;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class OfficeHourEventPresent {
    private int eventId;
    private String userId;
}
