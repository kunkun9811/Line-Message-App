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

public class RegisterActivity extends AppCompatActivity {

    TextView email;
    TextView password;
    TextView confirmPassword;
    String email_text;
    String password_text;
    String confirmPassword_text;
    TextView submit_button;

    /* mAuth points to our Firebase Authentication instance */
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        email_text = null;
        password_text = null;
        confirmPassword_text = null;
        submit_button = findViewById(R.id.submitButton);

    }

    private boolean checkIfVerified(FirebaseUser currentUser){
        if(!currentUser.isEmailVerified()){
            return false;
        } else {
            return true;
        }
    }

    public void submit(View submitButton){

        email_text = email.getText().toString();
        password_text = password.getText().toString();
        confirmPassword_text = confirmPassword.getText().toString();
        Log.i("After Pressing Submit: ", "Email: " + email_text +  ", Password: " + password_text + ", Confirm Password: " + confirmPassword_text);

        if(email_text != null && password_text != null && confirmPassword_text != null) {
            if (password_text.equals(confirmPassword_text)) {
                // Toast.makeText(this, "You have registered!", Toast.LENGTH_SHORT).show();

                mAuth.createUserWithEmailAndPassword(email_text, password_text)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Log.i("createUserWithEmail: ", "success");
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    if(checkIfVerified(user)) {
                                        Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), VerifyEmailActivity.class);
                                        startActivity(intent);
                                    }
                                    finish();
                                    return;
                                } else {
                                    Log.w("createUserWithEmail: ", "failure", task.getException());
                                    Toast.makeText(getApplicationContext(), "Something bad happened :( Unable to create an account", Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    return;
                                }
                            }
                        });

            } else {
                Toast.makeText(this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please fill every field", Toast.LENGTH_SHORT).show();
        }

        email_text = password_text = confirmPassword_text = null;

        Log.i("After Checking Submit: ", "Email: " + email_text +  ", Password: " + password_text + ", Confirm Password: " + confirmPassword_text);


    }
}
