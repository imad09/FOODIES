package com.foodies.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.foodies.app.FoodiesManagment;
import com.foodies.app.Models.DashboardModel;
import com.foodies.app.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class DashBoardFragment extends Fragment {
    private List<DashboardModel> dashboardModels = new ArrayList<>();
    private RecyclerView dashboard_recycler;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dashboard_view = inflater.inflate(R.layout.fragment_dashboard,container,false);
        dashboardModels.add(new DashboardModel("https://bit.ly/31Ky894","Produits totaux"));
        dashboardModels.add(new DashboardModel("https://bit.ly/3ahLVrD","Menu totales"));
        dashboardModels.add(new DashboardModel("https://bit.ly/2E0DYuU","Nombre de commandes"));
        dashboardModels.add(new DashboardModel("https://bit.ly/31IsUus","Nombre d'utilisateurs"));
        dashboard_recycler = dashboard_view.findViewById(R.id.dashboard_recycler);
        dashboard_recycler.setAdapter(new DashboardAdapter(getActivity(),dashboardModels));
        dashboard_recycler.setLayoutManager(new GridLayoutManager(getActivity(),2));
        //recycler
        return dashboard_view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
    public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashboardHolder>{
        private Context context;
        private List<DashboardModel> dashboardModels;
        private DatabaseReference mCountRef;

        public DashboardAdapter(Context context, List<DashboardModel> dashboardModels) {
            this.context = context;
            this.dashboardModels = dashboardModels;
        }

        @NonNull
        @Override
        public DashboardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View dash_view = LayoutInflater.from(context).inflate(R.layout.row_dashboard_layout,parent,false);
            return new DashboardHolder(dash_view);
        }

        @Override
        public void onBindViewHolder(@NonNull DashboardHolder holder, int position) {
            holder.dashboard_name.setText(dashboardModels.get(position).getDash_name());
            Glide.with(context).load(dashboardModels.get(position).getImage_url()).into(holder.dashboard_image);
            //counting
            mCountRef = FirebaseDatabase.getInstance().getReference();
            mCountRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long products = dataSnapshot.child("Products").getChildrenCount();
                    long categories = dataSnapshot.child("Menu").getChildrenCount();
                    long users = dataSnapshot.child("Users").getChildrenCount();
                    long commands = dataSnapshot.child("Commands").getChildrenCount();
                    switch (holder.getAdapterPosition()){
                        case 0:
                            holder.dashoard_count.setText(products+" produits");
                            break;
                        case 1:
                            holder.dashoard_count.setText(categories+" Menu");
                            break;
                        case 2:
                            holder.dashoard_count.setText(commands+" Commandes");
                            break;
                        case 3:
                            holder.dashoard_count.setText(users+" utilisateurs");
                            break;

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return dashboardModels.size();
        }

        class DashboardHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            private TextView dashboard_name,dashoard_count;
            private ImageView dashboard_image;
            public DashboardHolder(@NonNull View itemView) {
                super(itemView);
                dashboard_image = itemView.findViewById(R.id.dashboard_image);
                dashboard_name = itemView.findViewById(R.id.dashboard_name);
                itemView.setOnClickListener(this);
                dashoard_count = itemView.findViewById(R.id.dashboard_count);
            }

            @Override
            public void onClick(View v) {
                dashboardModels.clear();
                switch (getAdapterPosition()){
                    case 0:
                        FoodiesManagment.stock_managment_main_viewpager.setCurrentItem(1);
                        break;
                    case 1:
                        FoodiesManagment.stock_managment_main_viewpager.setCurrentItem(3);
                        break;
                    case 2:
                        FoodiesManagment.stock_managment_main_viewpager.setCurrentItem(2);
                        break;
                }
            }
        }
    }
}
