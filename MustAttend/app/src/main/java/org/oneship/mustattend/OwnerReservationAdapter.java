package org.oneship.mustattend;


import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OwnerReservationAdapter extends RecyclerView.Adapter<OwnerReservationAdapter.ViewHolder> {
    ArrayList<Integer> states = new ArrayList<Integer>();
    ArrayList<String> items = new ArrayList<String>();
    ArrayList<String> dateMonths = new ArrayList<String>();
    ArrayList<String> dateDays = new ArrayList<String>();
    ArrayList<String> timeHours = new ArrayList<String>();
    ArrayList<String> timeMins = new ArrayList<String>();
    ArrayList<String> numberOfPeoples = new ArrayList<String>();

    OnStoreItemClickListener3 listener;



    public void addState(Integer item){
        states.add(item);
    }
    public void addItem(String item){
        items.add(item);
    }
    public void addMonth(String item){
        dateMonths.add(item);
    }
    public void addDay(String item){
        dateDays.add(item);
    }
    public void addHour(String item){
        timeHours.add(item);
    }
    public void addMin(String item){
        timeMins.add(item);
    }
    public void addNum(String item){
        numberOfPeoples.add(item);
    }


    public Integer getCheck(int position) {
        return states.get(position);
    }
    public String getItem(int position) {
        return items.get(position);
    }
    public String getMonth(int position) {
        return dateMonths.get(position);
    }
    public String getDay(int position) {
        return dateDays.get(position);
    }
    public String getHour(int position) {
        return timeHours.get(position);
    }
    public String getMin(int position) {
        return timeMins.get(position);
    }
    public String getNum(int position) {
        return numberOfPeoples.get(position);
    }

    public void setOnItemClickListener(OnStoreItemClickListener3 listener) {
        this.listener = listener; // 리스너 할당
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ViewHolder 생성할 시점에 자동 호출
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.reservation_item, parent, false);

        return new ViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 지나간 거 재사용
        String item = items.get(position);
        holder.setItem(item);

        String item2 = dateMonths.get(position);
        holder.setMonth(item2);

        String item3 = dateDays.get(position);
        holder.setDay(item3);

        String item4 = timeHours.get(position);
        holder.setHour(item4);

        String item5 = timeMins.get(position);
        holder.setMin(item5);

        String item6 = numberOfPeoples.get(position);
        holder.setNum(item6);

        int item7 = states.get(position);
        if (item7 == 1){
            holder.setImageView1();
        }
        else if (item7 == 2) {
            holder.setImageView2();
        }
        else if (item7 == 3) {
            holder.setImageView3();
        }
    }

    @Override
    public int getItemCount() {
        // 객체의 수
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView textView2;
        TextView textView3;
        TextView textView4;
        TextView textView5;
        TextView textView6;
        ImageView imageView1;
        ImageView imageView2;
        ImageView imageView3;

        public ViewHolder (final View itemView, final OnStoreItemClickListener3 listener) {
            super(itemView);

            // 하나의 아이템 전달
            textView = itemView.findViewById(R.id.purpose);
            textView2 = itemView.findViewById(R.id.month);
            textView3 = itemView.findViewById(R.id.day);
            textView4 = itemView.findViewById(R.id.hour);
            textView5 = itemView.findViewById(R.id.min);
            textView6 = itemView.findViewById(R.id.num);
            imageView1 = itemView.findViewById(R.id.imageView1);
            imageView2 = itemView.findViewById(R.id.imageView2);
            imageView3 = itemView.findViewById(R.id.imageView3);


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
        public void setMonth(String item) {
            textView2.setText(item);
        }
        public void setDay(String item) {
            textView3.setText(item);
        }
        public void setHour(String item) {
            textView4.setText(item);
        }
        public void setMin(String item) {
            textView5.setText(item);
        }
        public void setNum(String item) { textView6.setText(item); }
        public void setImageView1() {
            imageView1.setVisibility(View.VISIBLE);
            imageView2.setVisibility(View.INVISIBLE);
            imageView3.setVisibility(View.INVISIBLE);
        }
        public void setImageView2() {
            imageView1.setVisibility(View.INVISIBLE);
            imageView2.setVisibility(View.VISIBLE);
            imageView3.setVisibility(View.INVISIBLE);
        }
        public void setImageView3() {
            imageView1.setVisibility(View.INVISIBLE);
            imageView2.setVisibility(View.INVISIBLE);
            imageView3.setVisibility(View.VISIBLE);
        }
    }
}