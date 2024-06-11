package com.example.doannhung;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class LSActivity extends Activity {
    ImageButton btn_back;
    Button btnXoa;
    ListView listViewTT;
    List<ThongTin> arr_thongtin;
    DTAdapter adapter;
    SQLiteDatabase thongtin_dtb;
    public void onCreate(Bundle savedInstanstate){
        super.onCreate(savedInstanstate);
        setContentView(R.layout.layout_lichsu);

        btnXoa = findViewById(R.id.btnXoa);
        btn_back = findViewById(R.id.back);
        thongtin_dtb = openOrCreateDatabase("qlthongtin.db",MODE_PRIVATE,null);
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hienThongBaoXacNhan();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LSActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        anhXa();

        adapter = new DTAdapter(this,R.layout.dongthongtin,arr_thongtin);
        listViewTT.setAdapter(adapter);
    }
    private void anhXa() {
        listViewTT = findViewById(R.id.listls);
        arr_thongtin = new ArrayList<>();
        Cursor cursor = thongtin_dtb.rawQuery("SELECT * FROM tbthongtin", null);
        int sttIndex = cursor.getColumnIndex("stt");
        int ngayIndex = cursor.getColumnIndex("ngay");
        int gioIndex = cursor.getColumnIndex("gio");
        int redIndex = cursor.getColumnIndex("red");
        int greenIndex = cursor.getColumnIndex("green");
        int blueIndex = cursor.getColumnIndex("blue");
        while (cursor.moveToNext()) {
            if (ngayIndex != -1 && gioIndex != -1 && redIndex != -1 && greenIndex != -1 && blueIndex != -1) {
                String ngay = cursor.getString(ngayIndex);
                String gio = cursor.getString(gioIndex);
                int red = cursor.getInt(redIndex);
                int green = cursor.getInt(greenIndex);
                int blue = cursor.getInt(blueIndex);

                arr_thongtin.add(new ThongTin(ngay, gio, String.valueOf(red), String.valueOf(blue), String.valueOf(green)));
            }
        }
    }
    private void xoaTatCa() {
        thongtin_dtb.execSQL("DELETE FROM tbthongtin");
        Toast.makeText(this, "Đã xóa tất cả thông tin", Toast.LENGTH_SHORT).show();
        arr_thongtin.clear();
        adapter.notifyDataSetChanged();
    }
    private void hienThongBaoXacNhan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận");
        builder.setMessage("Bạn có chắc chắn muốn xóa tất cả thông tin không?");

        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                xoaTatCa();
            }
        });

        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
