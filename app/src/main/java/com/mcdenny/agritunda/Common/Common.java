package com.mcdenny.agritunda.Common;

import com.mcdenny.agritunda.Model.Admin;
import com.mcdenny.agritunda.Model.Order;
import com.mcdenny.agritunda.Model.User;

import java.util.ArrayList;
import java.util.List;

public class Common {
    //This class saves the current user details
    public static User user_Current;
    public static Admin admin_Current;

    public static final int PICK_IMAGE_REQUEST = 71;
    public static final int PICK_CAMERA_IMAGE = 82;

    //Store current selected address
    public static String region;
    public static String  city;
    public static String  address;

    //Store selected distributor
    public static String distributor_name;
    public static String distributor_phone;

    //Store the users cart
    public static  List<Order> cartListOrder = new ArrayList<>();
    public static int totalCart = 0;

    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";




    public static String convertCodeToStatus(String status) {
        if(status.equals("0")){
            return "Placed";
        }
        else if(status.equals("1")){
            return "Item is being processed";
        }
        else {
            return "Shipped";
        }
    }

    public static String convertReceivedStatus(String status) {
        if(status.equals("0")){
            return "Order not received";
        }
        else{
            return "Order Received";
        }

    }
}
