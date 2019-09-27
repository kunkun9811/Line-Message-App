package com.timo.linemessageapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainPageActivity extends AppCompatActivity {

    private static final String TAG = "MainPageActivity";

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser user;

    /* For Recycler View */
    private String email;
    private String user_token;
    private String userId;
    private ArrayList<User> users;
    private ArrayList<String> emails;
    private ArrayList<String> tokens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        users = new ArrayList<>();
        email = null;
        user_token = null;
        emails = new ArrayList<>();
        tokens = new ArrayList<>();

        populateUsers();
    }



    private void populateUsers(){
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot doc : task.getResult()) {
                                Log.i(TAG, doc.getId() + " => " + doc.getData());
                                email = doc.getString("email");
                                user_token = doc.getString("token");
                                userId = doc.getString("userId");
                                User addUser = new User(email, userId, user_token);
                                users.add(addUser);
                            }
                            populateEmailsAndTokens();
                        } else {
                            Log.d(TAG, "populateEmailsAndTokens: UNABLE TO GET DOCUMENTS FROM DATABASE");
                        }
                    }
                });
    }

    private void populateEmailsAndTokens() {
        for(User user : users){
            emails.add(user.getEmail());
            tokens.add(user.getToken());
        }
        initRecyclerView();
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: initialize Recycler View");
        RecyclerView recyclerView = findViewById(R.id.theRecycler);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getApplicationContext(), emails, tokens);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

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
}
