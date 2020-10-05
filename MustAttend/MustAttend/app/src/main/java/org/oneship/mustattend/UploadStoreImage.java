package org.oneship.mustattend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONObject;
import org.oneship.mustattend.R;
import org.oneship.mustattend.mainUI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UploadStoreImage extends AppCompatActivity {

    final int REQ_CODE_SELECT_IMAGE = 100;

    ImageView store_imageview; //가세 사진 이미지뷰
    Button select_btn; //사진 선택 버튼
    Button upload_btn; //업로드 버튼
    String getImgURL = ""; //이미지 경로
    String getImgName = ""; //이미지 이름
    String email; //이메일 세션
    Uri uri; //이미지의 uri
    Bitmap img_bitmap; //이미지 비트맵
    byte[] img_byte;    //이미지 바이트 배열


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_store_image);

        email = getIntent().getStringExtra("user_email"); //OwnerSingUp.java로부터 이메일 세션 정보 받기

        //가게 사진
        store_imageview = (ImageView) findViewById(R.id.store_image); //가게사진 뷰 id 가져오기
        select_btn = (Button) findViewById(R.id.selectbtn); //'갤러리' 버튼
        upload_btn = (Button) findViewById(R.id.uploadbtn); //'사진 등록' 버튼

        //'갤러리' 버튼 리스너
        select_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                //intent.setType("image/*");
                intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                System.out.println("=====================================================");
                System.out.println("갤러리 버튼 클릭함");
                System.out.println("=====================================================");
                startActivityForResult(intent,1);

            }
        });

        //'사진 등록' 버튼 이벤트 리스너
        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("=====================================================");
                System.out.println("사진 등록 버튼 눌림");
                System.out.println("=====================================================");
                //JSONTask()로 이동
                new  UploadStoreImage.JSONTask().execute("http://192.168.43.231:3000/UploadImage");

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                try {

                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    img_bitmap = BitmapFactory.decodeStream(in);
                    //bitmap을 byte로 변환
                    img_byte =bitmapToByteArray(img_bitmap);
                    in.close();
                    // 이미지 띄우기
                    store_imageview.setImageBitmap(img_bitmap); //이미지 띄울 때는 비트맵 형태로

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*public static String saveBitmapToJpeg(Context context, Bitmap bitmap){

        File storage = context.getCacheDir(); // 이 부분이 임시파일 저장 경로

        //String fileName =  + ".jpg"; // 파일이름은 마음대로!

        File tempFile = new File(storage,fileName);

        try{
            tempFile.createNewFile(); // 파일을 생성해주고

            FileOutputStream out = new FileOutputStream(tempFile);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 90 , out); // 넘거 받은 bitmap을 jpeg(손실압축)으로 저장해줌

            out.close(); // 마무리로 닫아줍니다.

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tempFile.getAbsolutePath(); // 임시파일 저장경로를 리턴해주면 끝!
    }*/


    // 선택된 이미지 파일명 가져오기
    /*public String getImageNameToUri(Uri data) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);

        getImgURL = imgPath;
        getImgName = imgName;

        System.out.println("=====================================================");
        System.out.println("getImgURL : "+getImgURL+" getImgName : "+getImgName);
        System.out.println("=====================================================");

        return "success";
    }*/
    //이미지 절대경로 가져오기
    public String getRealPathFromURI(Uri contentURI) {
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


    public class JSONTask extends AsyncTask<String, String, String> {
        //인자(string,string,string)=(doInBackground,onProgressUpdate,onPostExecute)자료형

        @Override
        protected String doInBackground(String... urls) {//파라미터 형 string
            try {
                //이미지 전송에 필요한 문자열들
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int maxBufferSize = 1 * 1024 * 1024;


                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("email", email); //사용자의 이메일

                String data = jsonObject.toString();
                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    URL url = new URL(urls[0]);
                    String jpg = email.concat(".jpg");  //jpg 확장자 붙이기
                    con = (HttpURLConnection) url.openConnection();//url을 연결할 객체

                    con.setRequestMethod("POST");//POST방식으로 보냄
                    //Request 헤더 값 설정
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    //2020-09-11
                    //con.setRequestProperty("image", jpg);
                    con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    con.setRequestProperty("ENCTYPE", "multipart/form-data");
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음

                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    //con.connect();

                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    //사용자 이메일 서버에 전송
                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"email\"\r\n\r\n" + email);
                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + jpg + "\"" + lineEnd);
                    wr.writeBytes("Content-Type: " + "image/jpg" + lineEnd);
                    wr.writeBytes(lineEnd);
                    //wr.writeBytes("Content-Type: application/octet-stream\r\n\r\n");

                    //FileInputStream fileInputStream = new FileInputStream(getImgURL); //이미지 절대 경로 넣어놓음
                    //int bytesAvailable = fileInputStream.available();
                    //int bufferSize = Math.min(bytesAvailable, maxBufferSize);

                    byte[] buffer = new byte[maxBufferSize];
                    //int bytesRead = fileInputStream.read(buffer, 0, maxBufferSize);
                    //while (bytesRead > 0)
                    //{
                    // Upload file part(s)
                    DataOutputStream dataWrite = new DataOutputStream(con.getOutputStream());
                    dataWrite.write(img_byte, 0, img_byte.length);
                    //  bytesAvailable = fileInputStream.available();
                    // bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    // bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    //}
                    //서버에 텍스트+이미지 전송
                    //wr.writeBytes(lineEnd);
                    wr.writeBytes("\r\n--" + boundary + "--\r\n");
                    wr.flush();

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer2 = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer2.append(line);
                    }


                    System.out.println("=====================================================");
                    System.out.println("Line : "+buffer2);
                    System.out.println("=====================================================");
                    return buffer2.toString();//서버로 부터 받은 값을 리턴해줌

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
            if(result.equals("OK!")) {
                Toast.makeText(getApplicationContext(),"가입완료", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext() , OwnerMain.class);
                intent.putExtra("user_email",email); //intent로 mainUI activity에 전달할 이메일
                //액티비티 시작!
                startActivity(intent);
            }
        }
    }
    //Bitmap을 Byte로 변환
    public byte[] bitmapToByteArray( Bitmap $bitmap ) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        $bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        return byteArray ;
    }


}
