package com.askme.comfort.chat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.askme.comfort.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class Chat_Login_Selection extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    Button Button_Login_Google;

    private FirebaseAuth Firebase_Auth;
    private GoogleApiClient Google_Api_Client;

    private static final String TAG = "Chat_Login_Selection";
    private static final int RC_SIGN_IN = 9001;

    public void Sign_In_With_Google()
    {
        Intent I = Auth.GoogleSignInApi.getSignInIntent(Google_Api_Client);
        startActivityForResult(I, RC_SIGN_IN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat__login__selection);

        Firebase_Auth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        Google_Api_Client = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Button_Login_Google = (Button)findViewById(R.id.Button_Login_With_Google);
        Button_Login_Google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sign_In_With_Google();
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                Firebase_Auth_With_Google(account);
            } else {
                // Google Sign In failed
                Log.e(TAG, "Google Sign In failed.");
            }
        }
    }

    private void Firebase_Auth_With_Google(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGooogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        Firebase_Auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(Chat_Login_Selection.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(Chat_Login_Selection.this, Chat_Panel.class));
                            finish();
                        }
                    }
                });
    }
}
