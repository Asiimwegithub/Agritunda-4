package com.mcdenny.agritunda.ViewHolder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mcdenny.agritunda.Interface.ItemClickListener;
import com.mcdenny.agritunda.R;

public class AdminFoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    //admin
    public TextView adminProductItemName, adminProductItemPrice;
    public ImageView adminProductItemImage;
    public Button deleteProduct;
    public LinearLayout delete_layout;
    private ItemClickListener itemClickListener;

   public AdminFoodViewHolder(View itemView){
       super(itemView);

       //admin
       adminProductItemName = (TextView) itemView.findViewById(R.id.ad_item_name);
       delete_layout = itemView.findViewById(R.id.icon_delete);
       adminProductItemPrice = (TextView) itemView.findViewById(R.id.admin_item_price);
       adminProductItemImage = (ImageView) itemView.findViewById(R.id.admin_item_image);
       deleteProduct = (Button) itemView.findViewById(R.id.admin_delete_product);
       itemView.setOnClickListener(this);
   }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(itemView, getAdapterPosition(), false);
    }
}
