package com.mehrab.bittrader.User;

import java.util.ArrayList;
import java.util.List;

public class UserInformation {
    public String username_;
    public double btcBalance_;
    public double usdBalance_;
    public double accountValue_;
    public double maxValueReached_;
    public List<Transaction> transactions_;

    public UserInformation() {
        transactions_ = new ArrayList<Transaction>();
    }

    public UserInformation(String username, double btcBalance, double usdBalance,
                           double accountValue, double maxValueReached, List<Transaction> transactions) {
        username_ = username;
        btcBalance_ = btcBalance;
        usdBalance_ = usdBalance;
        accountValue_ = accountValue;
        maxValueReached_ = maxValueReached;
        transactions_ = transactions;
    }

    public void addTransaction(Transaction t) {
        transactions_.add(t);
    }
}
