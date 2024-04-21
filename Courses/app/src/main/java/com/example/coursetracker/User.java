package com.example.coursetracker;

public class User {
    private static User instance;
    private String username;
    private String phone;
    private int version;
    private User() { // Private constructor
    }
    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    // Getters and setters for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // getters and setters for phone
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {

        if (phone != null && !phone.isEmpty()) {
            this.phone = formatPhoneNumber(phone);
        }
    }
    public String formatPhoneNumber(String rawPhoneNumber) {
        String formattedPhoneNumber = rawPhoneNumber.replaceAll("[^0-9]", "");
        return formattedPhoneNumber;
    }

    public int getDatabaseVersion(String username){
        return version;
    }

    public void setDatabaseVersion(String username, int version) {
        this.version = version;
    }

}
