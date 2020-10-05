package org.oneship.mustattend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Owner_myPage extends AppCompatActivity implements View.OnClickListener {

    RadioButton possible_id; //주차 가능 라디오 버튼
    RadioButton impossible_id; //주차 안돼 라디오 버튼
    String user_email;  //이메일 섹션
    TextView ShopName_id;
    String ShopName;
    TextView ShopAddress_id;
    String ShopAddress;
    EditText ShopPhone_id;
    EditText menu1,menu2,menu3,price1,price2,price3; //대표메뉴, 가격들 2020-06-29
    String ShopPhone;
    TextView ShopPrivateNum_id;
    String ShopParking;
    String ShopPrivateNum;
    EditText Place_id;
    String Place;
    RadioGroup radioGroup_id;
    int radioGroup;
    RadioButton radiobutton;
    String radioText;
    NumberPicker Capacity_id;
    int Capacity;
    ImageView ShopImage;
    Bitmap img;
    byte[] img_byte;
    Button upload_btn;
    String store_name;              //intent로 받을 기존 가게 정보들
    String store_phone_num;
    String store_license;
    String store_parking;
    String store_image;
    String store_address;
    String store_menu1,store_menu2,store_menu3,store_price1,store_price2,store_price3; //이전 activity에서 전달받은 메뉴,가격들 2020-06-29
    int store_maxclientnum;
    Bitmap old_image;   //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_my_page);
        user_email = getIntent().getStringExtra("user_email"); //이메일 섹션 정보 받기
        store_name = getIntent().getStringExtra("store_name"); //가게 이름
        store_address = getIntent().getStringExtra("store_address");  //가게 주소
        store_phone_num = getIntent().getStringExtra("store_phone_num"); //가게 전화번호
        store_license = getIntent().getStringExtra("store_license"); //사업자 등록번호
        store_parking = getIntent().getStringExtra("store_parking"); //주차 가능?
        store_maxclientnum = getIntent().getIntExtra("store_maxclientnum", 0); //수용인원
        store_image = getIntent().getStringExtra("store_image");//가게 사진 String으로 받기
        store_menu1 = getIntent().getStringExtra("menu1"); //대표메뉴 값 받기 2020-06-29
        store_menu2 = getIntent().getStringExtra("menu2");
        store_menu3 = getIntent().getStringExtra("menu3");
        store_price1 = getIntent().getStringExtra("price1"); //가격 값 받기 2020-06-29
        store_price2 = getIntent().getStringExtra("price2");
        store_price3 = getIntent().getStringExtra("price3");

        ShopImage =(ImageView)findViewById(R.id.Shop_imge);
        ShopImage.setImageBitmap(img); //사진 띄우기
        //ShopImage.setImageURI(uri); //사진 띄우기
        new JSONTask_image().execute("");

        //가게 사진
        ShopImage =(ImageView)findViewById(R.id.Shop_imge);
        upload_btn = (Button)findViewById(R.id.upload_button);
        upload_btn.setOnClickListener(new View.OnClickListener() { //업로드 버튼 눌렀을 때
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        //뷰들 선언하기
        findViewById(R.id.register).setOnClickListener(this);
        ShopName_id = (TextView)findViewById(R.id.ShopName) ; //가게이름
        ShopAddress_id = (TextView)findViewById(R.id.ShopAddress) ; //가게 주소
        ShopPhone_id = (EditText)findViewById(R.id.ShopPhone) ; //가게 전화번호
        ShopPrivateNum_id = (TextView)findViewById(R.id.ShopPrivateNum) ; //사업자 등록번호

        radioGroup_id = (RadioGroup)findViewById(R.id.radioGroup); //주차 가능 라디오 그룹 아디 가져오기
        possible_id = (RadioButton)findViewById(R.id.r_btn1); //주차 가능 버튼
        impossible_id = (RadioButton)findViewById(R.id.r_btn2); //주차 안돼 버튼
        Capacity_id = (NumberPicker)findViewById(R.id.Capacity); //수용 인원


        final NumberPicker numberPickerCapacity = (NumberPicker) findViewById(R.id.Capacity);

        numberPickerCapacity.setMaxValue(10000);
        numberPickerCapacity.setMinValue(0);
        numberPickerCapacity.setWrapSelectorWheel(false);
        numberPickerCapacity.setValue(0);

        ShopName_id.setText(store_name) ;
        ShopAddress_id.setText(store_address);
        ShopPhone_id.setText(store_phone_num);
        ShopPrivateNum_id.setText(store_license);
        if(store_parking.equals("가능")){
            possible_id.setChecked(true);
        }
        else{
            impossible_id.setChecked(true);
        }

        Capacity_id.setValue(store_maxclientnum);
        //radiobutton.setChecked(true);
        menu1.setText(store_menu1);
        menu2.setText(store_menu2);
        menu3.setText(store_menu3);
        price1.setText(store_price1);
        price2.setText(store_price2);
        price3.setText(store_price3);

        /*
         * 여기까지는 DB의 가게 정보를 출력해준 거
         * */
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    img = BitmapFactory.decodeStream(in);
                    //bitmap을 byte로 변환
                    img_byte =bitmapToByteArray(img);
                    in.close();
                    // 이미지 띄우기
                    ShopImage.setImageBitmap(img);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //Bitmap을 String로 변환
    public static String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
        byte[] bytes = baos.toByteArray();
        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);
        return temp;
    }
    //String을 Bitmap으로 변환
    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
    //Bitmap을 Byte로 변환
    public byte[] bitmapToByteArray( Bitmap $bitmap ) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        //$bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        return byteArray ;
    }

    //수정 완료 버튼 누르면
    public void onClick(View v){
        switch (v.getId()){
            case R.id.register:
                //textview들의 값들 가져오기
                ShopName = ShopName_id.getText().toString(); //수정한 가게 이름
                //ShopAddress = ShopAddress_id.getText().toString();
                ShopPhone = ShopPhone_id.getText().toString(); //수정한 가게 전화번호
                //ShopPrivateNum = ShopPrivateNum_id.getText().toString();
                //Place = Place_id.getText().toString();
                if(possible_id.isChecked()){
                    ShopParking = possible_id.getText().toString(); //"가능"을 DB에 보내줄것
                }
                else{
                    ShopParking = impossible_id.getText().toString(); //"불가능"을 DB에 보내줄것
                }
                Capacity = Capacity_id.getValue(); //수정한 수용인원
                System.out.println("============="+ShopName+"  "+ShopPhone+"  "+"  "+Capacity+"=========");
                /*
                 * 사진은 나중에
                 * */
                //view에서 대표메뉴, 가격 값 받아오기 2020-06-29
                store_menu1 = menu1.getText().toString();
                store_menu2 = menu2.getText().toString();
                store_menu3 = menu3.getText().toString();
                store_price1 = price1.getText().toString();
                store_price2 = price2.getText().toString();
                store_price3 = price3.getText().toString();
                new JSONTask().execute("http://192.168.43.231:3000/modifystore");
                //사용자가 입력한 정보 텍스트로 변환

                break;


        }
    }

    //이미지를 불러올 쓰레드
    public class JSONTask_image extends AsyncTask<String, String, String> {
        //인자(string,string,string)=(doInBackground,onProgressUpdate,onPostExecute)자료형

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... urls) {//파라미터 형 string
            try {
                URL url = new URL("http://192.168.43.231/uploads/"+user_email+".jpg");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                try {
                    //con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true); //Server 통신에서 입력 가능한 상태로 만듦
                    con.connect(); //연결된 곳에 접속할 때 (connect() 호출해야 실제 통신 가능함)

                    InputStream is = con.getInputStream(); //inputStream 값 가져오기
                    img = BitmapFactory.decodeStream(is); // Bitmap으로 반환

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
        protected void onPostExecute(String result) {//UI 스레드 상에서 실행 doInBackground 종료 후 파라미터를 전달 받음
            super.onPostExecute(result);
            // 이미지 띄우기
            ShopImage.setImageBitmap(img); //이미지 띄울 때는 비트맵 형태로

        }
    }

    public class JSONTask extends AsyncTask<String, String, String> {
        //인자(string,string,string)=(doInBackground,onProgressUpdate,onPostExecute)자료형

        @Override
        protected String doInBackground(String... urls) {//파라미터 형 string
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("email", user_email);
                jsonObject.put("StoreName", ShopName);//가게이름
                //jsonObject.put("StoreAddress", ShopAddress);//가게주소
                jsonObject.put("StorePhone", ShopPhone);//가게 전화번호
                jsonObject.put("parking",ShopParking);//주차 여부
                jsonObject.put("Capacity", Capacity);//수용인원
                jsonObject.put("image", img);//이미지
                jsonObject.put("menu1",store_menu1); //대표메뉴, 가격 json형태로 2020-06-29
                jsonObject.put("menu2",store_menu2);
                jsonObject.put("menu3",store_menu3);
                jsonObject.put("price1",store_price1);
                jsonObject.put("price2",store_price2);
                jsonObject.put("price3",store_price3);


                String data = jsonObject.toString();
                HttpURLConnection con = null;
                BufferedReader reader = null;

                System.out.println("***json 데이터"+data);
                try{
                    URL url = new URL(urls[0]);

                    con = (HttpURLConnection) url.openConnection();//url을 연결할 객체

                    con.setRequestMethod("POST");//POST방식으로 보냄
                    //Request 헤더 값 설정
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음

                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();//버퍼를 받아줌

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

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
            if(result.equals("OK!")) {
                //System.out.println("***************:"+email);

                //AlertDialog.Builder signUpSuccess = new AlertDialog.Builder(getApplicationContext());
                // signUpSuccess.setTitle("가입 완료");
                //signUpSuccess.setMessage("가입이 완료되었습니다.");

                //AlertDialog alert = signUpSuccess.create();
                //alert.show();

                Toast.makeText(getApplicationContext(),"가게 정보 수정 완료", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext() , OwnerMain.class);   //새로고침
                intent.putExtra("user_email",user_email); //intent로 mainUI activity에 전달할 이메일
                //액티비티 시작!
                startActivity(intent);
            }
        }
    }
}