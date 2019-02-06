package com.example.dilansachintha.encrypto;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button btn_confirm = (Button) findViewById(R.id.btn_confirm);
        final EditText txt_pin_I = (EditText) findViewById(R.id.txt_pin_I);
        final EditText txt_pin_II = (EditText) findViewById(R.id.txt_pin_II);

        final DBHelper myDb = new DBHelper(this);
        Cursor cur = myDb.getData(1);

        if(cur.getCount() !=  0){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }else {
            Toast.makeText(RegisterActivity.this, "Enter a pin", Toast.LENGTH_SHORT).show();
            btn_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(txt_pin_I.getText() != null && txt_pin_I.getText().toString().equals(txt_pin_II.getText().toString())){
                        if(myDb.insertPin(txt_pin_I.getText().toString())){
                            Toast.makeText(RegisterActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }else{
                        Toast.makeText(RegisterActivity.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
