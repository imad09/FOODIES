package com.foodies.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodies.app.Models.CartModel;
import com.foodies.app.Models.ProductModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class UserCartActivity extends AppCompatActivity {
    private FirebaseRecyclerAdapter<CartModel, CartHolder> cart_adapter;
    private DatabaseReference mCartRef;
    private FirebaseAuth mAuth;
    private RecyclerView user_cart_recycler;
    public static TextView user_cart_total_price;
    private DatabaseReference mUserRef,mProductRef;
    public static List<CartModel> keys = new ArrayList<>();
    public static long final_price,calc ;
    public static List<CartModel> cartModels = new ArrayList<>();
    private DatabaseReference mCommandRef;
    public static boolean mobile_valide=false;
    public static HashMap<String,Object> command_hash;
    private CardView cart_validate_btn;
    private LinearLayout empty_cart_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_user_cart);
        user_cart_total_price = findViewById(R.id.user_cart_total_price);
        mCartRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid()).child("Cart");
        Query cart_query = mCartRef.orderByKey();
        FirebaseRecyclerOptions mCartOption =new FirebaseRecyclerOptions.Builder<CartModel>().setQuery(cart_query,CartModel.class).build();
        cart_adapter = new FirebaseRecyclerAdapter<CartModel, CartHolder>(mCartOption) {
            @Override
            protected void onBindViewHolder(@NonNull CartHolder cartHolder, int i, @NonNull CartModel model) {
                cartHolder.push(model.getKey(),model.getQuantity());
            }

            @NonNull
            @Override
            public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View cart_view = LayoutInflater.from(UserCartActivity.this).inflate(R.layout.row_user_cart,parent,false);
                return new CartHolder(cart_view);
            }
        };
        user_cart_recycler = findViewById(R.id.user_cart_recycler);
        user_cart_recycler.setAdapter(cart_adapter);
        user_cart_recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mUserRef = FirebaseDatabase.getInstance().getReference("Users");
        //For Loop
        empty_cart_layout = findViewById(R.id.empty_cart_layout);
        mProductRef = FirebaseDatabase.getInstance().getReference("Products");
        mUserRef.child(mAuth.getCurrentUser().getUid()).child("Cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    List<CartModel> keys = new ArrayList<>();
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        keys.add(snapshot.getValue(CartModel.class));
                    }
                    empty_cart_layout.setVisibility(keys.size() > 0 ? View.GONE : View.VISIBLE);
                    mProductRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                for (int i = 0; i < keys.size(); i++) {
                                    final_price = final_price + (dataSnapshot.child(keys.get(i).getKey()).child("product_price").getValue(Long.class)) * keys.get(i).getQuantity();
                                }
                                user_cart_total_price.setText(final_price + "");
                                final_price = 0;
                            } catch (NullPointerException e) {
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
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });
        findViewById(R.id.go_to_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cart_validate_btn = findViewById(R.id.cart_validate_btn);
        cart_validate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Random random = new Random();
                    final int random_number = random.nextInt(900000000);
                    command_hash = new HashMap<>();
                    command_hash.put("uid",mAuth.getCurrentUser().getUid());
                    command_hash.put("time",System.currentTimeMillis());
                    command_hash.put("order_number",random_number);
                    command_hash.put("total_price",Long.parseLong(user_cart_total_price.getText().toString()));
                    mUserRef.child(mAuth.getCurrentUser().getUid()).child("Cart").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                                    cartModels.add(snapshot.getValue(CartModel.class));
                                }
                                if (cartModels.size() >0){
                                    mUserRef.child(mAuth.getCurrentUser().getUid()).child("Commandes").child(String.valueOf(random_number)).setValue(command_hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            HashMap<String,Object> product_hash = new HashMap<>();
                                            for (int i = 0; i <cartModels.size(); i++) {
                                                product_hash.put("key",cartModels.get(i).getKey());
                                                product_hash.put("quantity",cartModels.get(i).getQuantity());
                                                product_hash.put("time",cartModels.get(i).getTime());
                                                product_hash.put("total_price",Long.parseLong(user_cart_total_price.getText().toString()));
                                                mUserRef.child(mAuth.getCurrentUser().getUid()).child("Commandes").child(String.valueOf(random_number)).
                                                        child("Products").child(cartModels.get(i).getKey()).setValue(product_hash);
                                            }
                                            startActivity(new Intent(getApplicationContext(),CartValidateCommandActivity.class).putExtra("order_number",random_number));
                                        }
                                    });
                                }
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            databaseError.toException().printStackTrace();
                        }
                    });
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().hide();
    }


    @Override
    protected void onStart() {
        super.onStart();
        cart_adapter.startListening();
    }
    public class CartHolder extends RecyclerView.ViewHolder {
        private TextView cart_product_name,cart_product_price,cart_product_quantity;
        private ImageView cart_product_image,delete_cart_item_btn,remove_product_quantity,add_product_quantity;
        private Context context;
        private DatabaseReference mProductRef,mUserRef;
        private FirebaseAuth mAuth;
        public long card_price,final_price= 0;
        public CartHolder(@NonNull View itemView) {
            super(itemView);
            mAuth = FirebaseAuth.getInstance();
            context = itemView.getContext();
            mProductRef = FirebaseDatabase.getInstance().getReference("Products");
            mUserRef = FirebaseDatabase.getInstance().getReference("Users");
            cart_product_image  =itemView.findViewById(R.id.cart_product_image);
            cart_product_name  =itemView.findViewById(R.id.cart_product_name);
            cart_product_price  =itemView.findViewById(R.id.cart_product_price);
            cart_product_quantity  =itemView.findViewById(R.id.cart_product_quantity);
            delete_cart_item_btn  =itemView.findViewById(R.id.delete_cart_item_btn);
            add_product_quantity  =itemView.findViewById(R.id.add_product_quantity);
            remove_product_quantity  =itemView.findViewById(R.id.remove_product_quantity);
        }

        public void push(final String key, final int quantity) {
            cart_product_quantity.setText("Ajouter A-"+quantity);
            mProductRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ProductModel productModel = dataSnapshot.getValue(ProductModel.class);
                    Glide.with(context).load(productModel.getProduct_image()).into(cart_product_image);
                    cart_product_name.setText(productModel.getProduct_name());
                    long price = quantity*productModel.getProduct_price();
                    card_price = productModel.getProduct_price();
                    cart_product_price.setText(String.valueOf(price));
                    final_price = final_price+Long.parseLong(cart_product_price.getText().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            delete_cart_item_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUserRef.child(mAuth.getCurrentUser().getUid()).child("Cart").child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, cart_product_name.getText().toString()+" retiré du panier", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            remove_product_quantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int remove_quantity =quantity-1;
                    if (remove_quantity != 0){
                        HashMap<String,Object> update_hash = new HashMap<>();
                        update_hash.put("quantity",remove_quantity);
                        mUserRef.child(mAuth.getCurrentUser().getUid()).child("Cart").child(key).updateChildren(update_hash);
                    }else {
                        mUserRef.child(mAuth.getCurrentUser().getUid()).child("Cart").child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, cart_product_name.getText().toString()+" retiré du panier", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            });
            add_product_quantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int add_quantity = quantity + 1;
                    HashMap<String, Object> update_hash = new HashMap<>();
                    update_hash.put("quantity", add_quantity);
                    mUserRef.child(mAuth.getCurrentUser().getUid()).child("Cart").child(key).updateChildren(update_hash);

                }
            });
        }
    }

}
