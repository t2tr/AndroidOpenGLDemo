package edu.wuwang.opengl.vr;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import edu.wuwang.opengl.BaseActivity;
import edu.wuwang.opengl.R;
import java.util.List;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by aiya on 2017/5/19.
 */

public class VrContextActivity extends BaseActivity implements GLTextureView.Renderer, GLSurfaceView.Renderer, SensorEventListener, View.OnClickListener {

    private View mGLView;
    private SensorManager mSensorManager;
    private Sensor mSensorRotation;
    private SkySphere mSkySphere;

    private float[] matrix = new float[16];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.glview);

        mSensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors=mSensorManager.getSensorList(Sensor.TYPE_ALL);
        //todo 判断是否存在rotation vector sensor
        mSensorRotation =mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        mGLView = findViewById(R.id.mGLView);
        if(mGLView instanceof GLTextureView) {
            ((GLTextureView) mGLView).setEGLContextClientVersion(2);
            ((GLTextureView) mGLView).setRenderer(this);
            ((GLTextureView) mGLView).setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        } else if(mGLView instanceof GLSurfaceView) {
            ((GLSurfaceView) mGLView).setEGLContextClientVersion(2);
            ((GLSurfaceView) mGLView).setRenderer(this);
            ((GLSurfaceView) mGLView).setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        }

        mSkySphere=new SkySphere(this.getApplicationContext(),"vr/down2.jpg");
        mGLView.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorRotation,SensorManager.SENSOR_DELAY_GAME);
        if(mGLView instanceof GLTextureView)
        ((GLTextureView) mGLView).onResume();
        else if(mGLView instanceof GLSurfaceView)
            ((GLSurfaceView) mGLView).onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        if(mGLView instanceof GLTextureView)
            ((GLTextureView) mGLView).onPause();
        else if(mGLView instanceof GLSurfaceView)
            ((GLSurfaceView) mGLView).onPause();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mSkySphere.create();
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_FRONT);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mSkySphere.setSize(width, height);
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
       // SensorManager.getRotationMatrixFromVector(matrix,event.values);
       // mSkySphere.setMatrix(matrix,event.values);
        mSkySphere.setVector(event.values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onClick(View v) {
        if(mSkySphere!=null) mSkySphere.recalibrate();
    }
}
