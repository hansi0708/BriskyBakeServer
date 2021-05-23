package com.hv.briskybakeserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.MapView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hv.briskybakeserver.Common.Common;
import com.hv.briskybakeserver.Interface.ItemClickListener;
import com.hv.briskybakeserver.Model.MyResponse;
import com.hv.briskybakeserver.Model.Notification;
import com.hv.briskybakeserver.Model.Order;
import com.hv.briskybakeserver.Model.Request;
import com.hv.briskybakeserver.Model.Sender;
import com.hv.briskybakeserver.Model.Token;
import com.hv.briskybakeserver.Remote.APIService;
import com.hv.briskybakeserver.ViewHolder.OrderViewHolder;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hv.briskybakeserver.Common.Common.currentRequest;
import static com.hv.briskybakeserver.Common.Common.currentUser;

public class OrderStatus extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;

    MaterialSpinner spinner;
    
    MapView mapview;

    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //Firebase
        database=FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");

        //Init service
        mService=Common.getFCMService();

        recyclerView=findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


            loadOrders();


    }

    private void loadOrders() {
        FirebaseRecyclerOptions<Request> options=new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(requests,Request.class)
                .build();
        adapter=new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull Request model) {
                holder.txtOrderId.setText(adapter.getRef(position).getKey());
                holder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                holder.txtOrderAddress.setText(model.getAddress());
                holder.txtOrderPhone.setText(model.getPhone());

                holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUpdateDialog(adapter.getRef(position).getKey(),adapter.getItem(position));
                    }
                });

                holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteOrder(adapter.getRef(position).getKey());
                    }
                });

                holder.btnDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent orderDetail=new Intent(OrderStatus.this,OrderDetail.class);
                        Common.currentRequest=model;
                        orderDetail.putExtra("OrderId",adapter.getRef(position).getKey());
                        startActivity(orderDetail);
                    }
                });

                holder.btnDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
               //         Intent trackingOrder=new Intent(OrderStatus.this,TrackingOrder.class);
              //          currentRequest=model;
                 //       startActivity(trackingOrder);
                    }
                });

            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout,parent,false);
                return new OrderViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }


    private void deleteOrder(String key) {
        requests.child(key).removeValue();
        adapter.notifyDataSetChanged();
    }

    private void showUpdateDialog(String key, Request item) {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(OrderStatus.this);
        alertDialog.setTitle("Update Orders");
        alertDialog.setMessage("Choose status :");

        LayoutInflater inflater=this.getLayoutInflater();
        final View view=inflater.inflate(R.layout.update_order_layout,null);

        spinner=view.findViewById(R.id.statusSpinner);
        spinner.setItems("Placed","On the Way","Shipped");

        alertDialog.setView(view);

        final String localKey=key;
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));

                requests.child(localKey).setValue(item);
                adapter.notifyDataSetChanged();
                sendOrderStatusToUser(localKey,item);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void sendOrderStatusToUser(final String key,final Request item) {
        DatabaseReference tokens=database.getReference("Tokens");
        tokens.orderByKey().equalTo(item.getPhone())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot:snapshot.getChildren())
                        {
                            Token token=postSnapshot.getValue(Token.class);

                            //Make raw payload
                            Notification notification=new Notification("BriskyBake","Your order"+key+"was updated");
                            Sender content=new Sender(token.getToken(),notification);

                            mService.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.body().success==1)
                                            {
                                                Toast.makeText(OrderStatus.this, "Order was updated!", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                Toast.makeText(OrderStatus.this, "Order was updated but could not send notification", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("ERROR",t.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}