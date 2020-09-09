package com.foodies.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.foodies.app.MainActivity;
import com.foodies.app.ProfileAdresseActivity;
import com.foodies.app.R;
import com.foodies.app.UserCommandesListActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.processphoenix.ProcessPhoenix;

public class ProfileSettingsFragment extends Fragment {
    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View settings_view = inflater.inflate(R.layout.fragment_settings,container,false);
        mAuth = FirebaseAuth.getInstance();
        settings_view.findViewById(R.id.profile_location_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ProfileAdresseActivity.class));
            }
        });
        settings_view.findViewById(R.id.setting_command_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UserCommandesListActivity.class));
            }
        });
        settings_view.findViewById(R.id.profile_logout_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent main = new Intent(getActivity(),MainActivity.class);
                ProcessPhoenix.triggerRebirth(getActivity(),main);
            }
        });
        return settings_view;
    }
}

