package com.kierki.client;

import java.io.IOException;

public class AuthenticationModule {

    private FileUserManager userManager = new FileUserManager();

    public boolean loginUser(String username, String password) {
        try {
            if (userManager.loginUser(username, password)) {
                System.out.println("Login successful");
                return true;
            } else {
                System.out.println("Login failed");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registerUser(String username, String password) {
        try {
            userManager.registerUser(username, password);
            System.out.println("Registration successful");
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("User already exists");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}