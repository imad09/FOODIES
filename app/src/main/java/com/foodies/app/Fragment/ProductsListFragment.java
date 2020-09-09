package com.foodies.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodies.app.Holders.ProductListHolder;
import com.foodies.app.Models.ProductModel;
import com.foodies.app.R;
import com.foodies.app.Stock_Add_Product;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class ProductsListFragment extends Fragment {
    private RecyclerView product_liste_recycler;
    public static List<ProductModel> row_product_liste = new ArrayList<>();
    public static FirebaseRecyclerAdapter<ProductModel, ProductListHolder> product_fb_adapter;
    public static DatabaseReference mProductref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View product_view = inflater.inflate(R.layout.fragment_product_list, container, false);
        product_liste_recycler = product_view.findViewById(R.id.product_liste_recycler);
        //Retrive Products
        //TODO max Product ID 13 char

        mProductref = FirebaseDatabase.getInstance().getReference("Products");
        Query product_query = mProductref.orderByKey();
        FirebaseRecyclerOptions mOption = new FirebaseRecyclerOptions.Builder<ProductModel>().setQuery(product_query, ProductModel.class).build();
        product_fb_adapter = new FirebaseRecyclerAdapter<ProductModel, ProductListHolder>(mOption) {
            @Override
            protected void onBindViewHolder(@NonNull ProductListHolder holder, int i, @NonNull ProductModel model) {
                holder.send(model.getProduct_key(), model.getProduct_name(), model.getProduct_id(), model.getProduct_quantity(),
                        model.getProduct_price(),
                        holder, model.getProduct_category(),model.getProduct_added_time());
            }

            @NonNull
            @Override
            public ProductListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View p_view = LayoutInflater.from(getActivity()).inflate(R.layout.row_product_liste, parent, false);
                return new ProductListHolder(p_view);
            }
        };
        product_liste_recycler.setAdapter(product_fb_adapter);
        product_liste_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        product_view.findViewById(R.id.add_product_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Stock_Add_Product.class));
            }
        });
        return product_view;
    }

    @Override
    public void onStart() {
        super.onStart();
        product_fb_adapter.startListening();
    }
}
