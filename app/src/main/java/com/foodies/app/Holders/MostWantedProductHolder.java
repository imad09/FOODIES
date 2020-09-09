package com.foodies.app.Holders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.foodies.app.R;

public class MostWantedProductHolder extends RecyclerView.ViewHolder {
    private ImageView most_wanted_product_image;
    private TextView most_wanted_product_name,most_wanted_product_price,most_wanted_product_description;
    private Context context;
    public MostWantedProductHolder(@NonNull View itemView) {
        super(itemView);
        context = itemView.getContext();
        most_wanted_product_image = itemView.findViewById(R.id.most_wanted_product_image);
        most_wanted_product_name = itemView.findViewById(R.id.most_wanted_product_name);
        most_wanted_product_price = itemView.findViewById(R.id.most_wanted_product_price);
        most_wanted_product_description = itemView.findViewById(R.id.most_wanted_product_description);
    }

    public void send(String product_name, String product_image, int product_price, String product_description) {
        most_wanted_product_name.setText(product_name.toUpperCase());
        most_wanted_product_description.setText(product_description);
        Glide.with(context).load(product_image).into(most_wanted_product_image);
        most_wanted_product_price.setText(String.valueOf(product_price));
    }
}
