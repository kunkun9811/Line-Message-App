package com.timo.linemessageapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

public class VerifyEmailActivity extends AppCompatActivity {

    private static final String TAG = "VerifyEmailActivity";

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;

    TextView user_email;
    long user_count;
    String user_id;
    String user_token;

    User newUser;
    /*
        I'm trying to add token to user object and make an arraylist out of them. use the arraylist to populate the recycler view
        and set onClicklistener on each item so whenever I click them they send a notification via FCM. Needs to be done by 9/18/2019 ...
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        user_email = findViewById(R.id.token);
        user_email.setText("Sent Email to " + mAuth.getCurrentUser().getEmail());
    }

    @Override
    protected void onStart() {
        super.onStart();

        /* I think currentUser.reload(), the reload() is fucking up the synchronization between firebase and
            android. If you reload, whenever you try to retrieve value from firebase it returns 0 or null
         */

        /* user count */
        db.collection("data")
                .document("users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user_count = documentSnapshot.getLong("total");
                        Log.i(TAG, "Get user total: " + user_count);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Unable to add user to database");
                    }
                });
        user_id = mAuth.getUid();
    }

    public void signOut(View signOutButton){
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            mAuth.signOut();
            Toast.makeText(getApplicationContext(), "Signed Out " + user.getEmail(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "User Already Signed Out", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        return;
    }

    public void checkIfVerified(View theVerifyButton){
        currentUser.reload();
        if(!currentUser.isEmailVerified()){
            Toast.makeText(getApplicationContext(), "Your account is still not verified :(", Toast.LENGTH_SHORT).show();
        } else {
            // increment user_count first
            ++user_count;

            /* For update, it is String, Object
            * update user count in database*/
            Map<String, Object> changeCount = new HashMap<>();
            changeCount.put("total", user_count);

            db.collection("data")
                    .document("users")
                    .update(changeCount)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i(TAG, "Updated total number of users: " + user_count);
                        }
                    });

            /* Get token */
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if(task.isSuccessful()){
                                String msg = task.getResult().getToken();
                                Log.d(TAG, "************TOKEN************ " + msg);
                                user_token = msg;
                                newUser = new User(currentUser.getEmail(), user_id, user_token);
                                db.collection("users")
                                        .document(currentUser.getUid())
                                        .set(newUser)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
//                                                Toast.makeText(getApplicationContext(), "Account Successfully Created", Toast.LENGTH_SHORT).show();
                                                Toast.makeText(getApplicationContext(), "Your email is verified! Welcome " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });


                            } else {

                                Log.d(TAG, "UNABLE TO GET TOKEN");
                                Toast.makeText(getApplicationContext(), "COULDN'T GET TOKEN 1", Toast.LENGTH_SHORT).show();

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "UNABLE TO GET TOKEN: " + e);
                            Toast.makeText(getApplicationContext(), "COULDN'T GET TOKEN 2", Toast.LENGTH_SHORT).show();
                        }
                    });


            return;
        }
    }

    public void sendVerificationEmail(final View sendEmailButton){
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        currentUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        sendEmailButton.setEnabled(true);

                        if(task.isSuccessful()){
                            Log.i("sendEmail Success: ", "Email Sent To " + currentUser.getEmail());
                            Toast.makeText(VerifyEmailActivity.this, "Email Sent To " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
                            currentUser.reload();
                        } else {
                            Log.e("sendEmail Fail: ", "Failed To Send Email To " + currentUser.getEmail());
                            Toast.makeText(VerifyEmailActivity.this, "Failed To Send Email To " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
