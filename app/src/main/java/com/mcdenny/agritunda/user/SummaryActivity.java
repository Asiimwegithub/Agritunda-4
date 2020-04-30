package com.mcdenny.agritunda.user;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mcdenny.agritunda.Common.Common;
import com.mcdenny.agritunda.Common.Cons;
import com.mcdenny.agritunda.Common.Util;
import com.mcdenny.agritunda.Model.Order;
import com.mcdenny.agritunda.Model.Requests;
import com.mcdenny.agritunda.R;
import com.mcdenny.agritunda.database.Database;

import java.util.ArrayList;
import java.util.List;

public class SummaryActivity extends AppCompatActivity {
    TextView mName, mPhone, mAddress, mArea, mNote, tvGrandTotal, tvShippingFee;
    Button checkout;

    FirebaseDatabase database;
    DatabaseReference orders;
    DatabaseReference cart;

    private String mCustomerAddress;
    private String mCustomerArea;
    private String mCustomerName;
    private String mCustomerPhone;
    private String mDistName;
    private String mDistPhone;
    private int orderTotal;
    private ProgressDialog progressDialog;
    private double pay_first = 0.0;

    List<Order> cartOrderList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        setTitle("Summary");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);


        mAddress = (TextView) findViewById(R.id.tvAddress);
        mArea = (TextView) findViewById(R.id.tvArea);
        mName = (TextView) findViewById(R.id.tvname);
        mPhone = (TextView) findViewById(R.id.tvPhone);
        checkout = (Button) findViewById(R.id.confirm);
        mNote = (TextView) findViewById(R.id.tv_customer_note);
        //mDistributorPhone = (TextView) findViewById(R.id.tv_distributor_phone);
        tvGrandTotal = (TextView) findViewById(R.id.tvGrandTotal);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Placing Order");
        progressDialog.setMessage("Please wait...");

        //initialise firebase
        database = FirebaseDatabase.getInstance();
        orders = database.getReference("Requests");
        cart = database.getReference("Cart");
        final String orderKey = orders.push().getKey();

       // mDistName = Common.distributor_name;
       // mDistPhone = Common.distributor_phone;
        mCustomerArea = Common.city;
        mCustomerAddress = Common.address;
        mCustomerName = Common.admin_Current.getName();
        mCustomerPhone = Common.admin_Current.getPhone();



        mAddress.setText(mCustomerAddress);
        mArea.setText(mCustomerArea);
        mPhone.setText(mCustomerPhone);
        mName.setText(mCustomerName);

        pay_first = 0.25 * Common.totalCart;
        mNote.setText( "Dear customer, Please your are required to pay upon the arrival of your order");
        //mDistributorName.setText(mDistName);
        //mDistributorPhone.setText(mDistPhone);


        orderTotal = Common.totalCart;
        tvGrandTotal.setText(Cons.Vals.CURRENCY + Util.formatNumber(String.valueOf(orderTotal)));

        cartOrderList = Common.cartListOrder;

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setTitle("Submitting Order");
                progressDialog.show();

                final String addr = mCustomerArea + "," + mCustomerAddress;
                final Requests requests = new Requests(
                        mCustomerName,
                        mCustomerPhone,
                        addr,
                        orderTotal,
                        cartOrderList
                );
                //Sending the above data to firebase database

                orders.child(String.valueOf(System.currentTimeMillis())).setValue(requests).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            //Deletes the order cart
                            new Database(getBaseContext()).clearCart();
                            Intent intent = new Intent(SummaryActivity.this, Cart.class);
                            startActivity(intent);
                            Toast.makeText(SummaryActivity.this, "Successfully placed your order.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(SummaryActivity.this, "Failed to place the order.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }


}
