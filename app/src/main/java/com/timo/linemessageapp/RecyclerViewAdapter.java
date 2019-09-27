package com.timo.linemessageapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<String> emails;
    private ArrayList<String> user_tokens;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public RecyclerViewAdapter(Context context, ArrayList<String> emails, ArrayList<String> user_token) {
        this.emails = emails;
        this.user_tokens = user_token;
        this.context = context;
    }


    /* This part will always be the same */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
    /* End of this part */

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        viewHolder.user_email.setText(emails.get(position));
        viewHolder.user_token.setText(user_tokens.get(position));

        viewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked on: " + emails.get(position));

//                Toast.makeText(context, "Clicked on " + user_tokens.get(position), Toast.LENGTH_SHORT).show();
                Map<String, Object> addMessage = new HashMap<>();
                addMessage.put("token", user_tokens.get(position));
                db.collection("messages")
                        .add(addMessage)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(context, "Sent Notification To " + emails.get(position), Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "UNABLE TO SEND MESSAGE TO " + emails.get(position), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return emails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView user_email;
        TextView user_token;
        ConstraintLayout itemLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user_email = itemView.findViewById(R.id.email);
            user_token = itemView.findViewById(R.id.token);
            itemLayout = itemView.findViewById(R.id.item_layout);

        }
    }
}
