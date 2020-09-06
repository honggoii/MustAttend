package org.oneship.mustattend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OwnerMain extends AppCompatActivity {
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_main);
        email = getIntent().getStringExtra("user_email");
        System.out.println("**********OwnerMainActivity : "+email+"***********");

        Button reserveButton = findViewById(R.id.ReserveButton);
        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext() , MyReservation.class);
                intent.putExtra("user_email",email); //intent로 다음 activity에 전달할 이메일
                //액티비티 시작!
                startActivity(intent);
            }
        });
    }
}