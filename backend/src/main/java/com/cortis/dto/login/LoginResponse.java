package com.cortis.dto.login;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponse {


    private String username;
    private String token;


    public LoginResponse(String username, String s) {
        this.username = username;
        this.token = s;
    }
}
