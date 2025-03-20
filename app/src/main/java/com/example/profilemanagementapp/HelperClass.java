package com.example.profilemanagementapp;


/**
 * Represents a user profile in the application. This class is used to store and retrieve
 * user information from Firebase database.
 */
public class HelperClass {

    String name, dob, address, phone, username, password;

    public HelperClass(String name, String dob, String address, String phone, String username, String password) {
        this.name = name;
        this.dob = dob;
        this.address = address;
        this.phone = phone;
        this.username = username;
        this.password = password;
    }

    public HelperClass() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
