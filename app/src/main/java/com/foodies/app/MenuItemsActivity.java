package com.foodies.app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodies.app.Holders.CoffeHolder;
import com.foodies.app.Models.MenuModel;
import com.foodies.app.Models.ProductModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MenuItemsActivity extends AppCompatActivity {
    private DatabaseReference mMenuRef,mProductsRef;
    private TextView category_act_name;
    private FirebaseRecyclerAdapter<ProductModel, CoffeHolder> menu_items_adapter;
    private RecyclerView menu_items_recycler;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_menu_items);
        mMenuRef = FirebaseDatabase.getInstance().getReference("Menu").child(getIntent().getStringExtra("key"));
        category_act_name = findViewById(R.id.category_act_name);
        mMenuRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MenuModel menuModel= dataSnapshot.getValue(MenuModel.class);
                category_act_name.setText(menuModel.getMenu_name().toUpperCase());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //get Items Section
        mProductsRef = FirebaseDatabase.getInstance().getReference("Products");
        Query mProductsQuery = mProductsRef.orderByChild("product_category").equalTo(getIntent().getStringExtra("name"));
        FirebaseRecyclerOptions product_option = new FirebaseRecyclerOptions.Builder<ProductModel>().setQuery(mProductsQuery,ProductModel.class).build();
        menu_items_adapter= new FirebaseRecyclerAdapter<ProductModel, CoffeHolder>(product_option) {
            @Override
            protected void onBindViewHolder(@NonNull CoffeHolder holder, final int i, @NonNull ProductModel model) {
                holder.send(menu_items_adapter.getRef(i).getKey(), model.getProduct_name(),
                        model.getProduct_price(),
                        model.getProduct_description(),
                        model.getProduct_image());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(),Product_Detaille_Activity.class).putExtra("key",menu_items_adapter.getRef(i).getKey()));
                    }
                });
            }

            @NonNull
            @Override
            public CoffeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View coffe_view =LayoutInflater.from(MenuItemsActivity.this).inflate(R.layout.row_coffe_layout,parent,false);
                return new CoffeHolder(coffe_view);
            }
        };
        menu_items_recycler = findViewById(R.id.menu_items_recycler);
        menu_items_recycler.setAdapter(menu_items_adapter);
        menu_items_recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
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
        menu_items_adapter.startListening();
    }
}
