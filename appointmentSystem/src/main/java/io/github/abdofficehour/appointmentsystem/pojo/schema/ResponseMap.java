package io.github.abdofficehour.appointmentsystem.pojo.schema;

import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
public class ResponseMap {
    private int code;
    private String message;
    private Object data;
}
