package com.hrc.administrator.compasstest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private ImageView compassImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        compassImg= (ImageView) findViewById(R.id.compassImg);
        //获取传感器管理器
        sensorManager= (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //获得加速度传感器和地磁传感器
        Sensor magneticSensor=sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor accelerometerSensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //为传感器设立监听
        sensorManager.registerListener(listener,magneticSensor,SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(listener,accelerometerSensor,SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //当sensorManager不为空的时候，取消监听
        if(sensorManager!=null){
            sensorManager.unregisterListener(listener);
        }
    }

    private SensorEventListener listener = new SensorEventListener() {
        float[] accelerometerValues=new float[3];
        float[] magneticValues=new float[3];
        private float lastRotateDegree;

        @Override
        public void onSensorChanged(SensorEvent event) {
            //values时一个数组返回xyz三个方向的值,若不调用clone()方法，两个数组将指向同一个引用
            //判断传感器类型
            if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
                //加速度传感器
                accelerometerValues=event.values.clone();
            }else if (event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
                //地磁传感器
                magneticValues=event.values.clone();
            }
            float[] R=new float[9];
            float[] values=new float[3];
            //R将储存该方法计算出来的旋转数据
            SensorManager.getRotationMatrix(R,null,accelerometerValues,magneticValues);
            //values将储存计算后的旋转数值，以弧度为单位
            SensorManager.getOrientation(R,values);
            //将计算出的角度取反，用于旋转指南针背景图
            float rotateDegree=-(float)Math.toDegrees(values[0]);
            if(Math.abs(rotateDegree-lastRotateDegree)>1){
                //旋转动画，第一个参数为旋转的起始角度，第二个参数为旋转的终止角度
                RotateAnimation animation=new RotateAnimation(lastRotateDegree,rotateDegree, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                animation.setFillAfter(true);
                compassImg.startAnimation(animation);
                lastRotateDegree=rotateDegree;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
