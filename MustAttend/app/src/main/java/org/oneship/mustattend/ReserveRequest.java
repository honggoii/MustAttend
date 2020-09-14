package org.oneship.mustattend;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

// 사장님에게 보이는 예약요청화면

public class ReserveRequest extends AppCompatActivity {
    String user_email; //이메일 세션

    // 받은 예약 정보
    String num;
    String purpose; //목적
    String dateMonth;
    String dateDay;
    String timeHour;
    String timeMin;
    String numberOfPeople;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_request);

        //이메일 세션
        Intent intent = getIntent();
        user_email = intent.getExtras().getString("user_email");

        System.out.println("**********ReserveRequestActivity : "+user_email+"***********");
        num = getIntent().getStringExtra("num");
        purpose = getIntent().getStringExtra("purpose"); //activity->activity로 값 전달 받을 때
        dateMonth = getIntent().getStringExtra("dateMonth"); //activity->activity로 값 전달 받을 때
        dateDay = getIntent().getStringExtra("dateDay"); //activity->activity로 값 전달 받을 때
        timeHour = getIntent().getStringExtra("timeHour"); //activity->activity로 값 전달 받을 때
        timeMin = getIntent().getStringExtra("timeMin"); //activity->activity로 값 전달 받을 때
        numberOfPeople = getIntent().getStringExtra("numberOfPeople"); //activity->activity로 값 전달 받을 때


        TextView tempPurpose = findViewById(R.id.temppurpose);
        tempPurpose.append(purpose); // 목적 띄우기
        //tempPurpose.append(purpose.replace("\"","")); // 목적 띄우기

        TextView tempDate = findViewById(R.id.tempdate);
        tempDate.append(dateMonth); // 날짜 띄우기
        //tempDate.append(dateMonth.replace("\"","")); // 날짜 띄우기

        TextView tempTime = findViewById(R.id.temptime);
        tempTime.append(timeHour); // 시간 띄우기
        //tempTime.append(timeHour.replace("\"","")); // 시간 띄우기

        TextView tempNum = findViewById(R.id.tempnum);
        tempNum.append(numberOfPeople); // 인원수 띄우기
        //tempNum.append(numberOfPeople.replace("\"","")); // 인원수 띄우기

        //수락 버튼 누르면 예약 상태 2로
        Button accept = findViewById(R.id.accept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //서버랑 연결
                new JSONTask().execute("http://192.168.0.11:3000/acceptrespond");
            }
        });

        //거절 버튼 누르면 예약 상태 3으로
        Button reject = findViewById(R.id.reject);
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public class JSONTask extends AsyncTask<String, String, String> {
        //인자(string,string,string)=(doInBackground,onProgressUpdate,onPostExecute)자료형
        @Override
        protected String doInBackground(String... urls) {//파라미터 형 string
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("num", num);

                String data = jsonObject.toString();
                HttpURLConnection con = null;
                BufferedReader reader = null;

                System.out.println("***json 데이터"+data);
                try{
                    URL url = new URL(urls[0]);

                    con = (HttpURLConnection) url.openConnection();//url을 연결할 객체
                    System.out.println("**************1*******************");
                    con.setRequestMethod("POST");//POST방식으로 보냄
                    //Request 헤더 값 설정
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    System.out.println("**************2*******************");
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect(); // 연결
                    System.out.println("**************3*******************");
                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();//버퍼를 받아줌

                    System.out.println("**************4*******************");
                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream(); // 입력스트림

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer(); // 데이터 받는 곳

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line); // 데이터 가져오기
                    }

                    System.out.println("*************"+buffer+"*************");
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
            Toast.makeText(getApplicationContext(),"수락 버튼 눌림", Toast.LENGTH_LONG).show();

        }
    }
}