package com.foodies.app.Services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class GetAdresseName extends IntentService {
    private ResultReceiver resultReceiver;
    public GetAdresseName() {
        super("GetAdresseName");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent !=null){
            resultReceiver = intent.getParcelableExtra("com.foodies.app"+".RECIVER");
            Location location = intent.getParcelableExtra("com.foodies.app"+".LOCATION_DATA_EXTRA");
            if (location == null){
                return;
            }
            Geocoder geocoder  = new Geocoder(this, Locale.getDefault());
            List<Address> addressList = null;
            try {
                addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                if (addressList != null){
                    Address address = addressList.get(0);
                    ArrayList<String> adress_composent = new ArrayList<>();
                    for (int i = 0; i <address.getMaxAddressLineIndex(); i++) {
                        adress_composent.add(address.getAddressLine(i));
                    }
                    SendToReciver(1, TextUtils.join(Objects.requireNonNull(System.getProperty("line.separator")),adress_composent));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void SendToReciver(int resultcode,String adresse){
        Bundle bundle = new Bundle();
        bundle.putString("ADRESSE_KEY",adresse);
        resultReceiver.send(resultcode,bundle);
    }
}
