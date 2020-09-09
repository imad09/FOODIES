package com.foodies.app.Holders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.foodies.app.Interfaces.Favorite_Interface;
import com.foodies.app.R;

public class CoffeHolder extends RecyclerView.ViewHolder {
    private TextView product_coffe_name,product_coffe_price,product_coffe_description,card_product_rating_count;
    private ImageView product_coffe_image;
    private RatingBar card_product_rating;
    private ImageView add_to_favorite_image;
    private CardView add_to_favorite_icon;
    private Context context;
    public CoffeHolder(@NonNull View itemView) {
        super(itemView);
        context  = itemView.getContext();
        product_coffe_price = itemView.findViewById(R.id.product_coffe_price);
        product_coffe_name = itemView.findViewById(R.id.product_coffe_name);
        product_coffe_description = itemView.findViewById(R.id.product_coffe_description);
        product_coffe_image = itemView.findViewById(R.id.product_coffe_image);
        card_product_rating_count = itemView.findViewById(R.id.card_product_rating_count);
        card_product_rating = itemView.findViewById(R.id.card_product_rating);
        add_to_favorite_image = itemView.findViewById(R.id.add_to_favorite_image);
        add_to_favorite_icon = itemView.findViewById(R.id.add_to_favorite_icon);

    }

    public void send(String key, String product_name, int product_price, String product_description, String product_image) {
        product_coffe_name.setText(product_name);
        product_coffe_description.setText(product_description);
        product_coffe_price.setText(String.valueOf(product_price));
        Glide.with(context).load(product_image).into(product_coffe_image);
        Favorite_Interface.check_favorite(key,add_to_favorite_image);
        Favorite_Interface.favorite(key,card_product_rating,card_product_rating_count);
        //Favorite Button Handle Click
        add_to_favorite_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Favorite_Interface.Add_To_Favorite(key,product_coffe_name,context);
            }
        });
    }
}
