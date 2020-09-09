package com.foodies.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.foodies.app.Models.TourModel;
import com.google.firebase.auth.FirebaseAuth;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;

public class AppTourActivity extends AppCompatActivity {
    private ViewPager tourviewpager;
    private DotsIndicator tour_tablayout;
    private List<TourModel> tourModels = new ArrayList<>();
    private TourPagerAdapter tourPagerAdapter;
    private CardView get_stared_btn;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_app_tour);
        getSupportActionBar().hide();
        tour_tablayout = findViewById(R.id.tour_tablayout);
        tourviewpager = findViewById(R.id.tourviewpager);
        get_stared_btn = findViewById(R.id.get_stared_btn);
        tourModels.add(new TourModel("MPHARMA,Pharmacie en ligne"
                ,"Vous pouvez désormais acheter des médicaments sans vous rendre à la pharmacie uniquement depuis votre téléphone",
                "https://bit.ly/2De8vVK"));
        tourModels.add(new TourModel("Achetez en un seul clic!",
                "Vous pouvez sélectionner facilement le produit et l'acheter avec une excellente expérience utilisateur",
                "https://bit.ly/3hXx2x4"));
        tourModels.add(new TourModel("Obtenez votre propre carte Pharma Pass",
                "Nous avons une nouvelle méthode de paiement, Pharmapass Vous pouvez acheter avec elle depuis l'application",
                "https://bit.ly/30rjSmi"));
        tourPagerAdapter = new TourPagerAdapter(getApplicationContext(),tourModels);
        tourviewpager.setAdapter(tourPagerAdapter);
        tour_tablayout.setViewPager(tourviewpager);
        tourviewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                get_stared_btn.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //ShareD preferences
        sharedPreferences = getApplicationContext().getSharedPreferences("PharmaIntro",0);
        editor = sharedPreferences.edit();
        get_stared_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("type","clean");
                editor.apply();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });
    }


    public class TourPagerAdapter extends PagerAdapter {
        private Context context;
        private List<TourModel> tourModels;

        public TourPagerAdapter(Context context, List<TourModel> tourModels) {
            this.context = context;
            this.tourModels = tourModels;
        }

        @Override
        public int getCount() {
            return tourModels.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View)object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View tour_view = LayoutInflater.from(context).inflate(R.layout.tour_pages,null);
            ImageView tour_layout_image = tour_view.findViewById(R.id.tour_layout_image);
            TextView tour_layout_name = tour_view.findViewById(R.id.tour_layout_name);
            TextView tour_layout_description = tour_view.findViewById(R.id.tour_layout_description);
            tour_layout_name.setText(tourModels.get(position).getName());
            tour_layout_description.setText(tourModels.get(position).getDescription());
            Glide.with(context).load(tourModels.get(position).getImage()).into(tour_layout_image);
            container.addView(tour_view);
            return tour_view;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //check pref
        if (sharedPreferences.getString("type","Nan").equals("clean")){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }else {
            Log.e("Intro", "onStart: First Run");
        }
    }
}
