package org.oneship.mustattend;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class OwnerReservation extends AppCompatActivity {
    String user_email;
    RecyclerView recyclerView;
    OwnerReservationAdapter adapter; // 어댑터 설정

    String num;
    String purpose;
    Integer dateMonth;
    Integer dateDay;
    Integer timeHour;
    Integer timeMin;
    Integer numberOfPeople;

    // 가게 선택하면 넘겨줄 아이템 변수들
    String sel_num;
    String sel_purpose;
    String sel_dateMonth;
    String sel_dateDay;
    String sel_timeHour;
    String sel_timeMin;
    String sel_numberOfPeople;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_reservation);
        user_email = getIntent().getStringExtra("user_email");
        System.out.println("**********RealReservationActivity : "+user_email+"***********");

        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new OwnerReservationAdapter(); // 어댑터 설정정

        //서버랑 연결
        new JSONTask().execute("http://192.168.0.11:3000/myreserve");

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, OwnerMain.class);
        intent.putExtra("user_email",user_email); //intent로 mainUI activity에 전달할 이메일
        startActivity(intent);
    }

    public class JSONTask extends AsyncTask<String, String, String> {
        //인자(string,string,string)=(doInBackground,onProgressUpdate,onPostExecute)자료형
        @Override
        protected String doInBackground(String... urls) {//파라미터 형 string
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user_email", user_email);

                //String data = jsonObject.toString();
                HttpURLConnection con = null;
                BufferedReader reader = null;

                //System.out.println("***json 데이터"+data);
                try{
                    URL url = new URL(urls[0]);

                    con = (HttpURLConnection) url.openConnection();//url을 연결할 객체
                    System.out.println("**************1*******************");
                    con.setRequestMethod("POST");//POST방식으로 보냄
                    //Request 헤더 값 설정
                    System.out.println("**************2*******************");
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    System.out.println("**************3*******************");
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();
                    System.out.println("*************4********");
                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();//버퍼를 받아줌
                    System.out.println("************5********");
                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);

                    }

                    System.out.println("*************"+buffer+"*************");
                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌

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
            System.out.println("*************여기********");
            System.out.println(result);

            JSONArray jsonarr = null;

            System.out.println("***************data type************");
            System.out.println(result.getClass().getName());

            // 받은 문자열 파싱해서 JsonArray로
            // 파싱이란? JSON 문자열을 해석해서 자바 객체로 만드는 과정
            JsonParser jsonParser = new JsonParser();
            JsonArray jsonArray = (JsonArray) jsonParser.parse(result);


            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject object = (JsonObject) jsonArray.get(i);
                JsonArray reservation = object.get("reservation").getAsJsonArray();
                System.out.println("***************reservation************"+reservation);
                System.out.println("***************뽑자************");

                //예약내역이 없음을 알려주는 부분
                if(reservation.size() == 0 ){
                    Toast.makeText(getApplicationContext(),"예약내역이 없습니다.", Toast.LENGTH_LONG).show();
                    break;
                }

                for(int j=0; j < reservation.size(); j++){
                    JsonObject reservationObj = (JsonObject) reservation.get(j);
                    System.out.println("***************reservationObj************"+reservationObj);
                    num = reservationObj.get("num").getAsString();
                    purpose = reservationObj.get("purpose").getAsString();
                    dateMonth = reservationObj.get("dateMonth").getAsInt();
                    dateDay = reservationObj.get("dateDay").getAsInt();
                    timeHour = reservationObj.get("timeHour").getAsInt();
                    timeMin = reservationObj.get("timeMin").getAsInt();
                    numberOfPeople = reservationObj.get("numberOfPeople").getAsInt();

                    // json객체를 json배열로 만들기
                    jsonarr = new JSONArray();
                    jsonarr.put(num);
                    jsonarr.put(purpose);
                    jsonarr.put(dateMonth);
                    jsonarr.put(dateDay);
                    jsonarr.put(timeHour);
                    jsonarr.put(timeMin);
                    jsonarr.put(numberOfPeople);

                    System.out.println(purpose); // 가게 속성 출력
                    System.out.println(dateMonth); // 가게 속성 출력
                    System.out.println(dateDay); // 가게 속성 출력
                    System.out.println(timeHour); // 가게 속성 출력
                    System.out.println(timeMin); // 가게 속성 출력
                    System.out.println(numberOfPeople); // 가게 속성 출력


                    //.replace("\"","")
                    //문자열에서 특정 문자를 바꾸라는 함수
                    adapter.addCheck(num.toString());
                    adapter.addItem(purpose.toString());
                    adapter.addMonth(dateMonth.toString());
                    adapter.addDay(dateDay.toString());
                    adapter.addHour(timeHour.toString());
                    adapter.addMin(timeMin.toString());
                    adapter.addNum(numberOfPeople.toString());
                    // 가게 이름이 json객체니까 string으로 바꿔서


                    recyclerView.setAdapter(adapter); // 리사이클러뷰에 어댑터설정

                    // 아이템 클릭하면
                    adapter.setOnItemClickListener(new OnStoreItemClickListener3() {
                        @Override
                        public void onItemClick(OwnerReservationAdapter.ViewHolder holder, View view, int position) {
                            sel_num = adapter.getCheck(position); //예약 번호
                            sel_purpose = adapter.getItem(position); // 목적
                            sel_dateMonth = adapter.getMonth(position);
                            sel_dateDay = adapter.getDay(position);
                            sel_timeHour = adapter.getHour(position);
                            sel_timeMin = adapter.getMin(position);
                            sel_numberOfPeople = adapter.getNum(position);

                            // 가게 페이지로
                            Intent intent = new Intent(OwnerReservation.this, ReserveRequest.class);
                            intent.putExtra("num",sel_num);//intent로 다음 activity에 전달할 예약 번호
                            intent.putExtra("user_email",user_email); //intent로 다음 activity에 전달할 이메일
                            intent.putExtra("purpose",sel_purpose); //intent로 다음 activity에 전달할 목적
                            intent.putExtra("dateMonth",sel_dateMonth); //intent로 다음 activity에 전달할 달
                            intent.putExtra("timeHour",sel_timeHour); //intent로 다음 activity에 전달할 시간
                            intent.putExtra("numberOfPeople",sel_numberOfPeople); //intent로 다음 activity에 전달할 수용인원
                            //AllStore.this.startActivity(intent);
                            startActivity(intent);
                        }
                    });
                }


            }
        }
    }

}