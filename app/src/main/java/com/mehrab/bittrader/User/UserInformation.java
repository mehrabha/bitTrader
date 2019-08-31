package com.mehrab.bittrader.User;

public class UserInformation {
    public double btcBalance;
    public double usdBalance;
    public double maxValueReached;

    public UserInformation() {

    }

    public UserInformation(double btcBalance, double usdBalance, double maxValueReached) {
        this.btcBalance = btcBalance;
        this.usdBalance = usdBalance;
        this.maxValueReached = maxValueReached;
    }
}
