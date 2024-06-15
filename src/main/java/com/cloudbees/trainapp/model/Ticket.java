package com.cloudbees.trainapp.model;

public class Ticket {

    private String from;
    private String to;
    private User user;
    private double price;
    private String seatSection;

    public Ticket(String from, String to, User user, double price, String seatSection) {
        this.from = from;
        this.to = to;
        this.user = user;
        this.price = price;
        this.seatSection = seatSection;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSeatSection() {
        return seatSection;
    }

    public void setSeatSection(String seatSection) {
        this.seatSection = seatSection;
    }
}
