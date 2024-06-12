package io.github.abdofficehour.appointmentsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "心跳接口")
public class Hb {

    @Operation(summary = "固定返回200")
    @ApiResponses({
            @ApiResponse(responseCode = "200")
    })
    @GetMapping("/hb")
    public void returnHb(){
        return;
    }

}
