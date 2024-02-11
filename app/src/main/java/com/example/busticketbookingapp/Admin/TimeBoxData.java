package com.example.busticketbookingapp.Admin;

public class TimeBoxData {
    private String startTime;
    private String endTime;
    private String direction;

    public TimeBoxData(String startTime, String endTime, String direction) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.direction = direction;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getDirection() {
        return direction;
    }
}
