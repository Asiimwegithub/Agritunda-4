package com.mcdenny.agritunda.admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mcdenny.agritunda.Interface.ItemClickListener;
import com.mcdenny.agritunda.Model.Distributor;
import com.mcdenny.agritunda.R;
import com.mcdenny.agritunda.ViewHolder.DistributorViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import mehdi.sakout.fancybuttons.FancyButton;

public class AdminDistributorActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    TextView imageSelected;

    FirebaseDatabase database;
    DatabaseReference distributorList;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseRecyclerAdapter<Distributor, DistributorViewHolder> adapter;

    //for the add distributor alert dialog
    MaterialEditText addDistName, addDistPhone, addDistEmail, addDistAddress;
    FancyButton  uploadDistImage;
    Button selectDistImage;
    Distributor newDistributor;
    Uri saveUri;
    private final int PICK_IMAGE_REQUEST = 71;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_distributor);
        setTitle("Distributors");

        //init firebase
        database = FirebaseDatabase.getInstance();
        distributorList = database.getReference("Distributor");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        FloatingActionButton fab = findViewById(R.id.fab_add_distributor);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDistributorDialog();
            }
        });

        //inflate the layout
        recyclerView = findViewById(R.id.recycler_admin_distributor);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadDistributorList();
    }

    //adding a new distributor in an alertdialog
    private void showAddDistributorDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminDistributorActivity.this);
        alertDialog.setTitle("Add new distributor");
        alertDialog.setMessage("Please fill in the information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_distributor_layout, null);

        addDistName = add_menu_layout.findViewById(R.id.add_distributor_name);
        addDistPhone = add_menu_layout.findViewById(R.id.add_distributor_phone);
        addDistEmail = add_menu_layout.findViewById(R.id.add_distributor_email);
        addDistAddress = add_menu_layout.findViewById(R.id.add_distributor_address);
        imageSelected = add_menu_layout.findViewById(R.id.img_distri_selected);
        selectDistImage = add_menu_layout.findViewById(R.id.select_distributor_image);

        //adding onclick event on the buttons
        selectDistImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //validateFields();
                if (addDistName.getText().toString().isEmpty()) {
                    addDistName.setError("You must fill in this field!");
                    addDistName.requestFocus();
                }
                else if(addDistPhone.getText().toString().isEmpty()) {
                    addDistPhone.setError("You must fill in this field!");
                    addDistPhone.requestFocus();
                }
                else if(addDistEmail.getText().toString().isEmpty()) {
                    addDistEmail.setError("You must fill in this field!");
                    addDistEmail.requestFocus();
                }
                else if(addDistAddress.getText().toString().isEmpty()) {
                    addDistAddress.setError("You must fill in this field!");
                    addDistAddress.requestFocus();
                }
                else{
                    chooseImage();//let the user choose the image and save its uri
                }

            }
        });

        alertDialog.setView(add_menu_layout);
        //alertDialog.setIcon(R.drawable.ic_cart_plus);

        //setting the buttons
        alertDialog.setPositiveButton("UPLOAD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               // dialogInterface.dismiss();
                uploadImage();
                //Creating a new category
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }//end of alert dialog

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);

    }

    private void uploadImage() {
        if (saveUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading....");
            progressDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Image uploaded succesfully",
                                    Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //set a value for a new distributor if image uploaded, and then get the download link
                                    //newDistributor = new Distributor(addDistName.getText().toString(), uri.toString());
                                    newDistributor = new Distributor(
                                            addDistName.getText().toString(),
                                            addDistPhone.getText().toString(),
                                            addDistEmail.getText().toString(),
                                            addDistAddress.getText().toString(),
                                            uri.toString());

                                    distributorList.push().setValue(newDistributor);
                                    Toast.makeText(getApplicationContext(), "New Distributor succesfully",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AdminDistributorActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded" + progress + "%");

                        }
                    });
        }
        else {
            Toast.makeText(this, "No distributor added, no image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            saveUri = data.getData();
            imageSelected.setText("Image selected!");
            imageSelected.setTextColor(this.getResources().getColor(R.color.colorPrimary));
        }
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

                    }
                });

            }
        };
        adapter.notifyDataSetChanged();//Refresh data if it has been changed
        //Setting the adapter
        recyclerView.setAdapter(adapter);
    }
}
