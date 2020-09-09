package com.foodies.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.foodies.app.Fragment.CategorieFragment;
import com.foodies.app.Fragment.DashBoardFragment;
import com.foodies.app.Fragment.OrdersFragment;
import com.foodies.app.Fragment.ProductsListFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class FoodiesManagment extends AppCompatActivity {
    private TabLayout stock_managment_main_tablayout;
    public static ViewPager stock_managment_main_viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodies_managment);
        getSupportActionBar().hide();
        //TabLayout Section
        stock_managment_main_tablayout = findViewById(R.id.stock_managment_main_tablayout);
        stock_managment_main_viewpager = findViewById(R.id.stock_managment_main_viewpager);
        StockTabLayoutAdapter stockTabLayoutAdapter = new StockTabLayoutAdapter(getSupportFragmentManager(),0);
        stockTabLayoutAdapter.Create(new DashBoardFragment(),"TABLEAU DE BORD");
        stockTabLayoutAdapter.Create(new ProductsListFragment(),"PRODUIT");
        stockTabLayoutAdapter.Create(new OrdersFragment(),"COMMANDES");
        stockTabLayoutAdapter.Create(new CategorieFragment(),"MENU");
        stock_managment_main_viewpager.setAdapter(stockTabLayoutAdapter);
        stock_managment_main_tablayout.setupWithViewPager(stock_managment_main_viewpager);
        stock_managment_main_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {
                //we dont need it for now my be later :)
            }
            @Override
            public void onPageSelected(final int position) { new Thread(new Runnable() {
                @SuppressLint("SourceLockedOrientationActivity")
                @Override
                public void run() {
                    if (position == 1){
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                               /* ProductsListFragment.row_product_liste.clear();*/
                            }
                        }).start();
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                }
            }).start();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //we dont need it for now my be later :)
            }
        });
    }
    public class StockTabLayoutAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList = new ArrayList<>();
        private List<String> fragment_names = new ArrayList<>();

        public StockTabLayoutAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragment_names.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragment_names.get(position);
        }
        public void Create(Fragment fr,String name){
            fragmentList.add(fr);
            fragment_names.add(name);
        }
    }
}
