package com.eventapp.model;

import java.io.Serializable;

/**
 * Represents a vendor service provider in the Event Aggregator application.
 */
public class Vendor implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String name;
    private String type;        // Type of service (e.g. Catering, Photography)
    private String description;
    private String contactInfo;
    private double baseCost;
    private String imageUrl;    // Path to vendor profile/service image
    
    // Default constructor
    public Vendor() {
    }
    
    // Parameterized constructor
    public Vendor(int id, String name, String type, String description, 
                 String contactInfo, double baseCost, String imageUrl) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.contactInfo = contactInfo;
        this.baseCost = baseCost;
        this.imageUrl = imageUrl;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getContactInfo() {
        return contactInfo;
    }
    
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
    
    public double getBaseCost() {
        return baseCost;
    }
    
    public void setBaseCost(double baseCost) {
        this.baseCost = baseCost;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    @Override
    public String toString() {
        return "Vendor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                ", baseCost=" + baseCost +
                '}';
    }
}