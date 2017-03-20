package com.example.jayti.hometowns;

/**
 * Created by Jayti on 17/03/17.
 */

public class User {
    String nickname, state, country, city;
    int year;
    Double latitude, longitude;

    public User(String nickname, String state, String country, String city, int year, Double latitude, Double longitude) {
        this.nickname = nickname;
        this.state = state;
        this.country = country;
        this.city = city;
        this.year = year;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
