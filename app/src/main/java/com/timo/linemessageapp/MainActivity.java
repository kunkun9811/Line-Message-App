package com.timo.linemessageapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    String email_text;
    String password_text;
    TextView email;
    TextView password;

    public void signIn(View signInButton){

        /** This is an example code of adding data to Firestore */
//        Map<String, String> test = new HashMap<>();
////        test.put("TestingTesting", "123");
////        String keyName = null;
////        for(Map.Entry<String, String> entry : test.entrySet()){
////            keyName = entry.getKey();
////        }
////        db.collection("Message_Board")
////                .document(keyName)
////                .set(test)
////                .addOnSuccessListener(new OnSuccessListener<Void>() {
////                    @Override
////                    public void onSuccess(Void theVoid) {
////                        Log.i("Checking Connection: ", "Success connection => Adding Value Works!");
////                    }
////                });
        email_text = email.getText().toString();
        password_text = password.getText().toString();

        mAuth.signInWithEmailAndPassword(email_text, password_text)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if(currentUser != null){
                                Intent intent;
                                if(!checkIfVerified(currentUser)){
                                    intent = new Intent(getApplicationContext(), VerifyEmailActivity.class);
                                    startActivity(intent);
                                } else {
                                    intent = new Intent(getApplicationContext(), MainPageActivity.class);
                                    startActivity(intent);
                                }
                            }

                            Toast.makeText(getApplicationContext(), currentUser.getEmail() + " Welcome Back!", Toast.LENGTH_SHORT).show();
                            finish();
                            return;

                        } else {
                            Log.w("signInWithEmail&PW: ", "Authentication Failed", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication Failed. Please Double Check Your Email and Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void register(View registerButton){
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }

    private boolean checkIfVerified(FirebaseUser currentUser){
        if(!currentUser.isEmailVerified()){
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onStart(){
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent;
            if(!checkIfVerified(currentUser)){
                intent = new Intent(getApplicationContext(), VerifyEmailActivity.class);
                startActivity(intent);
            } else {
                intent = new Intent(getApplicationContext(), MainPageActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email_text = null;
        password_text = null;
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
    }
}
