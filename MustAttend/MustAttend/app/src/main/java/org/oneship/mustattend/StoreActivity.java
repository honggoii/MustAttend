package org.oneship.mustattend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class StoreActivity extends AppCompatActivity {
    LinearLayout container;
    private ListView mListView;

    String user_email; //이메일 세션
    Bitmap img = null;

    // 받은 가게 정보
    String email;
    String store_name; // 가게이름
    String store_phonenum; // 전화번호
    String store_parking; // 주차여부
    String store_maxclientnum; // 수용인원
    String menu1, menu2, menu3, price1, price2, price3; //메뉴 및 가격
    String owner_email; //가게등록자의 이메일

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        menu1 = getIntent().getStringExtra("menu1");
        menu2 = getIntent().getStringExtra("menu2");
        menu3 = getIntent().getStringExtra("menu3");
        price1 = getIntent().getStringExtra("price1");
        price2 = getIntent().getStringExtra("price2");
        price3 = getIntent().getStringExtra("price3");

        TextView menu1_btn = findViewById(R.id.menu1);
        TextView menu2_btn = findViewById(R.id.menu2);
        TextView menu3_btn = findViewById(R.id.menu3);
        TextView price1_btn = findViewById(R.id.price1);
        TextView price2_btn = findViewById(R.id.price2);
        TextView price3_btn = findViewById(R.id.price3);

        //이메일 세션
        Intent intent = getIntent();
        user_email = intent.getExtras().getString("user_email");
        owner_email = intent.getExtras().getString("owner_email");

        System.out.println("**********StoreActivity : "+user_email+"***********");

        store_name = getIntent().getStringExtra("store_name"); //activity->activity로 값 전달 받을 때
        TextView name = (TextView) findViewById(R.id.txtname);
        name.setText(store_name.replace("\"",""));
        menu1_btn.setText(menu1.replace("\"",""));
        menu2_btn.setText(menu2.replace("\"",""));
        menu3_btn.setText(menu3.replace("\"",""));
        price1_btn.setText(price1.replace("\"",""));
        price2_btn.setText(price2.replace("\"",""));
        price3_btn.setText(price3.replace("\"",""));

        container = findViewById(R.id.container);

        container.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sub_menu, container, true);
        addLayout1();

        Button button1 = findViewById(R.id.register);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLayout1();
            }
        });

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLayout2();

            }
        });

        Button button3 = findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLayout3();

            }
        });

    }

    public void addLayout1() {
        container.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sub_menu, container, true);

        TextView menu1_btn = findViewById(R.id.menu1);
        TextView menu2_btn = findViewById(R.id.menu2);
        TextView menu3_btn = findViewById(R.id.menu3);
        TextView price1_btn = findViewById(R.id.price1);
        TextView price2_btn = findViewById(R.id.price2);
        TextView price3_btn = findViewById(R.id.price3);

        ImageView ShopImage =(ImageView)findViewById(R.id.imageView3);

        try {
            img = new AllStore.JSONTask_image().execute("http://192.168.43.231/uploads/"+owner_email+".jpg").get();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        ShopImage.setImageBitmap(img); //사진 띄우기

        menu1_btn.append(menu1.replace("\"",""));
        menu2_btn.append(menu2.replace("\"",""));
        menu3_btn.append(menu3.replace("\"",""));
        price1_btn.append(price1.replace("\"",""));
        price2_btn.append(price2.replace("\"",""));
        price3_btn.append(price3.replace("\"",""));

    }

    public void addLayout2() {
        container.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sub_resv, container, true);

        store_phonenum = getIntent().getStringExtra("store_phonenum"); //activity->activity로 값 전달 받을 때
        store_parking = getIntent().getStringExtra("store_parking"); //activity->activity로 값 전달 받을 때
        store_maxclientnum = getIntent().getStringExtra("store_maxclientnum"); //activity->activity로 값 전달 받을 때

        TextView textview_phone = findViewById(R.id.txtphone);
        TextView textView_parking = findViewById(R.id.txtparking);
        TextView textView_num = findViewById(R.id.txtnum);


        textview_phone.append(store_phonenum.replace("\"","")); // 전화번호 뿌리기
        textView_parking.append(store_parking.replace("\"","")); // 주차여부 뿌리기
        textView_num.append(store_maxclientnum.replace("\"","")); // 수용인원 뿌리기

        Button button = findViewById(R.id.btn_reserve);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoreActivity.this, BookingActivity.class);
                intent.putExtra("user_email",user_email); //intent로 다음 activity에 전달할 이메일
                intent.putExtra("store_name",store_name); //intent로 다음 activity에 전달할 이메일
                StoreActivity.this.startActivity(intent);
            }
        });
    }

    public void addLayout3() {
        container.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sub_revi, container, true);
        /* 위젯과 멤버변수 참조 획득 */
        mListView = (ListView)findViewById(R.id.listView);

        /* 아이템 추가 및 어댑터 등록 */
        dataSetting();
    }

    private void dataSetting(){

        MyAdapter mMyAdapter = new MyAdapter();


        for (int i=1; i<10; i++) {
            mMyAdapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon), "리뷰_" + i, "별점_" + i);
        }

        /* 리스트뷰에 어댑터 등록 */
        mListView.setAdapter(mMyAdapter);
    }

    public void onReservButtonClicked (View v){
        Toast.makeText(this,"예약 신청이 완료되었습니다.", Toast.LENGTH_LONG).show();
    }

    //이미지를 불러올 쓰레드
    public class JSONTask_image extends AsyncTask<String, String, Bitmap> {
        //인자(string,string,string)=(doInBackground,onProgressUpdate,onPostExecute)자료형
        Bitmap img;
        @SuppressLint("WrongThread")
        @Override
        protected Bitmap doInBackground(String... urls) {//파라미터 형 string
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                try {
                    //con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true); //Server 통신에서 입력 가능한 상태로 만듦
                    con.connect(); //연결된 곳에 접속할 때 (connect() 호출해야 실제 통신 가능함)

                    InputStream is = con.getInputStream(); //inputStream 값 가져오기
                    img = BitmapFactory.decodeStream(is); // Bitmap으로 반환

                    return img;

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if(con != null){
                        con.disconnect();
                    }
                }



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }

        private String getRealPathFromURI(Uri contentURI) {
            String result;
            Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);

            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentURI.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                cursor.close();
            }
            return result;
        }


        @Override
        protected void onPostExecute(Bitmap result) {//UI 스레드 상에서 실행 doInBackground 종료 후 파라미터를 전달 받음
            super.onPostExecute(result);
            // 이미지 띄우기
            //ShopImage.setImageBitmap(img); //이미지 띄울 때는 비트맵 형태로

        }
    }

}