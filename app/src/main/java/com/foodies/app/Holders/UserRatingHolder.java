package com.foodies.app.Holders;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foodies.app.Models.Users;
import com.foodies.app.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserRatingHolder extends RecyclerView.ViewHolder {
    private TextView rating_user_message,rating_user_date,rating_user_name;
    private RatingBar card_product_rating;
    private DatabaseReference mUserRef;
    public UserRatingHolder(@NonNull View itemView) {
        super(itemView);
        rating_user_message = itemView.findViewById(R.id.rating_user_message);
        rating_user_date = itemView.findViewById(R.id.rating_user_date);
        card_product_rating = itemView.findViewById(R.id.card_product_rating);
        rating_user_name = itemView.findViewById(R.id.rating_user_name);
    }
    public void push(String message, long time, float rating, String uid) {
        rating_user_message.setText(message);
        //get Date
        Date get_date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd : HH:mm");
        rating_user_date.setText(simpleDateFormat.format(get_date));
        //apply rating
        card_product_rating.setRating(rating);
        //get User Info
        try {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
            mUserRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        Users users = dataSnapshot.getValue(Users.class);
                        rating_user_name.setText(users.getUsername());
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }
}
