package org.oneship.mustattend;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RealRecommendActivity extends AppCompatActivity {
    String user_email; //로그인 세션
    String owner_email;
    double latitude;//나의 위도
    double longitude;//나의 경도
    List<Map<String,String>> list = new ArrayList<>();//거리,가게 이름 배열
    RecyclerView recyclerView;
    RecommendAdapter adapter;
    // 가게 선택하면 넘겨줄 아이템 변수들
    String email;
    String sel_store_name; // 가게이름
    String sel_store_phonenum; // 전화번호
    String sel_store_parking; // 주차여부
    String sel_store_maxclientnum; // 수용인원


    JsonObject store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_recommend);

        recyclerView = findViewById(R.id.recyclerView2);

        // 리사이클러뷰 모양 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        user_email = getIntent().getStringExtra("user_email"); //이메일 세션 전달 받기
        latitude = getIntent().getDoubleExtra("latitude",0); //이메일 세션 전달 받기
        longitude = getIntent().getDoubleExtra("longitude",0); //이메일 세션 전달 받기
        System.out.println("************추천 화면"+user_email+"****************");
        System.out.println("사용자: 위도, 경도"+latitude+longitude);

        //서버랑 연결
        new JSONTask().execute("http://192.168.43.231:3000/allstore");

        // 어댑터 설정
        adapter = new RecommendAdapter();
       // recyclerView.setAdapter(adapter); // 리사이클러뷰에 어댑터설정

    }

    //정렬
    public Comparator<Map<String, String>> mapComparator = new Comparator<Map<String, String>>() {
        public int compare(Map<String, String> m1, Map<String, String> m2) {
            return m1.get("distance").compareTo(m2.get("distance"));
        }
    };

    public class JSONTask extends AsyncTask<String, String, String> {
        //인자(string,string,string)=(doInBackground,onProgressUpdate,onPostExecute)자료형
        @Override
        protected String doInBackground(String... urls) {//파라미터 형 string
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", "name");

                //String data = jsonObject.toString();
                HttpURLConnection con = null;
                BufferedReader reader = null;

                //System.out.println("***json 데이터"+data);
                try{
                    URL url = new URL(urls[0]);

                    con = (HttpURLConnection) url.openConnection();//url을 연결할 객체
                    System.out.println("RealRecommend: URL OPEN");
                    con.setRequestMethod("POST");//POST방식으로 보냄
                    //Request 헤더 값 설정
                    System.out.println("RealRecommend: set Request Header");
                    con.connect(); // 연결
                    System.out.println("RealRecommend: Connect");

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream(); // 입력스트림

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer(); // 데이터 받는 곳

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line); // 데이터 가져오기
                    }

                    System.out.println("*************RealRecommedActivity 서버로 부터 밭은 값"+buffer+"*************");
                    return buffer.toString();//서버로 부터 받은 값을 string 형변환

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();//버퍼를 닫아줌
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {//UI 스레드 상에서 실행 doInBackground 종료 후 파라미터를 전달 받음
            super.onPostExecute(result);
            System.out.println("result:"+result);

            JSONArray jsonarr = null;

            System.out.println("***************data type************");
            System.out.println(result.getClass().getName());

            // 받은 문자열 파싱해서 JsonArray로
            // 파싱이란? JSON 문자열을 해석해서 자바 객체로 만드는 과정
            JsonParser jsonParser = new JsonParser();
            JsonArray jsonArray = (JsonArray) jsonParser.parse(result);

            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject object = (JsonObject) jsonArray.get(i);
                store = object.get("store").getAsJsonObject();

                // json객체를 json배열로 만들기
                jsonarr = new JSONArray();
                jsonarr.put(store);
                String storename;//가게 이름
                double storelatitude;//가게 위도
                double storelongitude;//가게 경도

                storename = store.get("name").getAsString();
                storelatitude = store.get("locationX").getAsDouble();
                storelongitude = store.get("locationY").getAsDouble();
                owner_email = object.get("email").toString();
                owner_email = owner_email.replaceAll("\"","");
                System.out.println("============================================");
                System.out.println(owner_email);
                System.out.println("============================================");

                // map 객체생성 후 첫번째 가게 이름과 나와의 거리 정보 저장
                Map<String,String> map = null;
                map = new HashMap<>();

                //나와 가게의 거리 계산
                Location locationMy = new Location("pointMy");//나의 위치 정보 객체로 저장
                locationMy.setLatitude(latitude);
                locationMy.setLongitude(longitude);

                Location locationStore = new Location("pointStore");//가게 위치 정보 객체로 저장
                locationMy.setLatitude(storelatitude);
                locationMy.setLongitude(storelongitude);

                double distance = locationMy.distanceTo(locationStore);
                String distanceString = Double.toString(distance);
                //가게이름과 계산된 거리를 map에 저장
                map.put("name",storename);
                map.put("distance",distanceString);
                map.put("phoneNum",store.get("phoneNum").toString());
                map.put("parking",store.get("parking").toString());
                map.put("maxclientnum",store.get("maxclientnum").toString());
                map.put("owner_email", owner_email); //가게등록자 이메일

                //생성한 <storename,distance>를 list에 저장
                list.add(map);
                System.out.println("가게 이름, 거리 map 리스트 :"+list);

            }
            //list 정렬
            Collections.sort(list,mapComparator);
            System.out.println("정렬된 리스트:"+list);

            Map<String, String> myMap ;

            //apdater로 전송

            for (int i = 0; i < list.size(); i++) {
                // 클릭한 가게 찾기위해 잠시
                myMap = list.get(i);
                System.out.println("*****어댑터 설정*****"+myMap.get("name"));
                String name = myMap.get("name");
                System.out.println("^^^^^^^^^^^^^^^^^^^^^^");
                System.out.println(name);
                String Ph = myMap.get("phoneNum");
                String parking= myMap.get("parking");
                String maxClientNum = myMap.get("maxclientnum");
                String owner_email = myMap.get("owner_email");
                adapter.addItem(name);//실제로 텍스트뷰에 올라가는 정보
                adapter.addName(name);//아이템 클릭시 정보를 넘겨주기 위한 임시 정보
                adapter.addphoneNum(Ph);//아이템 클릭시 정보를 넘겨주기 위한 임시 정보
                adapter.addparking(parking);//아이템 클릭시 정보를 넘겨주기 위한 임시 정보
                adapter.addmaxclientnum(maxClientNum);//아이템 클릭시 정보를 넘겨주기 위한 임시 정보
                System.out.println("adapter: "+adapter);

                recyclerView.setAdapter(adapter); // 리사이클러뷰에 어댑터설정

                // 아이템 클릭하면
                adapter.setOnItemClickListener(new OnStoreItemClickListener2() {
                    @Override
                    public void onItemClick(RecommendAdapter.ViewHolder holder, View view, int position) {
                        sel_store_name = adapter.getNames(position); // 가게이름 문자열로
                        sel_store_phonenum = adapter.getPhones(position); // 전화번호 문자열로
                        sel_store_parking = adapter.getParks(position); // 주차여부 문자열로
                        sel_store_maxclientnum = adapter.getNums(position); // 수용인원 문자열로

                        // 가게 페이지로
                        Intent intent = new Intent(RealRecommendActivity.this, StoreActivity.class);
                        intent.putExtra("user_email",user_email); //intent로 다음 activity에 전달할 이메일
                        intent.putExtra("store_name",sel_store_name); //intent로 다음 activity에 전달할 가게이름
                        intent.putExtra("store_phonenum",sel_store_phonenum); //intent로 다음 activity에 전달할 전화번호
                        intent.putExtra("store_parking",sel_store_parking); //intent로 다음 activity에 전달할 주차여부
                        intent.putExtra("store_maxclientnum",sel_store_maxclientnum); //intent로 다음 activity에 전달할 수용인원
                        //AllStore.this.startActivity(intent);
                        startActivity(intent);
                    }
                });
            }



        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, mainUI.class);
        intent.putExtra("user_email",user_email); //intent로 mainUI activity에 전달할 이메일
        startActivity(intent);
    }
}