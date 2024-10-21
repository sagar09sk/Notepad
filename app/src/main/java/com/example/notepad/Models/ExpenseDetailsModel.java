package com.example.notepad.Models;

public class ExpenseDetailsModel {
 String serial;
 String date;
 String expenseTitle;
 String expenseAmount;

    public String getSerial() {
        return serial;
    }

    public String getDate() {
        return date;
    }

    public String getExpenseTitle() {
        return expenseTitle;
    }

    public String getExpenseAmount() {
        return expenseAmount;
    }

    public ExpenseDetailsModel(String serial, String date, String expenseTitle, String expenseAmount) {
        this.serial = serial;
        this.date = date;
        this.expenseTitle = expenseTitle;
        this.expenseAmount = expenseAmount;
    }
}
