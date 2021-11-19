package com.example.myInst.payload.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginRequest {

    @NotEmpty(message = "Username not can empty")
    private String username;
    @NotEmpty(message = "Password not can empty")
    private String password;
}
