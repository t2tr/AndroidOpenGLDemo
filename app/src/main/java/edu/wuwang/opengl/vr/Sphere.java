package edu.wuwang.opengl.vr;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import edu.wuwang.opengl.utils.Gl2Utils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by aiya on 2017/5/19.
 */

public class Sphere {
    private static final String TAG = "Sphere";

    private static final float UNIT_SIZE = 1f;// 单位尺寸
    private float r = 2f; // 球的半径

    private float radius=2f;

    final double angleSpan = Math.PI/90f;// 将球进行单位切分的角度
    int vCount = 0;// 顶点个数，先初始化为0

    private static final float FROM_RADS_TO_DEGS = -57.2957795f;
    private Resources res;

    private int mHProgram;
    private int mHUTexture;
    private int mHProjMatrix;
    private int mHViewMatrix;
    private int mHModelMatrix;
    private int mHRotateMatrix;
    private int mHPosition;
    private int mHCoordinate;

    private int textureId;

    private float[] mViewMatrix=new float[16];
    private float[] mProjectMatrix=new float[16];
    private float[] mModelMatrix=new float[16];


    private boolean mIsInitSensor = false;
    private float[] mRotateMatrix = new float[16];

    private float[] mInitRotateVector;
    private float[] mSensorVector;

    private final float[] mCenterMatrix = new float[] {
            0,0,-1,0,
            -1,0,0,0,
            0,1,0,0,
            0,0,0,1
    };

    /*  1,0,0,0,
           0,0,-1,0,
           0,1,0,0,
           0,0,0,1*/
    private float[] mRotateVector;
    /*
        [0]          [1]        [2]    [3]     [4]           [5]        [6]     [7]     [8]        [9]         [10]
    -0.9956346 -0.008177486 0.09297807 0.0 0.0099688275 -0.99977326 0.018818181 0.0 0.0928031  0.019662913 0.99549025  0.0 0.0 0.0 0.0 1.0
    -0.6924454  0.009915203 0.7214021  0.0 0.71979225   -0.05866182 0.6917064   0.0 0.04917717 0.99822867  0.033483267 0.0 0.0 0.0 0.0 1.0
     */

    DecimalFormat df = new DecimalFormat("0.00");
    private float[] startRotateSensorOrient = new float[3];
    private float[] currentRotateSensorOrient = new float[3];
    private float[] currentOrient = new float[3];
    public void setVector(float[] values) {
        float[] vectors;
        if (values.length > 4) {
            vectors = new float[4];
            System.arraycopy(values, 0, vectors, 0, 4);
        } else
            vectors = values;

        calculateV4(vectors);
    }

    private void calculateBasedOnGravity(float[] vectors) {
        SensorManager.getRotationMatrixFromVector(mRotateMatrix,vectors);
    }

    private float[] initSensorM = new float[16];
    private float[] currentSensorM = new float[16];
    private float[] angleChange = new float[3];
    private void calculateV4(float[] vectors) {
        if(!mIsInitSensor) {
            mIsInitSensor = true;
            SensorManager.getRotationMatrixFromVector(initSensorM,vectors);
        }

        SensorManager.getRotationMatrixFromVector(currentSensorM,vectors);

        SensorManager.getAngleChange(angleChange,currentSensorM,initSensorM);
        float[] temp = new float[16];
        Matrix.rotateM(temp,0,mCenterMatrix,0,angleChange[1]*FROM_RADS_TO_DEGS,0,1,0);

        Matrix.rotateM(mRotateMatrix,0,temp,0,-angleChange[2]*FROM_RADS_TO_DEGS,0,0,-1);
    }

    private void calculateV3(float[] vectors) {

        if(!mIsInitSensor) {
            mIsInitSensor = true;
            retrieveOrientationType1(vectors, startRotateSensorOrient);
        }

        retrieveOrientationType1(vectors, currentRotateSensorOrient);

        for (int i = 0; i < 3; i++) {
            currentOrient[i] = currentRotateSensorOrient[i] - startRotateSensorOrient[i];
        }

        log3("rotate sensor", currentRotateSensorOrient);
        log3("rotate sphere", currentOrient);
        Log.d(TAG, "------------");

        float[] temp = new float[16];
        Matrix.rotateM(temp,0,mCenterMatrix,0, currentOrient[0],0,1,0);
      //  float[] temp1 = new float[16];
        //   Matrix.rotateM(temp1,0,temp,0,currentOrient[1],1,0,0);
        Matrix.rotateM(mRotateMatrix,0,temp,0, currentOrient[2],0,0,-1);
    }

    private void retrieveOrientationType2(float[] vectors, float[] outYPR) {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, vectors);
        int worldAxisX = SensorManager.AXIS_X;
        int worldAxisZ = SensorManager.AXIS_Z;
        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);
        float[] orientation = new float[3];
        SensorManager.getOrientation(rotationMatrix, orientation);
        float yaw = orientation[0] * FROM_RADS_TO_DEGS;
        float pitch = orientation[1] * FROM_RADS_TO_DEGS;
        float roll = orientation[2] * FROM_RADS_TO_DEGS;
        outYPR[0] = pitch; // x
        outYPR[1] = roll; // y
        outYPR[2] = yaw; // z
    }

    private void retrieveOrientationType1(float[] vectors, float[] outYPR) {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, vectors);
        int worldAxisX = SensorManager.AXIS_X;
        int worldAxisZ = SensorManager.AXIS_Z;
        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);
        float[] orientation = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientation);
        float yaw = orientation[0] * FROM_RADS_TO_DEGS;
        float pitch = orientation[1] * FROM_RADS_TO_DEGS;
        float roll = orientation[2] * FROM_RADS_TO_DEGS;
        outYPR[0] = pitch; // x
        outYPR[1] = roll; // y
        outYPR[2] = yaw; // z
    }

    private void log3(String name, float[] xyz) {
        Log.d(TAG, "report "+name+": x = "+df.format(xyz[0])+", y = "+ df.format(xyz[1])+", z = " + df.format(xyz[2]));

    }

    private int mMode = 0;
    public synchronized void recalibrate() {
        mIsInitSensor = false;
        mMode = (mMode==0) ? 1 : 0;
    }

    private FloatBuffer posBuffer;
    private FloatBuffer cooBuffer;
    private int vSize;
    private float skyRate=0.2f;

    private Bitmap mBitmap;

    public Sphere(Context context, String texture){
        this.res=context.getResources();
        try {
            mBitmap = BitmapFactory.decodeStream(context.getAssets().open(texture));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void create(){
        mHProgram=Gl2Utils.createGlProgramByRes(res,"vr/skysphere.vert","vr/skysphere.frag");
        mHProjMatrix=GLES20.glGetUniformLocation(mHProgram,"uProjMatrix");
        mHViewMatrix=GLES20.glGetUniformLocation(mHProgram,"uViewMatrix");
        mHModelMatrix=GLES20.glGetUniformLocation(mHProgram,"uModelMatrix");
        mHRotateMatrix=GLES20.glGetUniformLocation(mHProgram,"uRotateMatrix");
        mHUTexture=GLES20.glGetUniformLocation(mHProgram,"uTexture");
        mHPosition=GLES20.glGetAttribLocation(mHProgram,"aPosition");
        mHCoordinate=GLES20.glGetAttribLocation(mHProgram,"aCoordinate");
        textureId=createTexture();
        calculateAttribute();
    }

    public void setSize(int width,int height){
        //计算宽高比
        float ratio=(float)width/height;
        //设置透视投影
        //Matrix.frustumM(mProjectMatrix, 0, -ratio*skyRate, ratio*skyRate, -1*skyRate, 1*skyRate, 1, 200);
        //透视投影矩阵/视锥

        Matrix.perspectiveM(mProjectMatrix,0,90f,ratio,1f,300);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f,0.0f, 0.0f, 0.0f,1f, 0f,1.0f, 0.0f);
        //模型矩阵
        Matrix.setIdentityM(mModelMatrix,0);
        Log.d(TAG, "setSize!!");

    }

    public void draw(){

        GLES20.glUseProgram(mHProgram);
        GLES20.glUniformMatrix4fv(mHProjMatrix,1,false,mProjectMatrix,0);
        GLES20.glUniformMatrix4fv(mHViewMatrix,1,false,mViewMatrix,0);
        GLES20.glUniformMatrix4fv(mHModelMatrix,1,false,mModelMatrix,0);
        GLES20.glUniformMatrix4fv(mHRotateMatrix,1,false, mRotateMatrix,0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId);

        GLES20.glEnableVertexAttribArray(mHPosition);
        GLES20.glVertexAttribPointer(mHPosition,3,GLES20.GL_FLOAT,false,0,posBuffer);
        GLES20.glEnableVertexAttribArray(mHCoordinate);
        GLES20.glVertexAttribPointer(mHCoordinate,2,GLES20.GL_FLOAT,false,0,cooBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);

        GLES20.glDisableVertexAttribArray(mHPosition);
    }


    private void calculateAttribute(){
        ArrayList<Float> alVertix = new ArrayList<>();
        ArrayList<Float> textureVertix = new ArrayList<>();
        for (double vAngle = 0; vAngle < Math.PI; vAngle = vAngle + angleSpan){

            for (double hAngle = 0; hAngle < 2*Math.PI; hAngle = hAngle + angleSpan){
                float x0 = (float) (radius* Math.sin(vAngle) * Math.cos(hAngle));
                float y0 = (float) (radius* Math.sin(vAngle) * Math.sin(hAngle));
                float z0 = (float) (radius * Math.cos((vAngle)));

                float x1 = (float) (radius* Math.sin(vAngle) * Math.cos(hAngle + angleSpan));
                float y1 = (float) (radius* Math.sin(vAngle) * Math.sin(hAngle + angleSpan));
                float z1 = (float) (radius * Math.cos(vAngle));

                float x2 = (float) (radius* Math.sin(vAngle + angleSpan) * Math.cos(hAngle + angleSpan));
                float y2 = (float) (radius* Math.sin(vAngle + angleSpan) * Math.sin(hAngle + angleSpan));
                float z2 = (float) (radius * Math.cos(vAngle + angleSpan));

                float x3 = (float) (radius* Math.sin(vAngle + angleSpan) * Math.cos(hAngle));
                float y3 = (float) (radius* Math.sin(vAngle + angleSpan) * Math.sin(hAngle));
                float z3 = (float) (radius * Math.cos(vAngle + angleSpan));

                alVertix.add(x1);
                alVertix.add(y1);
                alVertix.add(z1);
                alVertix.add(x0);
                alVertix.add(y0);
                alVertix.add(z0);
                alVertix.add(x3);
                alVertix.add(y3);
                alVertix.add(z3);

                float s0 = (float) (hAngle / Math.PI/2);
                float s1 = (float) ((hAngle + angleSpan)/Math.PI/2);
                float t0 = (float) (vAngle / Math.PI);
                float t1 = (float) ((vAngle + angleSpan) / Math.PI);

                textureVertix.add(s1);// x1 y1对应纹理坐标
                textureVertix.add(t0);
                textureVertix.add(s0);// x0 y0对应纹理坐标
                textureVertix.add(t0);
                textureVertix.add(s0);// x3 y3对应纹理坐标
                textureVertix.add(t1);

                alVertix.add(x1);
                alVertix.add(y1);
                alVertix.add(z1);
                alVertix.add(x3);
                alVertix.add(y3);
                alVertix.add(z3);
                alVertix.add(x2);
                alVertix.add(y2);
                alVertix.add(z2);

                textureVertix.add(s1);// x1 y1对应纹理坐标
                textureVertix.add(t0);
                textureVertix.add(s0);// x3 y3对应纹理坐标
                textureVertix.add(t1);
                textureVertix.add(s1);// x2 y3对应纹理坐标
                textureVertix.add(t1);
            }
        }
        vCount = alVertix.size() / 3;
        posBuffer = convertToFloatBuffer(alVertix);
        cooBuffer=convertToFloatBuffer(textureVertix);
    }

    private FloatBuffer convertToFloatBuffer(ArrayList<Float> data){
        float[] d=new float[data.size()];
        for (int i=0;i<d.length;i++){
            d[i]=data.get(i);
        }

        ByteBuffer buffer=ByteBuffer.allocateDirect(data.size()*4);
        buffer.order(ByteOrder.nativeOrder());
        FloatBuffer ret=buffer.asFloatBuffer();
        ret.put(d);
        ret.position(0);
        return ret;
    }

    private int createTexture(){
        int[] texture=new int[1];
        if(mBitmap!=null&&!mBitmap.isRecycled()){
            //生成纹理
            GLES20.glGenTextures(1,texture,0);
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
            return texture[0];
        }
        return 0;
    }

}
