package com.mcdenny.agritunda.user;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mcdenny.agritunda.Common.Common;
import com.mcdenny.agritunda.R;
import com.rengwuxian.materialedittext.MaterialEditText;

public class AddressActivity extends AppCompatActivity {
    MaterialEditText etRegion, etCity, etAddress;
    String add_region, add_city, add_address;
    Button address_done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        setTitle("Your Address");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        etRegion = findViewById(R.id.address_region);
        etCity = findViewById(R.id.address_city);
        etAddress = findViewById(R.id.address_address);
        address_done = (Button) findViewById(R.id.btn_proceed);


        address_done.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //checking whether the edit text is empty
                if (etRegion.getText().toString().isEmpty()) {
                    etRegion.setError("field can't be empty");
                    etRegion.requestFocus();
                } else if (etCity.getText().toString().isEmpty()) {
                    etCity.setError("field can't be empty");
                    etCity.requestFocus();
                } else if (etAddress.getText().toString().isEmpty()) {
                    etAddress.setError("field can't be empty");
                    etAddress.requestFocus();
                } else {
                    add_region = etRegion.getText().toString();
                    add_city = etCity.getText().toString();
                    add_address = etAddress.getText().toString();

                    Common.region = add_region;
                    Common.city = add_city;
                    Common.address = add_address;
                    Intent summaryIntent = new Intent(AddressActivity.this, SummaryActivity.class);
                    startActivity(summaryIntent);
                    finish();
                }
            }
        });
    }
}
