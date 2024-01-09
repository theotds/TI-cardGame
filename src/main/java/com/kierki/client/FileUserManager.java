package com.kierki.client;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FileUserManager {
    private static final String FILE_PATH = "C:\\TI-java\\kierki\\src\\main\\java\\users.txt";

    public FileUserManager() {
        try {
            Files.createFile(Paths.get(FILE_PATH));
        } catch (IOException e) {
        }
    }

    public void registerUser(String username, String password) throws IOException {
        if (userExists(username)) {
            throw new IllegalArgumentException("Użytkownik już istnieje.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(username + "," + password);
            writer.newLine();
        }
    }

    public boolean loginUser(String username, String password) throws IOException {
        Map<String, String> users = loadUsers();
        return users.containsKey(username) && users.get(username).equals(password);
    }

    private boolean userExists(String username) throws IOException {
        Map<String, String> users = loadUsers();
        return users.containsKey(username);
    }

    private Map<String, String> loadUsers() throws IOException {
        Map<String, String> users = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    users.put(parts[0], parts[1]);
                }
            }
        }
        return users;
    }
}
