package com.example.busticketbookingapp.Common;

import java.util.List;
public class BusSearchClass {
    private String busNumber;
    private String busPlateNumber;
    private List<String> selectedRoutes;

    // Default constructor (required for Firebase)
    public BusSearchClass() {
    }

    public BusSearchClass(String busNumber, String busPlateNumber, List<String> selectedRoutes) {
        this.busNumber = busNumber;
        this.busPlateNumber = busPlateNumber;
        this.selectedRoutes = selectedRoutes;
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

    public List<String> getSelectedRoutes() {
        return selectedRoutes;
    }

    public void setSelectedRoutes(List<String> selectedRoutes) {
        this.selectedRoutes = selectedRoutes;
    }
}

