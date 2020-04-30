package com.mcdenny.agritunda.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mcdenny.agritunda.Common.Common;
import com.mcdenny.agritunda.Model.Admin;
import com.mcdenny.agritunda.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import mehdi.sakout.fancybuttons.FancyButton;

public class AdminLoginActivity extends AppCompatActivity {
    EditText username, password;
    TextView signup;
    FancyButton login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        //getting the reference
        username = (MaterialEditText) findViewById(R.id.ad_phone);
        password = (MaterialEditText) findViewById(R.id.ad_password);
        login = (FancyButton) findViewById(R.id.btn_ad_login);
        signup = (TextView) findViewById(R.id.ad_tvSignup);

        //initializing firebase database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference user_table = database.getReference("users");

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminLoginActivity.this, AdminSignupActivity.class));
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog mDialog = new ProgressDialog(AdminLoginActivity.this);
                //checking whether the edit text is empty
                if(username.getText().toString().isEmpty()) {
                    username.setError("You must fill in the phone number!");
                    username.requestFocus();
                }
                else if (password.getText().toString().isEmpty()){
                    password.setError("You must fill in the password!");
                    password.requestFocus();
                }
                //If the textfields are not empty
                else {
                    //setting a dialog to tell the user to wait
                    mDialog.setMessage("Please wait.....");
                    mDialog.setTitle("Loggin in");
                    mDialog.show();

                    user_table.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //checking if the user exists in the database
                            if (dataSnapshot.child(username.getText().toString()).exists()) {
                                //stopping the progress dialog
                                mDialog.dismiss();

                                //getting the users information
                                Admin admin = dataSnapshot.child(username.getText().toString()).getValue(Admin.class);
                                admin.setPhone(username.getText().toString());
                                if (admin.getPassword().equals(password.getText().toString())) {
                                    Intent intent = new Intent(AdminLoginActivity.this, AdminHomeActivity.class);
                                    Common.admin_Current = admin;//the user details are stored in user_current variable
                                    startActivity(intent);
                                    finish();//stops the login activity
                                } else {
                                    Toast.makeText(AdminLoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                                    mDialog.dismiss();
                                }
                            }
                            //if user doesn't exist
                            else {
                                Toast.makeText(AdminLoginActivity.this, "Admin does not exist!", Toast.LENGTH_SHORT).show();
                                mDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }//end of else if
            }
        });
    }

}
