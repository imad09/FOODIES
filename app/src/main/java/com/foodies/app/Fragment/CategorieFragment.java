package com.foodies.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodies.app.Holders.CategoriesListHolder;
import com.foodies.app.Models.MenuModel;
import com.foodies.app.R;
import com.foodies.app.Stock_Add_Category;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class CategorieFragment extends Fragment {
    private RecyclerView category_recycler;
    private DatabaseReference mCategoryRef;
    private FirebaseRecyclerAdapter<MenuModel, CategoriesListHolder> categories_adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View cat_view = inflater.inflate(R.layout.fragment_category,container,false);
        category_recycler = cat_view.findViewById(R.id.category_recycler);
        mCategoryRef = FirebaseDatabase.getInstance().getReference("Menu");
        Query category_query = mCategoryRef.orderByKey();
        FirebaseRecyclerOptions categorie_option = new FirebaseRecyclerOptions.Builder<MenuModel>().setQuery(category_query,MenuModel.class).build();
        categories_adapter= new FirebaseRecyclerAdapter<MenuModel, CategoriesListHolder>(categorie_option) {
            @Override
            protected void onBindViewHolder(@NonNull CategoriesListHolder holder, int i, @NonNull MenuModel model) {
                holder.send(model.getMenu_image(),model.getMenu_name(),categories_adapter.getRef(i).getKey());
            }

            @NonNull
            @Override
            public CategoriesListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View sec_view = LayoutInflater.from(getActivity()).inflate(R.layout.row_categorie_list,parent,false);
                return new CategoriesListHolder(sec_view);
            }
        };
        category_recycler.setAdapter(categories_adapter);
        category_recycler.setLayoutManager(new GridLayoutManager(getActivity(),2));
        cat_view.findViewById(R.id.add_category_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Stock_Add_Category.class));
                getActivity().overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });
        return cat_view;
    }

    @Override
    public void onStart() {
        super.onStart();
        categories_adapter.startListening();
    }
}
