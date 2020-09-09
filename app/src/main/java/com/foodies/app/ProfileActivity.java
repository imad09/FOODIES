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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.foodies.app.Fragment.FavoriteFragment;
import com.foodies.app.Fragment.ProfileSettingsFragment;
import com.foodies.app.Models.Users;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private TabLayout profile_tablyout;
    private ViewPager profile_viewpager;
    private CardView add_profile_address_btn;
    private FirebaseAuth mAuth;
    private DatabaseReference mUsersRef;
    private LinearLayout profile_adress_layout;
    private TextView profile_adresse_name,profile_username;
    //top Section
    private TextView profile_cart_product_count,profile_created_time,profile_command_count;
    private CircleImageView profile_user_picture;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_profile);
        profile_tablyout = findViewById(R.id.profile_tablayout);
        profile_viewpager = findViewById(R.id.profile_viewpager);
        add_profile_address_btn = findViewById(R.id.add_profile_address_btn);
        profile_adress_layout = findViewById(R.id.profile_adress_layout);
        profile_adresse_name = findViewById(R.id.profile_adresse_name);
        profile_username = findViewById(R.id.profile_username);
        profile_user_picture = findViewById(R.id.profile_user_picture);
        add_profile_address_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ProfileAdresseActivity.class));
            }
        });
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        if (mAuth.getCurrentUser() !=null){
            mUsersRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Users users = dataSnapshot.getValue(Users.class);
                    profile_username.setText(users.getUsername());
                    Glide.with(getApplicationContext()).load(users.getProfile_picture()).into(profile_user_picture);
                    if (users.getAddress() !=null){
                        add_profile_address_btn.setVisibility(View.GONE);
                        profile_adresse_name.setText(users.getAddress());
                    }else {
                        add_profile_address_btn.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    databaseError.toException();
                }
            });
        }
        ProfileAdapter profileAdapter = new ProfileAdapter(getSupportFragmentManager(),0);
        profileAdapter.create(new FavoriteFragment(),"Votre favori");
        profileAdapter.create(new ProfileSettingsFragment(),"Paramètres du compte");
        profile_viewpager.setAdapter(profileAdapter);
        profile_tablyout.setupWithViewPager(profile_viewpager);
        //hide ActionBar
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //Top Section
        profile_cart_product_count = findViewById(R.id.profile_cart_product_count);
        profile_created_time = findViewById(R.id.profile_created_time);
        profile_command_count = findViewById(R.id.profile_command_count);
        //lets Count
        if(mAuth.getCurrentUser() !=null){
            try {
                DatabaseReference mCountRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());
                mCountRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long product_in_cart = dataSnapshot.child("Cart").getChildrenCount();
                        long total_command = dataSnapshot.child("Commandes").getChildrenCount();
                        //get Created Time
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date account_time = new Date(dataSnapshot.child("time").getValue(Long.class));
                        //apply content
                        profile_command_count.setText(String.valueOf(total_command));
                        profile_cart_product_count.setText(String.valueOf(product_in_cart));
                        profile_created_time.setText(simpleDateFormat.format(account_time));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }catch (NullPointerException e){
                e.printStackTrace();
            }

        }
        getSupportActionBar().hide();
    }
    public class ProfileAdapter extends FragmentPagerAdapter{
        private List<Fragment> fragmentList = new ArrayList<>();
        private List<String> fragment_name = new ArrayList<>();

        public ProfileAdapter(@NonNull FragmentManager fm, int behavior) {
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
        public void create(Fragment fr,String name){
            fragment_name.add(name);
            fragmentList.add(fr);
        }
    }
}
