package com.example.notepad.Models;

public class ExpenditureModel {
    String month;
    String serialNo;
    String totalAmount;

    public String getMonth() {
        return month;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public ExpenditureModel(String serialNo , String month, String totalAmount) {
        this.month = month;
        this.serialNo = serialNo;
        this.totalAmount = totalAmount;
    }
}
