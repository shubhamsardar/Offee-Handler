package com.shubham.tripin1.offeehandler.Model;

/**
 * Created by Tripin1 on 7/6/2017.
 */

public class PostPaidPojo {
    private String name,mobile;
    private double amount;


    public PostPaidPojo(){
        //for firebase
    }

    public PostPaidPojo(String name, double amount,String mobile) {
        this.name = name;
        this.amount = amount;
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
