package edu.wuwang.opengl.vr;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class VRView extends GLTextureView implements GLTextureView.Renderer, GLSurfaceView.Renderer, SensorEventListener {
    private Sphere mSkySphere;
    private float[] matrix = new float[16];

    private SensorManager mSensorManager;
    private Sensor mSensorRotation;

    public VRView(Context context) {
        super(context);
        init(null);
    }

    public VRView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

  /*  public VRView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }*/

  public void recalibrate() {
      if(mSkySphere!=null) mSkySphere.recalibrate();
  }

    private void init(AttributeSet attrs) {
        mSensorManager=(SensorManager)getContext().getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors=mSensorManager.getSensorList(Sensor.TYPE_ALL);
        //todo 判断是否存在rotation vector sensor
        mSensorRotation =mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        mSkySphere=new Sphere(getContext(),"vr/down1.jpg");

    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorRotation,SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mSkySphere.create();
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_FRONT);
    }

    private float w, h;

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mSkySphere.setSize(width, height);
        w = width;
        h = height;
        GLES20.glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(1,1,1,1);
        mSkySphere.draw();
    }

    @Override
    public void onSurfaceDestroyed(GL10 gl) {
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        //SensorManager.getRotationMatrixFromVector(matrix,event.values);
       // mSkySphere.setMatrix(matrix,event.values);
        mSkySphere.setVector(event.values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
