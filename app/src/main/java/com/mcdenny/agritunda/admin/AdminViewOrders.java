package com.mcdenny.agritunda.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mcdenny.agritunda.Common.Common;
import com.mcdenny.agritunda.Interface.ItemClickListener;
import com.mcdenny.agritunda.Model.Requests;
import com.mcdenny.agritunda.R;
import com.mcdenny.agritunda.ViewHolder.OrderViewHolder;
import com.mcdenny.agritunda.user.DistributorList;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.Locale;

import static com.mcdenny.agritunda.user.DistributorList.CHOOSE_DISTRIBUTOR;

public class AdminViewOrders extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Requests, OrderViewHolder> adapter;
    FirebaseDatabase db;
    DatabaseReference requests;
    Spinner spinner;
    MaterialEditText chooseDistributor;

    private String mDistributorName;
    private String mDistributorPhone;
    private String mDistributorAddress;

    private static final int PICK_DISTRIBUTOR = 23;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_orders);
        //firebase
        db = FirebaseDatabase.getInstance();
        requests = db.getReference("Requests");

        //init
        recyclerView = (RecyclerView) findViewById(R.id.admin_list_orders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders(); //load all the orders
    }


    private void loadOrders() {
        adapter = new FirebaseRecyclerAdapter<Requests, OrderViewHolder>(
                Requests.class,
                R.layout.layout_admin_order,
                OrderViewHolder.class,
                requests
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, final Requests model, final int position) {
                viewHolder.orderID.setText(adapter.getRef(position).getKey());

                viewHolder.orderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.orderAddress.setText(model.getAddress());
                String totals = String.valueOf(model.getTotal());
                viewHolder.orderPhone.setText(model.getContact());
                viewHolder.orderReceived.setText(Common.convertReceivedStatus(model.getReceived()));

                Locale locale = new Locale("en", "UG");
                NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
                int theRealTotal = Integer.parseInt(totals);
                viewHolder.orderTotal.setText(numberFormat.format(theRealTotal));
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Intent orderDetail = new Intent(OrderStatus.this, OrderDetail.class);
                       // Common.currentRequest = model;
                        //orderDetail.putExtra("OrderId", adapter.getRef(position).getKey());
                        //startActivity(orderDetail);
                    }
                });

                viewHolder.order_update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowUpdateDialog(adapter.getRef(position).getKey(), adapter.getItem(position));
                    }
                });

                viewHolder.order_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteOrder(adapter.getRef(position).getKey());
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void deleteOrder(String key) {
        requests.child(key).removeValue();
        adapter.notifyDataSetChanged();
    }

    private void pickDistributor() {

        Intent intent = new Intent(this, DistributorList.class);
        intent.putExtra(CHOOSE_DISTRIBUTOR, true);
        startActivityForResult(intent, PICK_DISTRIBUTOR);
    }

    private void ShowUpdateDialog(String key, final Requests item) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminViewOrders.this);
        alertDialog.setTitle("Update Order");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.layout_update_order,null);

        spinner = (Spinner)view.findViewById(R.id.statusSpinner);
        chooseDistributor = (MaterialEditText) view.findViewById(R.id.choose_distributor);
        chooseDistributor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDistributor();
            }
        });


        alertDialog.setView(view);

        final String localKey = key;
        alertDialog.setPositiveButton("UPDATE ORDER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedItemId()));
                item.setDistributorName(mDistributorName);
                item.setDistributorPhone(mDistributorPhone);

                requests.child(localKey).setValue(item); //add to update size
                adapter.notifyDataSetChanged();
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_DISTRIBUTOR && resultCode == RESULT_OK) {

            mDistributorName = data.getStringExtra("distributorName");
            mDistributorPhone = data.getStringExtra("distributorPhone");
            mDistributorAddress = data.getStringExtra("distributorAddress");

            chooseDistributor.setText(mDistributorName+ ", " +mDistributorPhone);

            Common.distributor_name = mDistributorName;
            Common.distributor_phone = mDistributorPhone;
        } else {
            Toast.makeText(this, "No distributor selected!....", Toast.LENGTH_SHORT).show();
        }
    }

}