package com.hv.briskybakeserver.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hv.briskybakeserver.Interface.ItemClickListener;
import com.hv.briskybakeserver.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

        public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress;

        private ItemClickListener itemClickListener;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            txtOrderId=itemView.findViewById(R.id.order_id);
            txtOrderStatus=itemView.findViewById(R.id.order_status);
            txtOrderPhone=itemView.findViewById(R.id.order_phone);
            txtOrderAddress=itemView.findViewById(R.id.order_address);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v,getAbsoluteAdapterPosition(),false);
        }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select the Action");
        menu.add(0,0,getAbsoluteAdapterPosition(),"Update");
        menu.add(0,1,getAbsoluteAdapterPosition(),"Delete");

    }
}

