package com.foodies.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

public class Caesar_Cipher_Coding extends AppCompatActivity {
    private MaterialEditText caesar_data_edit_text,caesar_shift_edit_text;
    public static TextView caesar_result_text;
    public static StringBuilder stringBuilder = new StringBuilder();
    private RadioGroup radio_group_caesar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caesar__cipher__coding);
        caesar_data_edit_text = findViewById(R.id.caesar_data_edit_text);
        caesar_shift_edit_text = findViewById(R.id.caesar_shift_edit_text);
        caesar_result_text = findViewById(R.id.caesar_result_text);
        radio_group_caesar = findViewById(R.id.radio_group_caesar);
        findViewById(R.id.convert_code_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (caesar_data_edit_text.getText().toString().isEmpty() || caesar_shift_edit_text.getText().toString().isEmpty()){
                    Toast.makeText(Caesar_Cipher_Coding.this, "Please Enter Data", Toast.LENGTH_LONG).show();
                }else{
                    switch (radio_group_caesar.getCheckedRadioButtonId()){
                        case R.id.encode_radio:
                            caesar_result_text.setText(coding_caeser(caesar_data_edit_text.getText().toString(),Integer.parseInt(caesar_shift_edit_text.getText().toString())));
                        break;
                        case R.id.decode_radio:
                            //mazal
                            break;
                    }
                }
            }
        });
        getSupportActionBar().hide();
    }
    static String coding_caeser(String data, int shift){
        stringBuilder.setLength(0);
        for(int i = 0; i < data.length(); i++){
            char character = (char)(data.charAt(i) + shift);
            if (character > 'z'){
                stringBuilder.append((char)(data.charAt(i) - (26-shift)));
            }else
                stringBuilder.append((char)(data.charAt(i) + shift));
        }
        return String.valueOf(stringBuilder).replaceAll("\""," ");
    }
}