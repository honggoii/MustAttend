package org.oneship.mustattend;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import java.util.List;
import java.util.Locale;

public class OwnerSingUp extends AppCompatActivity  {
    String email;
    String pw;
    String phoneNum;
    int Year;
    int month;
    int day;
    EditText email_id, pw_id,StoreName_id,StorePhone_id,StorePrivateNum_id;
    NumberPicker Capacity_id;
    RadioGroup rg;
    Button upload_btn;
    ImageView ShopImage;
    Bitmap img;
    String StoreName,StoreAddress,StorePhone,StorePrivateNum;
    Integer Capacity;
    String parking;
    byte[] img_byte;
    /*2020.09.20*/
    Button Stroe_location_btn;
    double latitude;
    double longitude;
    private GpsTracker gpsTracker;
    TextView textView;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    /*2020.09.20*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_sing_up);
        /*2020.09.20*/
        //가게 위치 설정 버튼이 눌렸을 때
        Stroe_location_btn = (Button)findViewById(R.id.location_button);
        Stroe_location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkLocationServicesStatus()) {

                    showDialogForLocationServiceSetting();
                }else {

                    checkRunTimePermission();
                }

                textView = findViewById(R.id.locationTextView);


                gpsTracker = new GpsTracker(OwnerSingUp.this);

                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
//        args.putDouble("latitude",latitude);
//        args.putDouble("longitude",longitude);
//        recommendActivity.setArguments(args);

                //new OwnerSingUp.JSONTask().execute("http://172.30.1.31:3000/savelocation");
                String address = getCurrentAddress(latitude, longitude);
                textView.setText(address);

                // 현재 위도 경도 확인
                //Toast.makeText(OwnerSingUp.this, "현재위치 \n위도 " + latitude + "\n경도 " + longitude, Toast.LENGTH_LONG).show();
                //Toast.makeText(OwnerSingUp.this, "현재 위치 설정", Toast.LENGTH_LONG).show();


                //위치 저장 완료 메세지 띄우기
                Toast.makeText(getApplicationContext(),"위치가 설정되었습니다.", Toast.LENGTH_LONG).show();
            }
        });
        /*2020.09.20*/
        //가게 사진
        ShopImage =(ImageView)findViewById(R.id.Shop_imge);
        upload_btn = (Button)findViewById(R.id.upload_button);
        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,1);
            }
        });
        //Toast.makeText(getApplicationContext(),email, Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(),pw, Toast.LENGTH_LONG).show();

        //가게 번호에 하이픈 추가
        EditText brand_phone = (EditText) findViewById(R.id.ShopPhone);
        brand_phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        //수용인원NumberPicker
        final NumberPicker numberPickerCapacity = (NumberPicker) findViewById(R.id.Capacity);

        numberPickerCapacity.setMaxValue(100);
        numberPickerCapacity.setMinValue(0);
        numberPickerCapacity.setWrapSelectorWheel(false);
        numberPickerCapacity.setValue(0);

        //주차가능 여부 중 선택된 텍스트 가져오기
        rg = (RadioGroup) findViewById(R.id.radioGroup);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = rg.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton)findViewById(id);
                parking = rb.getText().toString();
            }
        });

        //SignUp에서 보낸 데이터 받기
        //Intent intent = getIntent();
        //email = intent.getExtras().getString("email");
        //pw =intent.getExtras().getString("password");
        //phoneNum = intent.getExtras().getString("phoneNum");
        //Year = intent.getExtras().getInt("Year");
        //month = intent.getExtras().getInt("month");
        //day = intent.getExtras().getInt("day");
        //System.out.println("넘겨온 데이터:"+email+pw+phoneNum+Year+month+day);
        //사용자가 입력한 데이터 id 받기
        email_id = (EditText)findViewById(R.id.email) ;
        pw_id = (EditText)findViewById(R.id.password);
        StoreName_id = (EditText)findViewById(R.id.ShopName);
        /*2020.09.20*/
        //StoreAddress_id = (TextView)findViewById(R.id.locationTextView);
        /*2020.09.20*/
        StorePhone_id = (EditText)findViewById(R.id.ShopPhone);
        StorePrivateNum_id = (EditText)findViewById(R.id.ShopPrivateNum);
        Capacity_id = (NumberPicker)findViewById(R.id.Capacity);

        /*2020.09.26*/
        email_id.addTextChangedListener(new TextWatcher() {
            String emailVal =  "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력되는 텍스트에 변화가 있을 때 호출

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때 호출
                String email = email_id.getText().toString().trim();
                if(email.matches(emailVal) && arg0.length()>0){//이메일 형식이 올바를 때
                    //텍스트의 색을 검정색으로 변경
                    email_id.setTextColor(Color.parseColor("#000000"));

                }
                else{//이메일 형식이 올바르지 않을 때
                    //텍스트의 색을 빨간색으로 변경
                    email_id.setTextColor(Color.parseColor("#F62217"));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에 호출
            }
        });

        pw_id.addTextChangedListener(new TextWatcher() {
            String pwVal = "^(?=.*[a-zA-Z])(?=.*[0-9]).{5,50}$";
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력되는 텍스트에 변화가 있을 때 호출

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때 호출
                String pw = pw_id.getText().toString().trim();
                if(pw.matches(pwVal) && arg0.length()>0){//이메일 형식이 올바를 때
                    //텍스트의 색을 검정색으로 변경
                    pw_id.setTextColor(Color.parseColor("#000000"));

                }
                else{//이메일 형식이 올바르지 않을 때
                    //텍스트의 색을 빨간색으로 변경
                    pw_id.setTextColor(Color.parseColor("#F62217"));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에 호출
            }
        });
        /*2020.09.26*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        // Check which request we're responding to
        /*2020.09.20*/
        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                // GPS 켰는지
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
        /*2020.09.20*/
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
        $bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        return byteArray ;
    }

    //Byte를 Bitmap으로 변환

    //등록완료 버튼이 눌렸을 때
    public void AllregisterCliked(View v){

        //사용자가 입력한 정보 텍스트로 변환
        email = email_id.getText().toString();
        //Toast.makeText(getApplicationContext(),email, Toast.LENGTH_LONG).show();
        pw = pw_id.getText().toString();
        StoreName = StoreName_id.getText().toString();
        /*2020.09.20*/
        //StoreAddress_id -> textView
        StoreAddress= textView.getText().toString();
        System.out.println("주소:"+StoreAddress);
        /*2020.09.20*/
        StorePhone = StorePhone_id.getText().toString();
        StorePrivateNum = StorePrivateNum_id.getText().toString();
        Capacity = Capacity_id.getValue();

        /*2020.09.26*/
        if(email.equals("") || pw.equals("")  || StoreName.equals("")
                || StoreAddress.equals("") || StorePhone.equals("") || StorePrivateNum.equals("")){
            //필수 입력항목이 빈칸일 때
            Toast.makeText(getApplicationContext(), "빈칸을 입력해주세요!",Toast.LENGTH_SHORT).show();
        }
        else{
            new JSONTask().execute("http://192.168.43.231:3000/register");
        }
        /*2020.09.26*/
    }

    public class JSONTask extends AsyncTask<String, String, String> {
        //인자(string,string,string)=(doInBackground,onProgressUpdate,onPostExecute)자료형

        @Override
        protected String doInBackground(String... urls) {//파라미터 형 string
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("email", email);
                jsonObject.put("password", pw);
                jsonObject.put("phoneNum", phoneNum);
                jsonObject.put("Year", Year);
                jsonObject.put("month", month);
                jsonObject.put("day", day);
                jsonObject.put("StoreName", StoreName);//가게이름
                jsonObject.put("StoreAddress", StoreAddress);//가게주소
                jsonObject.put("StorePhone", StorePhone);//가게 전화번호
                jsonObject.put("StorePrivateNum", StorePrivateNum);//사업자 등록 번호
                jsonObject.put("parking",parking);//주차 여부
                jsonObject.put("Capacity", Capacity);//수용인원
                jsonObject.put("image", img);//이미지
                /*2020.09.20*/
                jsonObject.put("locationX", latitude);//위도
                jsonObject.put("locationY", longitude);//경도
                /*2020.09.20*/
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
            System.out.println("====================="+result+"===================");
            if(result.equals("OK!")) {
                //System.out.println("***************:"+email);

                //AlertDialog.Builder signUpSuccess = new AlertDialog.Builder(getApplicationContext());
                // signUpSuccess.setTitle("가입 완료");
                //signUpSuccess.setMessage("가입이 완료되었습니다.");

                //AlertDialog alert = signUpSuccess.create();
                //alert.show();

                Toast.makeText(getApplicationContext(),"가입완료", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext() , UploadStoreImage.class);
                intent.putExtra("user_email",email); //intent로 mainUI activity에 전달할 이메일
                //액티비티 시작!
                startActivity(intent);
            }
        }
    }
    /*2020.09.20*/
    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            boolean check_result = true;
            // 권한 체크
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

            }
            else {
                // 거부한 권한
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(OwnerSingUp.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                }else {

                    Toast.makeText(OwnerSingUp.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        // 위치 권한 체크
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(OwnerSingUp.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(OwnerSingUp.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {


        } else {  // 권한 요청

            // 사용자가 권한 거절해놨으면
            if (ActivityCompat.shouldShowRequestPermissionRationale(OwnerSingUp.this, REQUIRED_PERMISSIONS[0])) {

                Toast.makeText(OwnerSingUp.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 권한 요청
                ActivityCompat.requestPermissions(OwnerSingUp.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 사용자가 권한 거절한 적 없으면
                ActivityCompat.requestPermissions(OwnerSingUp.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    public String getCurrentAddress( double latitude, double longitude) {

        // 지오코더
        // GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }


    // GPS 활성화
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(OwnerSingUp.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }



    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    /*2020.09.20*/
}