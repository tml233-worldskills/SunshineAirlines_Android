package com.example.sunshineairlines_android;

import java.io.Serializable;

public class FlightData implements Serializable {
    public String id;
    public String airlineName;
    public String flightNumber;
    public float price;
    public String departureDate;
    public String departureTime;
    public String cabinTypeId;
    public String cabinTypeName;
    public String aircraft;
    public int availableTickets;
}
