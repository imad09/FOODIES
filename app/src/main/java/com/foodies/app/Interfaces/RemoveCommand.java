package com.foodies.app.Interfaces;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public interface RemoveCommand {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid()).child("Commandes");

    public static void remove(long number){
        mUserRef.child(String.valueOf(number)).removeValue();
    }
}
