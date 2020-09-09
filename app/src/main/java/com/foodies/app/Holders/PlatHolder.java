package com.foodies.app.Holders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.foodies.app.Interfaces.Favorite_Interface;
import com.foodies.app.MainActivity;
import com.foodies.app.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class PlatHolder extends RecyclerView.ViewHolder {
    private TextView plat_name,plat_price;
    private ImageView plat_image,add_to_favorite_image;
    private CardView add_to_favorite_icon;
    private Context context;
    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;
    private RatingBar card_product_rating;
    private TextView card_product_rating_count;
    public PlatHolder(@NonNull View itemView) {
        super(itemView);
        mAuth = FirebaseAuth.getInstance();
        context = itemView.getContext();
        plat_name = itemView.findViewById(R.id.plat_name);
        plat_image = itemView.findViewById(R.id.plat_image);
        card_product_rating_count = itemView.findViewById(R.id.card_product_rating_count);
        card_product_rating = itemView.findViewById(R.id.card_product_rating);
        add_to_favorite_image = itemView.findViewById(R.id.add_to_favorite_image);
        plat_price = itemView.findViewById(R.id.plat_price);
        add_to_favorite_icon = itemView.findViewById(R.id.add_to_favorite_icon);
        mUserRef = FirebaseDatabase.getInstance().getReference("Users");
    }

    public void send(final String product_name, String product_image, int product_price, String product_description, final String key) {
        plat_price.setText(String.valueOf(product_price));
        Glide.with(context).load(product_image).into(plat_image);
        plat_name.setText(product_name);
        plat_name.setText(product_name);
        add_to_favorite_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Favorite_Interface.Add_To_Favorite(key,plat_name,context);
            }
        });
        //lets create Something to check if the product is in favorite list of the user or not :)
        Favorite_Interface.check_favorite(key,add_to_favorite_image);
        Favorite_Interface.favorite(key,card_product_rating,card_product_rating_count);
    }
}
