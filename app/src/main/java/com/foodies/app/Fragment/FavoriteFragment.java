package com.foodies.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.foodies.app.Interfaces.Favorite_Interface;
import com.foodies.app.Models.ProductModel;
import com.foodies.app.Product_Detaille_Activity;
import com.foodies.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {
    private RecyclerView favorite_recycler;
    private DatabaseReference mUserRef,mProductRef;
    private List<ProductModel> productModels = new ArrayList<>();
    private List<String> product_key = new ArrayList<>();
    private FirebaseAuth mAuth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fav_view = inflater.inflate(R.layout.fragment_favorite, container, false);
        mAuth = FirebaseAuth.getInstance();
        favorite_recycler = fav_view.findViewById(R.id.favorite_recycler);
        if (mAuth.getCurrentUser() !=null){
            mUserRef = FirebaseDatabase.getInstance().getReference("Users");
            mUserRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snap :dataSnapshot.child("Favorite").getChildren()){
                        product_key.add(snap.child("key").getValue(String.class));
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            mProductRef = FirebaseDatabase.getInstance().getReference("Products");
            mProductRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (int i = 0; i < product_key.size() ; i++) {
                        productModels.add(dataSnapshot.child(product_key.get(i)).getValue(ProductModel.class));
                    }
                    favorite_recycler.setAdapter(new FavoriteAdapter(getActivity(),productModels));
                    favorite_recycler.setLayoutManager(new GridLayoutManager(getActivity(),2));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        return fav_view;
    }
    public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteHolder> {
        private Context context;
        private List<ProductModel> productModels;

        public FavoriteAdapter(Context context, List<ProductModel> productModels) {
            this.context = context;
            this.productModels = productModels;
        }

        @NonNull
        @Override
        public FavoriteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View fav_view =LayoutInflater.from(context).inflate(R.layout.row_user_favorite_layout,parent,false);
            return new FavoriteHolder(fav_view);
        }

        @Override
        public void onBindViewHolder(@NonNull FavoriteHolder holder, int position) {
            try {
                holder.user_favorite_name.setText(productModels.get(position).getProduct_name());
                holder.user_favorite_price.setText(String.valueOf(productModels.get(position).getProduct_price()));
                Glide.with(context).load(productModels.get(position).getProduct_image()).into(holder.user_favorite_image);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().finish();
                        startActivity(new Intent(context, Product_Detaille_Activity.class).putExtra("key",productModels.get(position).getProduct_key()));
                    }
                });
                Favorite_Interface.favorite(productModels.get(position).getProduct_key(),holder.card_product_rating,holder.card_product_rating_count);
            }catch (NullPointerException e){
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return productModels.size();
        }

        class FavoriteHolder extends RecyclerView.ViewHolder{
            private TextView user_favorite_name,user_favorite_price,card_product_rating_count;
            private ImageView user_favorite_image;
            private RatingBar card_product_rating;

            public FavoriteHolder(@NonNull View itemView) {
                super(itemView);
                user_favorite_name = itemView.findViewById(R.id.user_favorite_name);
                user_favorite_price = itemView.findViewById(R.id.user_favorite_price);
                user_favorite_image = itemView.findViewById(R.id.user_favorite_image);
                card_product_rating_count = itemView.findViewById(R.id.card_product_rating_count);
                card_product_rating = itemView.findViewById(R.id.card_product_rating);
            }
        }
    }
}
