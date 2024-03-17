package com.example.busticketbookingapp.Admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bus {
    private String busNumber;
    private String busPlateNumber;
    private int numberOfSeats, availableSeats;
    private String source;
    private String destination;
    private List<String> selectedRoutes;
    private List<TimeBoxData> timeBoxDataList;
    private double minimumFare;
    private double intermediateFare;
    private double maximumFare;


    
    public Bus(String busNumber, String busPlateNumber, int numberOfSeats, int availableSeats, String source, String destination,
               List<String> selectedRoutes, double minimumFare, double intermediateFare, double maximumFare, List<TimeBoxData> timeBoxDataList) {
        this.busNumber = busNumber;
        this.busPlateNumber = busPlateNumber;
        this.numberOfSeats = numberOfSeats;
        this.source = source;
        this.destination = destination;
        this.selectedRoutes = selectedRoutes;
        this.minimumFare = minimumFare;
        this.intermediateFare = intermediateFare;
        this.maximumFare = maximumFare;
        this.timeBoxDataList = timeBoxDataList;
        this.availableSeats = availableSeats;
    }

    
    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getBusPlateNumber() {
        return busPlateNumber;
    }

    public void setBusPlateNumber(String busPlateNumber) {
        this.busPlateNumber = busPlateNumber;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public int getAvailableSeats() { return availableSeats; }

    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public List<String> getSelectedRoutes() {
        return selectedRoutes;
    }

    public void setSelectedRoutes(List<String> selectedRoutes) {
        this.selectedRoutes = selectedRoutes;
    }

    public List<TimeBoxData> getTimeBoxDataList() {
        return timeBoxDataList;
    }

    public void setTimeBoxDataList(List<TimeBoxData> timeBoxDataList) {
        this.timeBoxDataList = timeBoxDataList;
    }

    public double getMinimumFare() {
        return minimumFare;
    }

    public void setMinimumFare(double minimumFare) {
        this.minimumFare = minimumFare;
    }

    public double getIntermediateFare() {
        return intermediateFare;
    }

    public void setIntermediateFare(double intermediateFare) {
        this.intermediateFare = intermediateFare;
    }

    public double getMaximumFare() {
        return maximumFare;
    }

    public void setMaximumFare(double maximumFare) {
        this.maximumFare = maximumFare;
    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("busNumber", busNumber);
        result.put("busPlateNumber", busPlateNumber);
        result.put("numberOfSeats", numberOfSeats);
        result.put("availableSeats", availableSeats);
        result.put("source", source);
        result.put("destination", destination);
        result.put("selectedRoutes", selectedRoutes);
        result.put("minimumFare", minimumFare);
        result.put("intermediateFare", intermediateFare);
        result.put("maximumFare", maximumFare);
        String lastTicketBookingTime = "";
        result.put("lastTicketBookingTime", lastTicketBookingTime);
        return result;
    }
}
