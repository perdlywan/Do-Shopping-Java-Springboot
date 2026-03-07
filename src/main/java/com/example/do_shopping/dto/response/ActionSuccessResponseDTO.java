package com.example.do_shopping.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ActionSuccessResponseDTO {
    private int status;
    private String message;
    private Object data;
}
