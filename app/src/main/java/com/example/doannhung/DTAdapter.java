package com.example.doannhung;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

public class DTAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<ThongTin> ttList;

    public DTAdapter(Context context,int layout,List<ThongTin> ttList){
        this.context = context;
        this.layout = layout;
        this.ttList = ttList;
    }
    @Override
    public int getCount() {
        return ttList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    private class ViewHolder{
        TextView ngay;
        TextView gio;
        TextView red,blue,green;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout,null);

            holder = new ViewHolder();
            holder.ngay =(TextView) convertView.findViewById(R.id.viewNgay);
            holder.gio = (TextView) convertView.findViewById(R.id.viewGio);
            holder.red = (TextView) convertView.findViewById(R.id.viewRed);
            holder.blue = (TextView) convertView.findViewById(R.id.viewBlue);
            holder.green = (TextView) convertView.findViewById(R.id.viewGreen);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }
        ThongTin thongTin = ttList.get(position);
        holder.ngay.setText(thongTin.getNgay());
        holder.gio.setText(thongTin.getGio());
        holder.red.setText(thongTin.getRed());
        holder.blue.setText(thongTin.getBlue());
        holder.green.setText(thongTin.getGreen());
        return convertView;
    }
}
