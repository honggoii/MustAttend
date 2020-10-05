package org.oneship.mustattend;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/* 데이터 관리, 리사이클러 뷰 관리 */

// 뷰홀더를 담은 어댑터 상속
public class AllStoreAdapter extends RecyclerView.Adapter<AllStoreAdapter.ViewHolder> {
    ArrayList<String> items = new ArrayList<String>();

    ArrayList<String> names = new ArrayList<String>();
    ArrayList<String> phones = new ArrayList<String>();
    ArrayList<String> parks = new ArrayList<String>();
    ArrayList<String> nums = new ArrayList<String>();
    ArrayList<Bitmap> images = new ArrayList<Bitmap>();

    OnStoreItemClickListener listener;

    static  ImageView imageView2 ;

    //AllStore.java에서 가게 이름 넘겨줌
    public void addItem(String item) {
        System.out.println("item=============="+item);
        items.add(item); // 아이템 추가

    }

    // AllStore.java에서 이미지 경로 넘겨줌
    public void addImage (Bitmap item) {
        images.add(item);
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }
    public void setImages(ArrayList<Bitmap> items) {
        this.images = items;
    }

    // 잠시 가게이름
    public void addName(String item) {
        names.add(item);
    }
    public String getNames(int position) {
        return names.get(position);
    }
    // 잠시 가게번호
    public void addphoneNum (String item) {
        phones.add(item);
    }
    public String getPhones(int position) {
        return phones.get(position);
    }
    // 잠시 가게주차
    public void addparking (String item) {
        parks.add(item);
    }
    public String getParks(int position) {
        return parks.get(position);
    }
    // 잠시 가게인원
    public void addmaxclientnum (String item) {
        nums.add(item);
    }
    public String getNums(int position) {
        return nums.get(position);
    }

    public Bitmap getImage(int position) { return images.get(position); }


    public String getItems(int position) {
        return items.get(position);
    }
    public Bitmap getImages(int position) {
        return images.get(position);
    }


    public void setItem(int position, String item) {
        items.set(position, item);
    }
    public void setImage(int position, Bitmap item) {
        images.set(position, item);
    }


    public void setOnItemClickListener(OnStoreItemClickListener listener) {
        this.listener = listener; // 리스너 할당
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 뷰홀더 생성할 시점에 자동호출
        // 레이아웃 인플레이션해서 뷰홀더에 넣음
        // 하나의 아이템을 위한 뷰홀더를 만드는 과정
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.allstore_item, parent, false);

        return new ViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 올라간걸 아래로 재사용
        // 만들어진 걸 사용
        String item = items.get(position); // 지금 위치 아이템
        Bitmap img = images.get(position); //지금 위치 이미지 경로
        holder.setItem(item);
        holder.setImage(img);
    }

    @Override
    public int getItemCount() {
        // 객체 수
        return items.size();
    }

    // 하나의 아이템도 뷰 -> 뷰를 담아둘 객체가 뷰홀더 (재사용)
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        Bitmap img;

        public ViewHolder (final View itemView, final OnStoreItemClickListener listener) {
            super(itemView);
            // 하나의 아이템 전달
            textView = itemView.findViewById(R.id.textView12);
            imageView = itemView.findViewById(R.id.imageView3);


            // item이(view가) 틀릭되면
            itemView.setOnClickListener(new View.OnClickListener() {

                // 리스너한테 던지기
                @Override
                public void onClick(View v) {
                    // v == itemView
                    int position = getAdapterPosition(); // 몇 번째 뷰인지

                    if(listener !=null) {
                        listener.onItemClick(ViewHolder.this, itemView, position); // 지금 뷰
                    }
                }
            });
        }

        public void setItem(String item) {
            textView.setText(item);

        }

        public void setImage(Bitmap path) {
            imageView.setImageBitmap(path);

        }
    }



}
