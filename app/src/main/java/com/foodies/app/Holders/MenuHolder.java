package com.foodies.app.Holders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.foodies.app.Models.ProductModel;
import com.foodies.app.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shehuan.niv.NiceImageView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuHolder extends RecyclerView.ViewHolder {
    private TextView mMenu_name,category_count;
    private CircleImageView mMenu_image;
    private Context context;
    private DatabaseReference mCategoryRef;
    private DatabaseReference mCalculate_Ref,mDeleteRef;
    private List<ProductModel> productModelList = new ArrayList<>();
    private int counter =0;
    public MenuHolder(@NonNull View itemView) {
        super(itemView);
        context = itemView.getContext();
        mMenu_name = itemView.findViewById(R.id.menu_name);
        category_count = itemView.findViewById(R.id.category_count);
        mMenu_image = itemView.findViewById(R.id.menu_image);
        mCalculate_Ref = FirebaseDatabase.getInstance().getReference("Products");

    }

    public void send(String menu_image, String menu_name) {
        mMenu_name.setText(menu_name.toUpperCase());
        Glide.with(context).load(menu_image).into(mMenu_image);
        mCalculate_Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    productModelList.add(snapshot.getValue(ProductModel.class));
                }
                for (ProductModel productModel:productModelList){
                    if (productModel.getProduct_category().contains(menu_name)){
                        counter++;
                    }
                }
                category_count.setText(counter+" Produits");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException();
            }
        });
    }
}
