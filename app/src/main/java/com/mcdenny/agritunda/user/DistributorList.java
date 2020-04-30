package com.mcdenny.agritunda.user;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mcdenny.agritunda.Interface.ItemClickListener;
import com.mcdenny.agritunda.Model.Distributor;
import com.mcdenny.agritunda.R;
import com.mcdenny.agritunda.ViewHolder.DistributorViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import mehdi.sakout.fancybuttons.FancyButton;

public class DistributorList extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference distributorList;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseRecyclerAdapter<Distributor, DistributorViewHolder> adapter;

    //for the add distributor alert dialog
    MaterialEditText addDistName, addDistPhone, addDistEmail, addDistAddress;
    FancyButton selectDistImage, uploadDistImage;
    Distributor newDistributor;
    Uri saveUri;
    private final int PICK_IMAGE_REQUEST = 71;

    public static final String CHOOSE_DISTRIBUTOR = "chooseDistributor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor_list);
        setTitle("Delivery Company");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //init firebase
        database = FirebaseDatabase.getInstance();
        distributorList = database.getReference("Distributor");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //inflate the layout
        recyclerView = findViewById(R.id.recycler_distributor);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadDistributorList();
    }

        private void loadDistributorList() {
        adapter = new FirebaseRecyclerAdapter<Distributor, DistributorViewHolder>(
                Distributor.class,
                R.layout.layout_distributor_list,
                DistributorViewHolder.class,
                distributorList
        ) {
            @Override
            protected void populateViewHolder(DistributorViewHolder viewHolder, final Distributor model, int position) {
                viewHolder.distName.setText(model.getName());
                viewHolder.distAddress.setText(model.getAddress());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.distImage);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClicked) {

                        if(DistributorList.this.getIntent().hasExtra(CHOOSE_DISTRIBUTOR)
                                && DistributorList.this.getIntent().getExtras().getBoolean(CHOOSE_DISTRIBUTOR)){

                            Intent intent = new Intent();
                            intent.putExtra("distributorName", model.getName());
                            intent.putExtra("distributorPhone", model.getPhone());
                            intent.putExtra("distributorAddress", model.getAddress());
                            DistributorList.this.setResult(RESULT_OK, intent);
                            DistributorList.this.finish();

                            return;
                        }
                    }
                });



            }
        };
        adapter.notifyDataSetChanged();//Refresh data if it has been changed
        //Setting the adapter
        recyclerView.setAdapter(adapter);
    }

}
