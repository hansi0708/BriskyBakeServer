package com.hv.briskybakeserver.Model;

public class User {

    private String Name,Password,Phone,Email;

    public User(String name, String password) {
        Name = name;
        Password = password;
    }

    public User(String name, String password, String phone, String email) {
        Name = name;
        Password = password;
        Phone = phone;
        Email = email;
    }

    public User() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
