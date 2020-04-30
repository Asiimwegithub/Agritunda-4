package com.mcdenny.agritunda.ViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcdenny.agritunda.Interface.ItemClickListener;
import com.mcdenny.agritunda.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView cart_item_name, cart_item_price;
    public ImageView cart_item_count;
    public Button cart_delete;

    private ItemClickListener itemClickListener;

    public void setCart_item_name(TextView cart_item_name) {
        this.cart_item_name = cart_item_name;
    }

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        cart_item_name = (TextView) itemView.findViewById(R.id.cart_item_name);
        cart_item_price = (TextView) itemView.findViewById(R.id.cart_item_price);
        cart_item_count = (ImageView) itemView.findViewById(R.id.cart_item_count);
        cart_delete = (Button) itemView.findViewById(R.id.remove_cart_item);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
