package com.foodies.app.Interfaces;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.foodies.app.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public interface Favorite_Interface {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference("Users");
    public static void favorite(String key, final RatingBar ratingBar, final TextView rating_count){
        rating_count.setText("(0 Utilisateurs)");
        DatabaseReference mProductRef = FirebaseDatabase.getInstance().getReference("Products");
        mProductRef.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //value event listener good and bad in same time
                try {
                    float rating = dataSnapshot.child("Rating").child("rating").getValue(Float.class);
                    //lets get Raters count
                    long raters_count = dataSnapshot.child("Rating").child("Users").getChildrenCount();
                    //lets make maths Calculate
                    float card_rating = rating/raters_count;
                    ratingBar.setRating(card_rating);
                    rating_count.setText("("+raters_count+" Utilisateurs"+")");
                }catch (NullPointerException e){
                    e.printStackTrace();
                    Log.e("Rating Error", "onDataChange: "+ratingBar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public static void Add_To_Favorite(final String key, final TextView detaille_product_name, final Context context){
        if (mAuth.getCurrentUser() !=null){
            mUserRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("Favorite").hasChild(key)){
                        mUserRef.child(mAuth.getCurrentUser().getUid()).child("Favorite").child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //check_favorite_icon();
                                Toast.makeText(context, detaille_product_name.getText().toString()+"  Supprimer de la liste des favoris", Toast.LENGTH_LONG).show();
                            }
                        });
                    }else {
                        HashMap<String,Object> favorite_hash = new HashMap<>();
                        favorite_hash.put("key",key);
                        favorite_hash.put("time",System.currentTimeMillis());
                        mUserRef.child(mAuth.getCurrentUser().getUid()).child("Favorite").child(key).setValue(favorite_hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, detaille_product_name.getText().toString()+" a été ajouté à la liste des favoris", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else {
            Toast.makeText(context, "Veuillez d'abord vous connecter", Toast.LENGTH_LONG).show();
        }
    }
    public static void check_favorite(final String key, final ImageView favorite_image){
        if (mAuth.getCurrentUser()!=null){
            mUserRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("Favorite").hasChild(key)){
                        favorite_image.setImageResource(R.drawable.favorite_icon_full);
                    }else {
                        favorite_image.setImageResource(R.drawable.favorite_empty_icon);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    databaseError.toException().printStackTrace();
                }
            });
        }
    }
}
