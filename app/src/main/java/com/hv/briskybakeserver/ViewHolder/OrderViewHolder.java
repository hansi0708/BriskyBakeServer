package com.hv.briskybakeserver.ViewHolder;

import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hv.briskybakeserver.Interface.ItemClickListener;
import com.hv.briskybakeserver.R;

public class OrderViewHolder extends RecyclerView.ViewHolder
        {

        public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress;
        
        public Button btnEdit,btnRemove,btnDetail,btnDirection,map;

    //    private ItemClickListener itemClickListener;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId=itemView.findViewById(R.id.order_id);
            txtOrderStatus=itemView.findViewById(R.id.order_status);
            txtOrderPhone=itemView.findViewById(R.id.order_phone);
            txtOrderAddress=itemView.findViewById(R.id.order_address);

            btnEdit=itemView.findViewById(R.id.edit);
            btnRemove=itemView.findViewById(R.id.remove);
            btnDetail=itemView.findViewById(R.id.detail);
            map=itemView.findViewById(R.id.map);
           // btnDirection=itemView.findViewById(R.id.direction);


         //   itemView.setOnClickListener(this);
           // itemView.setOnLongClickListener(this);
            //itemView.setOnCreateContextMenuListener(this);

        }

}

