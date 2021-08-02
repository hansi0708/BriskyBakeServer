package com.hv.briskybakeserver.Model;

public class Rating {

    private String userPhone;
    private String name;
    private String foodId;
    private String rateValue;
    private String comment;

    public Rating() {
    }

    public Rating(String userPhone, String name, String foodId, String rateValue, String comment) {
        this.userPhone = userPhone;
        this.name = name;
        this.foodId = foodId;
        this.rateValue = rateValue;
        this.comment = comment;
    }

    public Rating(String userPhone, String foodId, String rateValue, String comment) {
        this.userPhone = userPhone;
        this.foodId = foodId;
        this.rateValue = rateValue;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
