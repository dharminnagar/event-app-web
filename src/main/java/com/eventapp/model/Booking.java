package com.eventapp.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Represents a booking made by a user for one or more vendor services.
 */
public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private int userId;
    private Date eventDate;
    private String eventLocation;
    private String status; // e.g., "PENDING", "CONFIRMED", "CANCELLED"
    private double totalCost;
    private Date bookingDate;
    private List<Integer> vendorIds; // List of vendor IDs included in this booking
    
    // Default constructor
    public Booking() {
    }
    
    // Parameterized constructor
    public Booking(int id, int userId, Date eventDate, String eventLocation, 
                  String status, double totalCost, Date bookingDate, List<Integer> vendorIds) {
        this.id = id;
        this.userId = userId;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.status = status;
        this.totalCost = totalCost;
        this.bookingDate = bookingDate;
        this.vendorIds = vendorIds;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public Date getEventDate() {
        return eventDate;
    }
    
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }
    
    public String getEventLocation() {
        return eventLocation;
    }
    
    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public double getTotalCost() {
        return totalCost;
    }
    
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
    
    public Date getBookingDate() {
        return bookingDate;
    }
    
    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }
    
    public List<Integer> getVendorIds() {
        return vendorIds;
    }
    
    public void setVendorIds(List<Integer> vendorIds) {
        this.vendorIds = vendorIds;
    }
    
    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", userId=" + userId +
                ", eventDate=" + eventDate +
                ", eventLocation='" + eventLocation + '\'' +
                ", status='" + status + '\'' +
                ", totalCost=" + totalCost +
                ", bookingDate=" + bookingDate +
                '}';
    }
}
