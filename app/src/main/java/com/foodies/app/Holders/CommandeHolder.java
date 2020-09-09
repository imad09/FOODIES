package com.foodies.app.Holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foodies.app.Models.ProductModel;
import com.foodies.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class CommandeHolder extends RecyclerView.ViewHolder {
    private DatabaseReference mProductRef;
    private TextView command_product_quantity,command_product_name,command_product_description,command_product_price;
    public CommandeHolder(@NonNull View itemView) {
        super(itemView);
        command_product_price = itemView.findViewById(R.id.command_product_price);
        command_product_quantity = itemView.findViewById(R.id.command_product_quantity);
        command_product_name = itemView.findViewById(R.id.command_product_name);
        command_product_description = itemView.findViewById(R.id.command_product_description);
        command_product_price = itemView.findViewById(R.id.command_product_price);
    }

    public void push(String key, final int quantity) {
        mProductRef = FirebaseDatabase.getInstance().getReference("Products").child(key);
        mProductRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ProductModel productModel = dataSnapshot.getValue(ProductModel.class);
                long price = productModel.getProduct_price()*quantity;
                command_product_name.setText(productModel.getProduct_name());
                command_product_price.setText(String.valueOf(price));
                command_product_quantity.setText("x"+quantity);
                command_product_description.setText(productModel.getProduct_description());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });
    }
}
