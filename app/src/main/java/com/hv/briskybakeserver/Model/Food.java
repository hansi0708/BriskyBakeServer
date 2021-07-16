package com.hv.briskybakeserver.Model;

import java.util.ArrayList;
import java.util.List;

public class Food {
    private String Name,Image,Description,Price,Discount,MenuId,MenuValue;
    private List<String> Unit;

    public Food()
    {

    }

    public Food(String name, String image, String description, String price, String discount, String menuId, String menuValue) {
        Name = name;
        Image = image;
        Description = description;
        Price = price;
        Discount = discount;
        MenuId = menuId;
        MenuValue = menuValue;
    }

    public Food(String name, String image, String description, String price, String discount, String menuId, String menuValue, List<String> unit) {
        Name = name;
        Image = image;
        Description = description;
        Price = price;
        Discount = discount;
        MenuId = menuId;
        MenuValue = menuValue;
        Unit = unit;
    }

    public List<String> getUnit() {
        return Unit;
    }

    public void setUnit(List<String> unit) {
        Unit = unit;
    }

    public String getMenuValue() {
        return MenuValue;
    }

    public void setMenuValue(String menuValue) {
        MenuValue = menuValue;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }
}

