package com.cortis;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args) {

        PasswordEncoder encoder =
                new BCryptPasswordEncoder();

        String hash = encoder.encode("test1234");

        System.out.println(hash);
    }
}