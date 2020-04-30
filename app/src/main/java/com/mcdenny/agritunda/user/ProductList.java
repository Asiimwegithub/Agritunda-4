package com.mcdenny.agritunda.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mcdenny.agritunda.Interface.ItemClickListener;
import com.mcdenny.agritunda.Model.Product;
import com.mcdenny.agritunda.R;
import com.mcdenny.agritunda.ViewHolder.ProductViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

import mehdi.sakout.fancybuttons.FancyButton;

public class ProductList extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference productItemList;
    FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter;
    MaterialEditText edtName, editDescription, editPrice, editDiscount;
    Button btnSelectImage, btnCapture;
    FancyButton btnUploadImage;
    Product newProduct;
    FloatingActionButton fab;
    StorageReference storageReference;
    FirebaseStorage storage;
    CoordinatorLayout rootLayout;
    Uri saveUri, saveCameraUri;
    ProgressDialog progressDialog;

    String categoryId = "";
    String toolbarTitle ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        toolbarTitle = getIntent().getStringExtra("CategoryName");
        setTitle("List of "+ toolbarTitle);

        progressDialog = new ProgressDialog(this);

        //firebase init
        database = FirebaseDatabase.getInstance();
        productItemList = database.getReference("Products");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);

        recyclerView = findViewById(R.id.recycler_product);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);


        //getting the intent from the category activity
        if(getIntent() != null){
            categoryId = getIntent().getStringExtra("CategoryId");
        }
        if(!categoryId.isEmpty() && categoryId != null) {
            loadProductItemList(categoryId);
        }

    }

    private void loadProductItemList(String categoryId) {
        progressDialog.setMessage("Loading food....");
        progressDialog.show();
        adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(
                Product.class,
                R.layout.layout_product_list,
                ProductViewHolder.class,
                productItemList.orderByChild("menuid").equalTo(categoryId)
        ) {
            @Override
            protected void populateViewHolder(ProductViewHolder viewHolder, Product model, int position) {
                viewHolder.productItemName.setText(model.getName());

                Locale locale = new Locale("en", "UG");
                NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
                int thePrice = (Integer.parseInt(model.getPrice()));
                viewHolder.productItemPrice.setText(numberFormat.format(thePrice));

                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.productItemImage);

                final Product productItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClicked) {
                        Intent productList = new Intent(ProductList.this, ProductDetail.class);
                        //Getting the category key id and sending it to the product list activity
                        productList.putExtra("ProductListID", adapter.getRef(position).getKey());
                        startActivity(productList);
                    }
                });
                progressDialog.dismiss();
            }
        };
        adapter.notifyDataSetChanged();//Refresh data if it has been changed
        //Setting the adapter
        recyclerView.setAdapter(adapter);
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
        if (id == R.id.action_cart) {
            startActivity(new Intent(ProductList.this, Cart.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
