package com.mcdenny.agritunda.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mcdenny.agritunda.Interface.ItemClickListener;
import com.mcdenny.agritunda.Model.Requests;
import com.mcdenny.agritunda.R;
import com.mcdenny.agritunda.ViewHolder.OrderViewHolder;

public class OrderStatus extends AppCompatActivity {
    public RecyclerView orderRecylerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;
    Spinner spinner;

    FirebaseRecyclerAdapter<Requests, OrderViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        setTitle("Your Orders");

        //init firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        orderRecylerView = (RecyclerView) findViewById(R.id.list_orders);
        orderRecylerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        orderRecylerView.setLayoutManager(layoutManager);

        loadOrderStatus();
    }

    private void loadOrderStatus() {
        adapter = new FirebaseRecyclerAdapter<Requests, OrderViewHolder>(
                Requests.class,
                R.layout.layout_order,
                OrderViewHolder.class,
                requests
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, final Requests model, final int position) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderPhone.setText(model.getContact());
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtHasArrived.setText(convertReceivedStatus(model.getReceived()));
                viewHolder.txtOrderStatus.setText(convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderDistributor.setText(model.getDistributorName() + "," +model.getDistributorPhone());

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClicked) {

                    }
                });

                viewHolder.imgReceived.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUpdateDialog(adapter.getRef(position).getKey(), adapter.getItem(position));
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        orderRecylerView.setAdapter(adapter);
    }

    private void showUpdateDialog(String key, final Requests item) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus.this);
        alertDialog.setTitle("Update Order");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.layout_update_customer_order,null);

        spinner = (Spinner)view.findViewById(R.id.spinner_received);

        alertDialog.setView(view);

        final String localKey = key;
        alertDialog.setPositiveButton("UPDATE ORDER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                item.setReceived(String.valueOf(spinner.getSelectedItemId()));

                requests.child(localKey).setValue(item); //add to update size
                adapter.notifyDataSetChanged();
                loadOrderStatus();
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

    private String convertCodeToStatus(String status) {
        if(status.equals("0")){
            return "Placed";
        }
        else if(status.equals("1")){
            return "Still in process";
        }
        else {
            return "Shipped";
        }
    }

    public static String convertReceivedStatus(String check) {
        if(check.equals("0")){
            return "Order not received";
        }
        else{
            return "Order Received";
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Connect with me on facebook via: www.facebook.com/denislucaz");
            startActivity(Intent.createChooser(shareIntent, "Send Invite Via"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
