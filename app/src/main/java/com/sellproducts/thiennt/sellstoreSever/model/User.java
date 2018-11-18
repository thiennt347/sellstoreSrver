package com.sellproducts.thiennt.sellstoreSever.model;

public class User {

    private String Name, Phone, Password, IsStaff;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public User(String name, String password) {
        Name = name;
        Password = password;
    }

    public User() {
    }
}
