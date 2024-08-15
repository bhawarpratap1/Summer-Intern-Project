package com.example.combine;

public class userRequestModel {
    public String getPhoneno() {
        return pin_code;
    }

    public userRequestModel(String phoneno, String traffic_measure, String noise_measure, String road_condition) {
        this.pin_code = phoneno;
        this.traffic_measure = traffic_measure;
        this.noise_measure = noise_measure;
        this.road_condition = road_condition;
    }

    public userRequestModel() {
    }

    public void setPhoneno(String phoneno) {
        this.pin_code = phoneno;
    }

    public String getTraffic_measure() {
        return traffic_measure;
    }

    public void setTraffic_measure(String traffic_measure) {
        this.traffic_measure = traffic_measure;
    }

    public String getNoise_measure() {
        return noise_measure;
    }

    public void setNoise_measure(String noise_measure) {
        this.noise_measure = noise_measure;
    }

    public String getRoad_condition() {
        return road_condition;
    }

    public void setRoad_condition(String road_condition) {
        this.road_condition = road_condition;
    }

    private String pin_code,traffic_measure,noise_measure,road_condition;

}
