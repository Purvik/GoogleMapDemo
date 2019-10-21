package com.student.googlemapdemo;

public class Restaurant {

    private int id;
    private String name;
    private String phone;
    private double latitude;
    private double longitude;

    public Restaurant() {
    }

    public Restaurant(int id, String name, String phone, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Restaurant(String name, String phone, double latitude, double longitude) {
        this.name = name;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
