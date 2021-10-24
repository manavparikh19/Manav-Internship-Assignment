package com.developer.mae.snfsendriyainternshipapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Objects;


public class LoginActivity extends AppCompatActivity {
private SignInButton googleSignInButton;
private LoginButton facebookSignInButton;
private GoogleSignInClient mGoogleSignInClient;
private String TAG="LoginActivity";
private FirebaseAuth mAuth;
private int RC_SIGN_IN = 1;
private CallbackManager mCallbackManager;
TextInputLayout username,password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button btnSignup = findViewById(R.id.btnLoginSignUp);
        Button btnLogin = findViewById(R.id.btnLogin);

        username=findViewById(R.id.loginUsername);
        password=findViewById(R.id.loginPassword);

        facebookSignInButton=findViewById(R.id.facebookSignInButton);

        googleSignInButton=findViewById(R.id.googleSignInButton);
        mAuth=FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        CheckUser();

        GoogleSignInOptions gso =new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("584697808925-u0rsbqv5cn41cffghnilj08v89kc78i9.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });



        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        facebookSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "facebook:onError", error);
                    }
                });

            }
        });


    }


    private Boolean validateUsername(){
        String val=username.getEditText().getText().toString();

        if(val.isEmpty()){
            username.setError("Field cannot be empty");
            return false;
        }else {
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }

    }

    private Boolean validatePassword(){
        String val=password.getEditText().getText().toString();
        
        if(val.isEmpty()){
            password.setError("Field cannot be empty");
            return false;
        }else{
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }
    
    public void loginUser(){
        //Validate Login Info
        if(!validateUsername() | !validatePassword()){
            return;
        }else{
            isUser();
        }
    }

    private void isUser() {

        final String userEnteredUsername = username.getEditText().getText().toString().trim();
        final String userEnteredPassword = password.getEditText().getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        Query checkUser=reference.orderByChild("username").equalTo(userEnteredUsername);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot ){

                if(dataSnapshot.exists()){

                    username.setError(null);
                    username.setErrorEnabled(false);

                    String passwordFromDB = dataSnapshot.child(userEnteredUsername).child("password").getValue(String.class);


                    assert passwordFromDB != null;
                    if(passwordFromDB.equals(userEnteredPassword)){

                        password.setError(null);
                        password.setErrorEnabled(false);
                        Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                        String emailFromDB = dataSnapshot.child(userEnteredUsername).child("emailAddress").getValue(String.class);
                        intent.putExtra("emailID",emailFromDB);
                        startActivity(intent);
                        finish();

                    }else{
                        password.setError("Wrong Password");
                        password.requestFocus();
                    }
                }else{
                    username.setError("No such user exists");
                    username.requestFocus();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                           Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                           intent.putExtra("fb_login",true);
                           intent.putExtra("service_login",true);
                           startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void CheckUser() {
        //if user is already signed in then go to profile activity
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        if(firebaseUser!=null){
            startActivity(new Intent(this,HomeActivity.class));
            finish();
        }

    }

    private void signIn() {
        Intent signInIntent =mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task =GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount acc=task.getResult(ApiException.class);
                FirebaseGoogleAuth(acc);

            }catch (ApiException e){
                FirebaseGoogleAuth(null);
            }
        }else{
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
            // Pass the activity result back to the Facebook SDK
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount account) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser firebaseUser=mAuth.getCurrentUser();

                String personName = account.getDisplayName();
                String personGivenName=account.getGivenName();
                String PersonFamilyName=account.getFamilyName();
                String personEmail= account.getEmail();
                String personId=account.getId();
                Uri personPhoto = account.getPhotoUrl();

                //check if user is new or existing
                if(authResult.getAdditionalUserInfo().isNewUser()){
                    Toast.makeText(getApplicationContext(), "Account Created....\n"+personEmail, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Existing User....\n"+personEmail, Toast.LENGTH_SHORT).show();
                }

                //start Home Activity
                Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
                intent.putExtra("service_login",true);
                startActivity(intent);
                Log.d(TAG, "Done");
                finish();

            }
        });
    }
}


