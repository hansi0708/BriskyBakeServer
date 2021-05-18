package com.hv.briskybakeserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.hv.briskybakeserver.Common.Common;
import com.hv.briskybakeserver.ViewHolder.OrderDetailAdapter;

public class OrderDetail extends AppCompatActivity {

    TextView order_id,order_phone,order_address,order_total,order_comment;
    String order_value_id="";
    RecyclerView listFoods;
    RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        order_id=findViewById(R.id.order_id);
        order_phone=findViewById(R.id.order_phone);
        order_total=findViewById(R.id.order_total);
        order_address=findViewById(R.id.order_address);
        order_comment=findViewById(R.id.order_comment);

        listFoods=findViewById(R.id.lstFoods);
        listFoods.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        listFoods.setLayoutManager(layoutManager);

        if (getIntent()!=null)
        {
            order_value_id=getIntent().getStringExtra("OrderId");
        }

        order_id.setText(order_value_id);
        order_phone.setText(Common.currentRequest.getPhone());
        order_total.setText(Common.currentRequest.getTotal());
        order_address.setText(Common.currentRequest.getAddress());
        order_comment.setText(Common.currentRequest.getComments());

        OrderDetailAdapter adapter=new OrderDetailAdapter(Common.currentRequest.getFoods());
        adapter.notifyDataSetChanged();
        listFoods.setAdapter(adapter);

    }
}