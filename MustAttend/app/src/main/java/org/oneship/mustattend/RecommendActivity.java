package org.oneship.mustattend;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class RecommendActivity extends Fragment {
    String user_email; //이메일 세션
    double latitude;
    double longitude;
    ViewGroup viewGroup;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull  ViewGroup container,
                             @NonNull Bundle savedInstanceState) {
        viewGroup =(ViewGroup)inflater.inflate(R.layout.activity_recommend, container, false);
        user_email = this.getArguments().getString("user_email"); //이메일 세션 받기
        latitude = this.getArguments().getDouble("latitude");//위도 받기
        longitude = this.getArguments().getDouble("longitude");//위도 받기

        System.out.println("**********이메일 : "+user_email+"***********");
        System.out.println("**********위도 경도 : "+latitude+longitude+"***********");

        //만약 위도랑 경도가 없다면(즉, 사용자가 자신의 위치를 설정하지 않았다면)
        //토스트로 위도를 설정하세요 메세지 띄우기
        if(longitude == 0 && latitude ==0){
            Toast.makeText(getActivity().getApplicationContext(),"위치를 설정해주세요.", Toast.LENGTH_LONG).show();
        }

        else{
            Intent intent = new Intent(getActivity().getApplicationContext(), RealRecommendActivity.class);
            intent.putExtra("user_email",user_email); //intent로 RealRecommend activity에 전달할 이메일
            intent.putExtra("latitude",latitude); //intent로 RealRecommend activity에 전달할 위도
            intent.putExtra("longitude",longitude); //intent로 RealRecommend activity에 전달할 경도
            startActivity(intent);

        }

        return viewGroup;

    }



}
