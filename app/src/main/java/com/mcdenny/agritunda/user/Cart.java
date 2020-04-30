package com.mcdenny.agritunda.user;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mcdenny.agritunda.Common.Common;
import com.mcdenny.agritunda.Interface.ItemClickListener;
import com.mcdenny.agritunda.Model.Order;
import com.mcdenny.agritunda.R;
import com.mcdenny.agritunda.ViewHolder.CartViewHolder;
import com.mcdenny.agritunda.database.Database;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.mcdenny.agritunda.user.DistributorList.CHOOSE_DISTRIBUTOR;

public class Cart extends AppCompatActivity {
    TextView totalPrice;
    FancyButton placeOrder;
    LinearLayout cartLayout;
    TextView emptyCart;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference orders;

    List<Order> cartOrder = new ArrayList<>();
    CartAdapter adapter;

   private int mTotal = 0;

    private static final int PICK_DISTRIBUTOR = 23;

    private String mDistributorName;
    private String mDistributorPhone;
    private String mDistributorAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        setTitle("Your Cart");

        totalPrice = (TextView) findViewById(R.id.total);
        placeOrder = (FancyButton) findViewById(R.id.place_order_btn);
        cartLayout = (LinearLayout) findViewById(R.id.cartLayout);
        emptyCart = (TextView) findViewById(R.id.emptyCart);

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cartOrder.size() > 0){
                    //pickDistributor();
                    Intent intent = new Intent(Cart.this, AddressActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(Cart.this, "Nothing in your cart", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //initialise firebase
        database = FirebaseDatabase.getInstance();
        orders = database.getReference("Requests");

        recyclerView = (RecyclerView) findViewById(R.id.cartList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadCartOrders();

    }

    private void pickDistributor() {

        Intent intent = new Intent(this, DistributorList.class);
        intent.putExtra(CHOOSE_DISTRIBUTOR, true);
        startActivityForResult(intent, PICK_DISTRIBUTOR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_DISTRIBUTOR && resultCode == RESULT_OK) {

            mDistributorName = data.getStringExtra("distributorName");
            mDistributorPhone = data.getStringExtra("distributorPhone");
            mDistributorAddress = data.getStringExtra("distributorAddress");

            Common.distributor_name = mDistributorName;
            Common.distributor_phone = mDistributorPhone;

           // viewAlertDialog();
        } else {
            Toast.makeText(this, "No distributor selected!....", Toast.LENGTH_SHORT).show();
        }
    }

    //method that is called when place order button is clicked
    private void viewAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("Selected Distributor");
        alertDialog.setMessage(
                "\nName: " + mDistributorName +
                        "\nContact: " + mDistributorPhone
        );

        alertDialog.setPositiveButton("CONTINUE TO ADDRESS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Cart.this, AddressActivity.class);
                startActivity(intent);
                finish();
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

    //sending the orders to the cart
    private void loadCartOrders() {
        cartOrder = new Database(this).getCart();
        adapter = new CartAdapter(cartOrder, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //calculating the total price
        int total = 0;
        for (Order order : cartOrder) {
            total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));


            cartLayout.setVisibility(View.VISIBLE);
            emptyCart.setVisibility(View.GONE);

            Common.cartListOrder = cartOrder;
            Common.totalCart = total;
        } mTotal = total;

        Locale locale = new Locale("en", "UG");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        totalPrice.setText(numberFormat.format(total));

    }


    //adapter
    public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {
        private List<Order> listData = new ArrayList<>();
        private Context context;

        public CartAdapter(List<Order> listData, Context context) {
            this.listData = listData;
            this.context = context;
        }

        @NonNull
        @Override
        public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View itemView = inflater.inflate(R.layout.layout_cart, parent, false);
            return new CartViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, final int position) {
            TextDrawable textDrawable = TextDrawable.builder()
                    .buildRound(""+listData.get(position).getQuantity(), Color.RED);
            cartViewHolder.cart_item_count.setImageDrawable(textDrawable);

            Locale locale = new Locale("en", "UG");
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

            int price = (Integer.parseInt(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuantity()));

            cartViewHolder.cart_item_price.setText(" "+numberFormat.format(price));

            cartViewHolder.cart_item_name.setText(listData.get(position).getOrderName());

            cartViewHolder.cart_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Cart cart = new Cart();
                   deleteCart(position);
                }
            });

            cartViewHolder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClicked) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return listData.size();
        }
    }


    // deleting from the cart
    public void deleteCart(int position){
        loadCartOrders();
        cartOrder.remove(position);
        new Database(this).clearCart();
         int newTotal = 0;
        for (Order item:cartOrder) {
            new Database(this).addToCart(item);
            newTotal += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));
            Locale locale = new Locale("en", "UG");
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
            totalPrice.setText(numberFormat.format(newTotal));
        }
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
