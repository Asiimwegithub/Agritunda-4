package com.mcdenny.agritunda.Model;

import java.util.List;

public class Requests {
    private String name;
    private String contact;
    private String address;
    private String distributorName;
    private String distributorPhone;
    private String received;
    private int total;
    private String status;
    private List<Order> orders;//List of orders

    public Requests(){

    }

    public Requests(String name, String contact, String address, int total, List<Order> orders) {
        this.name = name;
        this.contact = contact;
        this.address = address;
        this.distributorName = "Not assigned";
        this.distributorPhone = "Not assigned";
        this.total = total;
        this.status = "0";
        this.received = "0";
        this.orders = orders;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistributorName() {
        return distributorName;
    }

    public void setDistributorName(String distributorName) {
        this.distributorName = distributorName;
    }

    public String getDistributorPhone() {
        return distributorPhone;
    }

    public void setDistributorPhone(String distributorPhone) {
        this.distributorPhone = distributorPhone;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public String getReceived() {
        return received;
    }

    public void setReceived(String received) {
        this.received = received;
    }
}
