package com.example.doannhung;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.doannhung.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UnknownFormatConversionException;

public class MainActivity extends Activity {
    Button buttontang,buttongiam,button_onoff,button_reset;
    ImageView imageView_red,imageView_blue,imageView_green;
    TextView textView_speed,textView_sum,textView_red,textView_blue,textView_green,textView_time,textView_day;
    RelativeLayout relativeLayout;
    String[] arr = new String[5];
    Handler handler;
    VideoView videoView;
    boolean isColorChanged = false;
    Integer speed=3;
    int chedo=1;
    int red,blue,green,redl,bluel,greenl;
    private String MQTTHOST = "tcp://103.180.149.239:1883";
    private MqttAndroidClient client;
    private MqttConnectOptions options;
    private String topic = "chuoi_json";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ánh Xạ đến các view
        anhXa();

        handler = new Handler();
        handler.post(updateTimeViewRunnable);

        startVideo();

        String clientId = MqttClient.generateClientId();
        clientId = String.valueOf(new MqttAndroidClient(this.getApplicationContext(),MQTTHOST,clientId));

        client = new MqttAndroidClient(this.getApplicationContext(),MQTTHOST,clientId);

        options = new MqttConnectOptions();

        try{
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    SUB(client,topic);
                    Toast.makeText(MainActivity.this,"CONNECTED",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this,"DISCONNECTED",Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (MqttException e){
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if(topic.equals("chuoi_json")){
                    String mydata = message.toString();
                    String pt_data = ptChuoi(mydata);
                    arr = pt_data.split(",");
                    chedo = Integer.valueOf(arr[0]);
                    speed = Integer.valueOf(arr[1]);
                    red = Integer.valueOf(arr[2]);
                    green = Integer.valueOf(arr[3]);
                    blue = Integer.valueOf(arr[4]);

                    if(chedo == 1) {
                        button_onoff.setBackgroundResource(R.drawable.nut_onoff);
                        button_onoff.setText("Bật");
                        relativeLayout.setBackgroundResource(R.drawable.rainbow2);
                        textView_speed.setText("Tốc Độ: "+speed+"m/s");
                        videoView.start();
                    }
                    else {
                        button_onoff.setBackgroundResource(R.drawable.nut_onoff2);
                        button_onoff.setText("Tắt");
                        relativeLayout.setBackgroundResource(R.drawable.rainbow);
                        textView_speed.setText("Tốc Độ: "+0+"m/s");
                        videoView.pause();
                    }
                        if (redl != red) {
                            startConveyorAnimation(imageView_red);
                            redl = red;
                        }
                        if (greenl != green) {
                            startConveyorAnimation(imageView_green);
                            greenl = green;
                        }
                        if (bluel != blue) {
                            startConveyorAnimation(imageView_blue);
                            bluel = blue;
                        }
                    hienThi(red,green,blue,chedo,speed);
                }
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        button_onoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message="";
                if (isColorChanged ) {
                    button_onoff.setBackgroundResource(R.drawable.nut_onoff);
                    isColorChanged = false;
                    button_onoff.setText("Bật");
                    chedo = 1;
                    relativeLayout.setBackgroundResource(R.drawable.rainbow2);
                    textView_speed.setText("Tốc Độ: "+speed+"m/s");
                    startVideo();
                    StringBuilder st = new StringBuilder("{\"mode\"");
                    st.append(":").append(chedo).append(",\"speed\":").append(speed).append(",\"red\":").append(red).append(",\"green\":").append(green).append(",\"blue\":").append(blue).append("}");
                    message= String.valueOf(st);
                } else {
                    button_onoff.setBackgroundResource(R.drawable.nut_onoff2);
                    isColorChanged = true;
                    button_onoff.setText("Tắt");
                    chedo = 0;
                    relativeLayout.setBackgroundResource(R.drawable.rainbow);
                    textView_speed.setText("Tốc độ: "+0+"m/s");
                    videoView.pause();
                    StringBuilder st = new StringBuilder("{\"mode\"");
                    st.append(":").append(chedo).append(",\"speed\":").append(speed).append(",\"red\":").append(red).append(",\"green\":").append(green).append(",\"blue\":").append(blue).append("}");
                    message = String.valueOf(st);
                }
                guiMQTT(message);
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                startVideo();
            }
        });


        buttongiam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message="";
                if(speed >0) {
                    speed--;
                    String speed_st = String.valueOf(speed);
                    textView_speed.setText("Tốc độ: " + speed_st + "m/s");
                    if (speed == 0) {
                        Toast.makeText(MainActivity.this, "Hệ thống tắt", Toast.LENGTH_LONG).show();
                        button_onoff.setBackgroundResource(R.drawable.nut_onoff2);
                        isColorChanged = true;
                        button_onoff.setText("Tắt");
                        relativeLayout.setBackgroundResource(R.drawable.rainbow);
                        videoView.pause();
                        chedo=0;
                    }
                    StringBuilder st = new StringBuilder("{\"mode\"");
                    st.append(":").append(chedo).append(",\"speed\":").append(speed).append(",\"red\":").append(red).append(",\"green\":").append(green).append(",\"blue\":").append(blue).append("}");
                    message = String.valueOf(st);
                    guiMQTT(message);
                }
                else {
                    Toast.makeText(MainActivity.this, "Hệ thống tắt", Toast.LENGTH_LONG).show();
                }
            }
        });
        buttontang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message="";
                if (speed == 0) {
                    Toast.makeText(MainActivity.this, "Hệ thống bật", Toast.LENGTH_LONG).show();
                    button_onoff.setBackgroundResource(R.drawable.nut_onoff);
                    isColorChanged = false;
                    button_onoff.setText("Bật");
                    relativeLayout.setBackgroundResource(R.drawable.rainbow2);
                    chedo=1;
                }
                speed++;
                String speed_st = String.valueOf(speed);
                textView_speed.setText("Tốc độ: " + speed_st + "m/s");
                StringBuilder st = new StringBuilder("{\"mode\"");
                st.append(":").append(chedo).append(",\"speed\":").append(speed).append(",\"red\":").append(red).append(",\"green\":").append(green).append(",\"blue\":").append(blue).append("}");
                message = String.valueOf(st);
                guiMQTT(message);
            }
        });
        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message="";
                speed = 3;
                red = redl = 0;
                blue = bluel = 0;
                green = greenl = 0;
                chedo = 1;
                textView_red.setText("0");
                textView_blue.setText("0");
                textView_green.setText("0");
                textView_speed.setText("Tốc độ: "+speed+"m/s");
                textView_sum.setText("0");
                StringBuilder st = new StringBuilder("{\"mode\"");
                st.append(":").append(chedo).append(",\"speed\":").append(speed).append(",\"red\":").append(red).append(",\"green\":").append(green).append(",\"blue\":").append(blue).append("}");
                message = st.toString();
                guiMQTT(message);
            }
        });
    }

    void anhXa() {
        videoView = findViewById(R.id.bangtruyen);
        buttontang = findViewById(R.id.tang);
        buttongiam = findViewById(R.id.giam);
        button_onoff = findViewById(R.id.onoff);
        button_reset = findViewById(R.id.reset);
        textView_day = findViewById(R.id.day);
        textView_time = findViewById(R.id.time);
        textView_speed = findViewById(R.id.speed);
        textView_sum = findViewById(R.id.sum);
        textView_red = findViewById(R.id.red);
        textView_blue = findViewById(R.id.blue);
        textView_green = findViewById(R.id.green);
        relativeLayout = findViewById(R.id.relativelayout);
        imageView_red =findViewById(R.id.tomatoo_red);
        imageView_blue = findViewById(R.id.tomato_blue);
        imageView_green = findViewById(R.id.tomato_green);
    }
    private Runnable updateTimeViewRunnable = new Runnable() {
        public void run(){
            updateTime();
            handler.postDelayed(this,1000);
        }
    };
    private void updateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        String currentDate = dateFormat.format(calendar.getTime());
        textView_time.setText(currentTime);
        textView_day.setText(currentDate);
    }
    protected void onDestroy(){
        super.onDestroy();
        handler.removeCallbacks(updateTimeViewRunnable);
    }
    private void startConveyorAnimation(ImageView imageView) {
        imageView.setVisibility(View.VISIBLE);
        int parentWidth = ((View)imageView.getParent()).getWidth(); // Lấy chiều rộng của phần tử cha
        int imageViewWidth = imageView.getWidth(); // Lấy chiều rộng của ImageView

        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, 1f,
                Animation.ABSOLUTE, 0f,
                Animation.ABSOLUTE, 0f);

        animation.setDuration(3000);
        animation.setFillAfter(true);
        imageView.startAnimation(animation);
    }
    private void startVideo(){
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bang_truyen33s));
        videoView.start();
    }
    private void SUB(MqttAndroidClient client, String topic){
        int qos = 1;
        try{
            IMqttToken subtoken = client.subscribe(topic,qos);
            subtoken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });
        }
        catch (MqttException e){
            e.printStackTrace();
        }
    }

    private void guiMQTT(String chuoi){
        String topic = "chuoi_json";
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = chuoi.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic,message);
        }
        catch (UnsupportedEncodingException | MqttException e){
            e.printStackTrace();
        }
    }
    private String ptChuoi(String chuoi){
        String chuoi1 = chuoi.replace("{","").replace("}","").replace(":","").replace("mode","").replace("speed","").replace("red","").replace("blue","").replace("green","").replace("\"","");
        return chuoi1;
    }
    private void hienThi(int red1,int green1,int blue1,int mode1,int speed1){
        textView_red.setText(String.valueOf(red1));
        textView_green.setText(String.valueOf(green1));
        textView_blue.setText(String.valueOf(blue1));
        textView_speed.setText(String.valueOf("Tốc độ: " + speed1 + "m/s"));
        textView_sum.setText(String.valueOf(red1+blue1+green1));
        if(mode1 == 1)
        {
            button_onoff.setText("Bật");
        }
        else {
            button_onoff.setText("Tắt");
        }
    }
}