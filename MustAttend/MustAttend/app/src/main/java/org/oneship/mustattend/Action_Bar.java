package org.oneship.mustattend;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Action_Bar extends AppCompatActivity {
//    BottomNavigationView bottomNavigationView;
//    private CheckReservation fragmentReservation = new CheckReservation();
//    private RecommendActivity fragmentRecommend = new RecommendActivity();
//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("**********Action Bar JAVA Page*********");
//        setContentView(R.layout.activity_action_bar);
//
//        bottomNavigationView = findViewById(R.id.navigationView);
//
//        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,fragmentReservation).commitAllowingStateLoss();
//
//        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
//            @Override
//            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
//                switch (menuItem.getItemId()){
//                    case R.id.RecommendItem:{
//                        getSupportFragmentManager().beginTransaction()
//                                .replace(R.id.frameLayout,fragmentReservation)
//                                .commitAllowingStateLoss();
//                    }
//                    default: break;
//
//                }
//            }
//        });
    }
}
