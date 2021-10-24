package com.developer.mae.snfsendriyainternshipapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
private FirebaseAuth mAuth;
TextView tvEmail ;
Button logout ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tvEmail = findViewById(R.id.homeEmailTV);
        logout = findViewById(R.id.homeLogoutBtn);

        Intent intent = getIntent();
        boolean isFBLogin = intent.getBooleanExtra("fb_login", false);
        boolean isServiceLogin = intent.getBooleanExtra("service_login", false);
        String emailAddr=intent.getStringExtra("emailID");
        tvEmail.setText(emailAddr);


        //init firebase auth
        mAuth = FirebaseAuth.getInstance();

        if (isServiceLogin) {
            CheckUser();}

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mAuth.signOut();
                    if (isFBLogin) {
                        LoginManager.getInstance().logOut();
                    }
                    CheckUser();
                }
            });


        }


    private void CheckUser() {
        //get Current user
        FirebaseUser firebaseUser =mAuth.getCurrentUser();
        if(firebaseUser == null){
            //user not logged in
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }else{
            String email =firebaseUser.getEmail();
            //set email
            tvEmail.setText(email);
        }
    }
}