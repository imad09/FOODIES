package com.foodies.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.foodies.app.Fragment.DetailleFragment;
import com.foodies.app.Fragment.ReviewsFragment;
import com.foodies.app.Interfaces.Auth;
import com.foodies.app.Interfaces.Favorite_Interface;
import com.foodies.app.Models.ProductModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Product_Detaille_Activity extends AppCompatActivity {
    private TabLayout product_detaille_tablyout;
    private ViewPager product_detaille_viewpager;
    public static ProductDetailleTabAdapter productDetailleTabAdapter;
    private ImageView detaille_product_image;
    private TextView detaille_product_name,detaille_product_price;
    private DatabaseReference mProductRef;
    //Quantity Section
    private ImageView add_product_quantity,remove_product_quantity;
    private int quantity =1;
    private TextView add_to_cart_text;
    private FirebaseAuth mAuth;
    private DatabaseReference mCartRef;
    private CardView add_product_to_cart_btn;
    private int new_quantity = 0;
    //Get User Cart Count
    private DatabaseReference mUserCartCountRef;
    private TextView user_cart_items_count;
    public static String key;
    private ImageView add_to_favorite_image;
    private CardView add_to_favorite_icon;
    private RatingBar card_product_rating;
    private TextView card_product_rating_count;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        key = getIntent().getStringExtra("key");
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_product__detaille_);
        product_detaille_tablyout = findViewById(R.id.product_detaille_tablyout);
        product_detaille_viewpager = findViewById(R.id.product_detaille_viewpager);
        productDetailleTabAdapter = new ProductDetailleTabAdapter(getSupportFragmentManager(),0);
        productDetailleTabAdapter.add(new DetailleFragment(),"Detaille");
        productDetailleTabAdapter.add(new ReviewsFragment(),"Commentaire");
        product_detaille_viewpager.setAdapter(productDetailleTabAdapter);
        product_detaille_tablyout.setupWithViewPager(product_detaille_viewpager);
        //Section
        detaille_product_image = findViewById(R.id.detaille_product_image);
        detaille_product_name = findViewById(R.id.detaille_product_name);
        detaille_product_price = findViewById(R.id.detaille_product_price);
        //Product Data
        mProductRef = FirebaseDatabase.getInstance().getReference("Products");
        try {
            mProductRef.child(getIntent().getStringExtra("key")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ProductModel productModel = dataSnapshot.getValue(ProductModel.class);
                    detaille_product_name.setText(productModel.getProduct_name());
                    Glide.with(Product_Detaille_Activity.this).load(productModel.getProduct_image()).into(detaille_product_image);
                    detaille_product_price.setText(String.valueOf(productModel.getProduct_price()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        //Add Remove Quantity
        add_product_quantity = findViewById(R.id.add_product_quantity);
        remove_product_quantity = findViewById(R.id.remove_product_quantity);
        add_to_cart_text = findViewById(R.id.add_to_cart_text);
        add_product_quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                add_to_cart_text.setText("Quanitity: "+quantity);
            }
        });
        add_product_quantity.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                quantity = quantity+5;
                add_to_cart_text.setText("Quanitity: "+quantity);
                return false;
            }
        });
        //remove Section
        remove_product_quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity == 1){
                    Toast.makeText(Product_Detaille_Activity.this, "Vous ne pouvez pas supprimer 1 quantité", Toast.LENGTH_LONG).show();
                }else {
                    quantity--;
                    add_to_cart_text.setText("Quanitity: "+quantity);
                }
            }
        });
        remove_product_quantity.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (quantity <= 5){
                    Toast.makeText(Product_Detaille_Activity.this, "Vous ne pouvez pas supprimer 1 quantité", Toast.LENGTH_LONG).show();
                }else {
                    quantity = quantity-5;
                    add_to_cart_text.setText("Quanitity: "+quantity);
                }
                return false;
            }
        });
        mCartRef = FirebaseDatabase.getInstance().getReference("Users");
        add_product_to_cart_btn = findViewById(R.id.add_product_to_cart_btn);
        add_product_to_cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Auth.mAuth.getCurrentUser() !=null){
                    mCartRef.child(mAuth.getCurrentUser().getUid()).child("Cart").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(key)){
                                int old_quantity = dataSnapshot.child(key).child("quantity").getValue(Integer.class);
                                new_quantity = old_quantity+quantity;
                                HashMap<String,Object> cart_hash = new HashMap<>();
                                cart_hash.put("key",getIntent().getStringExtra("key"));
                                cart_hash.put("time",System.currentTimeMillis());
                                cart_hash.put("quantity",new_quantity);
                                cart_hash.put("price",Long.parseLong(detaille_product_price.getText().toString())*new_quantity);
                                mCartRef.child(mAuth.getCurrentUser().getUid()).child("Cart").child(key).setValue(cart_hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Product_Detaille_Activity.this, "Quantité mise à jour (nouvelle quantité: "+ new_quantity+" )", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }else {
                                HashMap<String,Object> cart_hash = new HashMap<>();
                                cart_hash.put("key",getIntent().getStringExtra("key"));
                                cart_hash.put("time",System.currentTimeMillis());
                                cart_hash.put("quantity",quantity);
                                mCartRef.child(mAuth.getCurrentUser().getUid()).child("Cart").child(key).setValue(cart_hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Product_Detaille_Activity.this, "Produit ajouté au panier avec succès", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("Add Product To Cart", "onFailure: "+e.getMessage());
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
        //get Cart Count Section
        user_cart_items_count = findViewById(R.id.user_cart_items_count);
        mUserCartCountRef = FirebaseDatabase.getInstance().getReference("Users");
        if (Auth.mAuth.getCurrentUser() !=null){
            mUserCartCountRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long items_count = dataSnapshot.child("Cart").getChildrenCount();
                    user_cart_items_count.setText(String.valueOf(items_count));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        //remove Actionbar
        findViewById(R.id.go_to_cart_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Auth.mAuth !=null){
                    startActivity(new Intent(getApplicationContext(),UserCartActivity.class));
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                }else {
                    Toast.makeText(Product_Detaille_Activity.this, "Veuillez vous connecter", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                }
            }
        });
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });
        //add to favorite Section
        //check Favorite Section
        add_to_favorite_icon = findViewById(R.id.add_to_favorite_icon);
        add_to_favorite_image = findViewById(R.id.add_to_favorite_image);
        //get Rating
        add_to_favorite_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Favorite_Interface.Add_To_Favorite(key,detaille_product_name,Product_Detaille_Activity.this);
            }
        });
        card_product_rating_count = findViewById(R.id.card_product_rating_count);
        card_product_rating = findViewById(R.id.card_product_rating);
        //lets create Something to check if the product is in favorite list of the user or not :)
        Favorite_Interface.check_favorite(key,add_to_favorite_image);
        Favorite_Interface.favorite(key,card_product_rating,card_product_rating_count);
        getSupportActionBar().hide();
    }
    public class ProductDetailleTabAdapter extends FragmentPagerAdapter{
        private List<Fragment> fragmentList = new ArrayList<>();
        private List<String> fragment_name = new ArrayList<>();

        public ProductDetailleTabAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragment_name.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragment_name.get(position);
        }
        public void add(Fragment fragment,String name){
            fragmentList.add(fragment);
            fragment_name.add(name);
        }
    }
}
