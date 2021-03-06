package com.shubham.tripin1.offeehandler.Model;

/**
 * Created by Tripin1 on 6/20/2017.
 */

public class CoffeeOrder {
    private String mCoffeeName;
    private String mCoffeeNumber;
    private String mOrderStatus;
    private String mItemPrice;


    public CoffeeOrder(){
        //for firebase
    }



    public CoffeeOrder(String mCoffeeName,String mCoffeeNumber,String mOrderStatus,String mItemPrice){
        this.mCoffeeName = mCoffeeName;
        this.mCoffeeNumber = mCoffeeNumber;
        this.mOrderStatus = mOrderStatus;
        this.mItemPrice = mItemPrice;
    }

    public String getmCoffeeName() {
        return mCoffeeName;
    }

    public void setmCoffeeName(String mCoffeeName) {
        this.mCoffeeName = mCoffeeName;
    }

    public String getmCoffeeNumber() {
        return mCoffeeNumber;
    }

    public void setmCoffeeNumber(String mCoffeeNumber) {
        this.mCoffeeNumber = mCoffeeNumber;
    }

    public String getmOrderStatus() {
        return mOrderStatus;
    }

    public void setmOrderStatus(String mOrderStatus) {
        this.mOrderStatus = mOrderStatus;
    }

    public String getmItemPrice() {
        return mItemPrice;
    }

    public void setmItemPrice(String mItemPrice) {
        this.mItemPrice = mItemPrice;
    }


}
