package com.hv.briskybakeserver.ViewHolder;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hv.briskybakeserver.MapsActivity;
import com.hv.briskybakeserver.Model.Order;
import com.hv.briskybakeserver.OrderDetail;
import com.hv.briskybakeserver.R;

import java.util.List;

class MyViewHolder extends RecyclerView.ViewHolder{

    public TextView product_name,quantity,price,discount,unit;
public Button map;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        product_name=itemView.findViewById(R.id.product_name);
        quantity=itemView.findViewById(R.id.product_quantity);
        price=itemView.findViewById(R.id.product_price);
        discount=itemView.findViewById(R.id.product_discount);
        unit=itemView.findViewById(R.id.product_unit);

    }
}

public class OrderDetailAdapter extends RecyclerView.Adapter<MyViewHolder>{

    List<Order> myOrders;
    public OrderDetailAdapter(List<Order> myOrders) {
        this.myOrders = myOrders;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_layout,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order order=myOrders.get(position);
        holder.product_name.setText(String.format("Name : %s",order.getProductName()));
        holder.quantity.setText(String.format("Quantity : %s",order.getQuantity()));
        holder.price.setText(String.format("Price : %s",order.getPrice()));
        holder.discount.setText(String.format("Discount : %s",order.getDiscount()));
        holder.unit.setText(String.format("%s (%s)",order.getOrderUnit(),order.getOrderValue()));

    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }
}
