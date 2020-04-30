package com.mcdenny.agritunda.admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mcdenny.agritunda.Common.Common;
import com.mcdenny.agritunda.Interface.ItemClickListener;
import com.mcdenny.agritunda.Model.Product;
import com.mcdenny.agritunda.R;
import com.mcdenny.agritunda.ViewHolder.AdminFoodViewHolder;
import com.mcdenny.agritunda.user.Cart;
import com.mcdenny.agritunda.user.ProductDetail;
import com.mcdenny.agritunda.user.ProductList;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

import mehdi.sakout.fancybuttons.FancyButton;

public class AdminHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView currentAdminName;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionButton fab;

    //firebase
    FirebaseDatabase db;
    DatabaseReference produce;
    FirebaseStorage storage;
    StorageReference storageReference;

    MaterialEditText edtName, editDescription, editPrice, editDiscount;
    FancyButton  btnUploadImage;
    Button btnSelectImage;
    TextView imageSelected;
    Product newProduct;
    Uri saveUri;
    AlertDialog.Builder alertDialog;
    ProgressDialog progressDialog;

    FirebaseRecyclerAdapter<Product, AdminFoodViewHolder> productAdapter;
    String category_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_admin);
        //setSupportActionBar(toolbar);
        toolbar.setTitle("Farmers Produce ");

        //categoryId = getIntent().getStringExtra("CategoryId");
        alertDialog = new AlertDialog.Builder(AdminHomeActivity.this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");

        //firebase
        db = FirebaseDatabase.getInstance();
        produce = db.getReference("produce");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //init
        recyclerView = (RecyclerView) findViewById(R.id.admin_recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        fab = (FloatingActionButton) findViewById(R.id.admin_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddProductDialog();
            }
        });

        //Initializing the navigation drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_admin_view);
        navigationView.setNavigationItemSelectedListener(this);

        //showing the users full name on the header
        View headerView = navigationView.getHeaderView(0);
        currentAdminName = headerView.findViewById(R.id.adminFullName);
        currentAdminName.setText(Common.admin_Current.getName());

        loadListProduct();

    }

    private void loadListProduct() {
        progressDialog.setMessage("Loading food....");
        progressDialog.show();
        productAdapter = new FirebaseRecyclerAdapter<Product, AdminFoodViewHolder>(
                Product.class,
                R.layout.layout_admin_product_list,
                AdminFoodViewHolder.class,
                produce
        ) {
            @Override
            protected void populateViewHolder(AdminFoodViewHolder viewHolder, Product model, final int position) {
                viewHolder.adminProductItemName.setText(model.getName());

                Locale locale = new Locale("en", "UG");
                NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
                int thePrice = (Integer.parseInt(model.getPrice()));
                viewHolder.adminProductItemPrice.setText(numberFormat.format(thePrice));

                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.adminProductItemImage);

                if(!model.getUserid().equals(Common.admin_Current.getPhone())){
                    viewHolder.delete_layout.setVisibility(View.GONE);
                }

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClicked) {
                        Intent productList = new Intent(AdminHomeActivity.this, ProductDetail.class);
                        //Getting the category key id and sending it to the product list activity
                        productList.putExtra("produce_id", productAdapter.getRef(position).getKey());
                        startActivity(productList);
                    }
                });

                final String refKey = productAdapter.getRef(position).getKey();
                viewHolder.deleteProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteProduct(refKey);
                        productAdapter.notifyDataSetChanged();
                        productAdapter.notifyItemRemoved(position);
                    }
                });

                progressDialog.dismiss();
            }
        };
        productAdapter.notifyDataSetChanged();//Refresh data if it has been changed
        //Setting the adapter
        recyclerView.setAdapter(productAdapter);
    }

    private  void deleteProduct(String key){
        produce.child(key).removeValue();
    }

    private void showAddProductDialog() {

        //final AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminFoodList.this);
        alertDialog.setTitle("Add new food");
        alertDialog.setMessage("Please fill in the information");
        alertDialog.setCancelable(false);

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_product_layout, null);

        edtName = add_menu_layout.findViewById(R.id.editName);
        editDescription = add_menu_layout.findViewById(R.id.editDescription);
        editDiscount = add_menu_layout.findViewById(R.id.editDiscount);
        editPrice = add_menu_layout.findViewById(R.id.editPrice);
        imageSelected = add_menu_layout.findViewById(R.id.img_selected);
        btnSelectImage = add_menu_layout.findViewById(R.id.select_product_image);


        //adding onclick event on the buttons
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //validateFields();
                if (edtName.getText().toString().isEmpty()) {
                    edtName.setError("You must fill in this field!");
                    edtName.requestFocus();
                }
                else if(editDescription.getText().toString().isEmpty()) {
                    editDescription.setError("You must fill in this field!");
                    editDescription.requestFocus();
                }
                else if(editDiscount.getText().toString().isEmpty()) {
                    editDiscount.setError("You must fill in this field!");
                    editDiscount.requestFocus();
                }
                else if(editPrice.getText().toString().isEmpty()) {
                    editPrice.setError("You must fill in this field!");
                    editPrice.requestFocus();
                }
                else {
                    chooseImage();//let the user choose the image and save its uri
                }

            }
        });

        alertDialog.setView(add_menu_layout);


        //setting the buttons
        alertDialog.setPositiveButton("UPLOAD FOOD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                uploadImage();
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


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), Common.PICK_IMAGE_REQUEST);


    }

    private void uploadImage() {
        if (saveUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Please wait..");
            progressDialog.setMessage("Uploading....");
            progressDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("foods/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            //Toast.makeText(getApplicationContext(), "Image uploaded succesfully",Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //set a value for a new category if image uploaded, and then get the download link
                                    newProduct = new Product();
                                    newProduct.setDescription(editDescription.getText().toString());
                                    newProduct.setDiscount(editDiscount.getText().toString());
                                    newProduct.setImage(uri.toString());
                                    newProduct.setUserid(Common.admin_Current.getPhone());
                                    newProduct.setName(edtName.getText().toString());
                                    newProduct.setPrice(editPrice.getText().toString());
                                    //adding new food to the database
                                    produce.push().setValue(newProduct);
                                    Toast.makeText(AdminHomeActivity.this, "New Product " + newProduct.getName() + " has been added", Toast.LENGTH_SHORT).show();
                                    /*alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            dialog.dismiss();
                                        }
                                    });*/

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AdminHomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploading " + progress + "%");

                        }
                    });
        }
        else {
            Toast.makeText(this, "Food not added! No image was selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            saveUri = data.getData();
            imageSelected.setText("Image Selected");
            imageSelected.setTextColor(this.getResources().getColor(R.color.colorPrimary));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.admin_nav_home) {
            // Handle the camera action
        }
        else if (id == R.id.admin_nav_orders) {
            intent = new Intent(AdminHomeActivity.this, AdminViewOrders.class);
            startActivity(intent);

        }
        else if (id == R.id.admin_nav_cart) {
            startActivity(new Intent(getApplicationContext(), Cart.class));
        }
        else if (id == R.id.admin_nav_distributors) {
            intent = new Intent(AdminHomeActivity.this, AdminDistributorActivity.class);
            startActivity(intent);

        }else if (id == R.id.admin_nav_settings) {
            Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.admin_nav_logout) {
            //firebaseAuth.signOut();
            intent = new Intent(AdminHomeActivity.this, AdminLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Toast.makeText(AdminHomeActivity.this, "Thanks for visiting!\nSee you soon.", Toast.LENGTH_SHORT)
                    .show();
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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
        } else if (id == R.id.action_cart){
            startActivity(new Intent(getApplicationContext(), Cart.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
