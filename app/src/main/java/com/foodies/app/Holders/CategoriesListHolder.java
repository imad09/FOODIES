package com.foodies.app.Holders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.foodies.app.Models.ProductModel;
import com.foodies.app.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoriesListHolder extends RecyclerView.ViewHolder {
    private TextView categorie_name,category_count;
    private CircleImageView categorie_image;
    private DatabaseReference mCalculate_Ref,mDeleteRef;
    private Context context;
    private int counter =0;
    private List<ProductModel> productModelList = new ArrayList<>();
    private CardView category_delete_btn;
    public CategoriesListHolder(@NonNull View itemView) {
        super(itemView);
        context = itemView.getContext();
        categorie_image = itemView.findViewById(R.id.category_image);
        categorie_name = itemView.findViewById(R.id.category_name);
        category_count = itemView.findViewById(R.id.category_count);
        category_delete_btn = itemView.findViewById(R.id.category_delete_btn);
        mCalculate_Ref = FirebaseDatabase.getInstance().getReference("Products");
        mDeleteRef = FirebaseDatabase.getInstance().getReference("Menu");
    }

    public void send(String categorie_images, final String categorie_names, final String key) {
        Glide.with(context).load(categorie_images).into(categorie_image);
        categorie_name.setText(categorie_names);
        mCalculate_Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    productModelList.add(snapshot.getValue(ProductModel.class));
                }
                for (ProductModel productModel:productModelList){
                    if (productModel.getProduct_category().contains(categorie_names)){
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
        category_delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeleteRef.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "La Catégorie "+categorie_names+" a éte Supprimier", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }
}
