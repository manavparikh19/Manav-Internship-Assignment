package com.developer.mae.snfsendriyainternshipapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    TextInputLayout regFirstName,regLastName,regUsername,regEmailAddress,regPhoneNumber,regPassword,regConfirmPassword;
    Button btnSignUp,btnAlreadyUserLogin;

    FirebaseDatabase rootNode;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

         btnSignUp = findViewById(R.id.btnRegisterSignUp);
         btnAlreadyUserLogin = findViewById(R.id.btnRegisterLogin);

         regFirstName=findViewById(R.id.registerFirstName);
         regLastName=findViewById(R.id.registerLastName);
         regUsername=findViewById(R.id.registerUsername);
         regEmailAddress=findViewById(R.id.registerEmailAddress);
         regPhoneNumber=findViewById(R.id.registerPhoneNumber);
         regPassword=findViewById(R.id.registerPassword);
         regConfirmPassword=findViewById(R.id.registerConfirmPassword);

         btnSignUp.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 rootNode=FirebaseDatabase.getInstance();
                 reference=rootNode.getReference("users");

                 //get ALl the values

                 String firstName=regFirstName.getEditText().getText().toString();
                 String lastName=regLastName.getEditText().getText().toString();
                 String username=regUsername.getEditText().getText().toString();
                 String emailAddress=regEmailAddress.getEditText().getText().toString();
                 String phoneNumber=regPhoneNumber.getEditText().getText().toString();
                 String password=regPassword.getEditText().getText().toString();
                 String confirmPassword=regConfirmPassword.getEditText().getText().toString();


                 UserHelperClass helperClass=new UserHelperClass(firstName,lastName,username,emailAddress,phoneNumber,password,confirmPassword);

                 reference.child(username).setValue(helperClass);
             }
         });


        btnAlreadyUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    private Boolean validateUsername(){
        String val=regUsername.getEditText().getText().toString();
        String noWhiteSpace= "\\A\\w{4,20}\\z";

        if(val.isEmpty()){
            regUsername.setError("Field cannot be empty");
            return false;
        }else if(val.length() >= 15){
            regUsername.setError("Username too long");
            return false;
        }else if(!val.matches(noWhiteSpace)){
            regUsername.setError("White spaces are not allowed");
            return false;
        }else {
            regUsername.setError(null);
            regUsername.setErrorEnabled(false);
            return true;
        }

    }

    private Boolean validatePassword(){
        String val=regPassword.getEditText().getText().toString();
        String passwordVal="^"+
                "(?=.*[A-Z])"+      // any upper or lower case letter
                "(?=.*[@#$%^&+=])" + //a special character must occur at least once you can replace with your special characters
                "(?=\\S+$)"    +     // no whitespace allowed in the entire string
                ".{4,}"      +    // anything, at least six places though
                "$";
        if(val.isEmpty()){
            regPassword.setError("Field cannot be empty");
            return false;
        }else if(!val.matches(passwordVal)){
            regPassword.setError("Password is too weak");
            return false;
        }else{
            regPassword.setError(null);
            regPassword.setErrorEnabled(false);
            return true;
        }
    }
}