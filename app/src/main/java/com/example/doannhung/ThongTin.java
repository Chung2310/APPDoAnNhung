package com.example.doannhung;

import java.time.LocalDate;
import java.time.LocalTime;

public class ThongTin {
    private String ngay;
    private String gio;
    private String red,blue,green;
    public ThongTin(){}
    public ThongTin(String ngay,String gio,String red,String blue,String green){
        this.ngay = ngay;
        this.gio = gio;
        this.red = red;
        this.blue = blue;
        this.green = green;
    }
    public String getGio(){
        return gio;
    }
    public String getNgay(){
        return ngay;
    }
    public String getRed(){
        return red;
    }
    public String getBlue(){
        return blue;
    }
    public String getGreen(){
        return green;
    }
}
