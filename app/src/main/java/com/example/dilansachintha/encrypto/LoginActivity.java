package com.example.dilansachintha.encrypto;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private Button btn_go;
    private EditText txt_pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_go = (Button) findViewById(R.id.btn_go);
        txt_pin = (EditText) findViewById(R.id.txt_pin);

        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txt_pin.getText().toString() != null){
                    DBHelper myDb = new DBHelper(LoginActivity.this);
                    Cursor cur = myDb.getData(1);
                    cur.moveToFirst();
                    if(cur.getString(1).equals(txt_pin.getText().toString())){
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }else{
                    Toast.makeText(LoginActivity.this,"Unsuccessful",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
