package com.example.combine;

public class requestModel {
   private float jerk_value;
    private float speed_value;
    private float noise_value;

    private String street_address;

    public requestModel() {
    }

    public requestModel(float jerk_value, float speed_value, float noise_value, String street_address) {
        this.jerk_value = jerk_value;
        this.speed_value = speed_value;
        this.noise_value = noise_value;
        this.street_address = street_address;
    }

    public float getJerk_value() {
        return jerk_value;
    }

    public void setJerk_value(float jerk_value) {
        this.jerk_value = jerk_value;
    }

    public float getSpeed_value() {
        return speed_value;
    }

    public void setSpeed_value(float speed_value) {
        this.speed_value = speed_value;
    }

    public float getNoise_value() {
        return noise_value;
    }

    public void setNoise_value(float noise_value) {
        this.noise_value = noise_value;
    }

    public String getStreet_address() {
        return street_address;
    }

    public void setStreet_address(String street_address) {
        this.street_address = street_address;
    }
}

