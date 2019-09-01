package com.mehrab.bittrader.User;

public class Transaction {
    public String type_;
    public Double amount_;
    public Double btc_price_;

    public Transaction() {

    };

    public Transaction(String type, Double amount, Double btc_price) {
        this.type_ = type;
        this.amount_ = amount;
        this.btc_price_ = btc_price;
    }
}
