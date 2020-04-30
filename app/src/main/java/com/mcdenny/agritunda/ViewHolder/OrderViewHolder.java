package com.mcdenny.agritunda.ViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcdenny.agritunda.Interface.ItemClickListener;
import com.mcdenny.agritunda.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtOrderId, txtOrderPhone, txtOrderStatus, txtOrderAddress, txtOrderDistributor, txtHasArrived;
    public ImageView imgReceived;
    private ItemClickListener itemClickListener;

    //for admin
    public TextView orderID, orderStatus, orderPhone, orderAddress, orderTotal, orderReceived;
    public ImageView order_update, order_delete;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        txtOrderId = (TextView) itemView.findViewById(R.id.order_id);
        txtOrderPhone = (TextView) itemView.findViewById(R.id.order_phone);
        txtOrderStatus = (TextView) itemView.findViewById(R.id.order_status);
        txtOrderAddress = (TextView) itemView.findViewById(R.id.order_address);
        txtOrderDistributor = (TextView) itemView.findViewById(R.id.order_distributor);
        txtHasArrived = (TextView) itemView.findViewById(R.id.order_arrival);
        imgReceived = (ImageView) itemView.findViewById(R.id.edit_recieved);

        //for admin
        orderID = (TextView) itemView.findViewById(R.id.admin_order_id);
        orderStatus = (TextView) itemView.findViewById(R.id.admin_order_status);
        orderPhone = (TextView) itemView.findViewById(R.id.admin_order_phone);
        orderAddress = (TextView) itemView.findViewById(R.id.admin_order_address);
        orderTotal = (TextView) itemView.findViewById(R.id.admin_order_total);
        order_update = (ImageView) itemView.findViewById(R.id.admin_order_update);
        orderReceived = (TextView) itemView.findViewById(R.id.order_status_received);
        order_delete = (ImageView) itemView.findViewById(R.id.admin_order_delete);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
