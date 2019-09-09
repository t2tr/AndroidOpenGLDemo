package edu.wuwang.opengl;

import edu.wuwang.opengl.blend.BlendActivity;
import edu.wuwang.opengl.camera.Camera3Activity;
import edu.wuwang.opengl.light.LightActivity;
import edu.wuwang.opengl.vr.VrContextActivity;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.wuwang.opengl.camera.Camera2Activity;
import edu.wuwang.opengl.camera.CameraActivity;
import edu.wuwang.opengl.egl.EGLBackEnvActivity;
import edu.wuwang.opengl.etc.ZipActivity;
import edu.wuwang.opengl.fbo.FBOActivity;
import edu.wuwang.opengl.image.SGLViewActivity;
import edu.wuwang.opengl.obj.ObjLoadActivity;
import edu.wuwang.opengl.obj.ObjLoadActivity2;
import edu.wuwang.opengl.render.FGLViewActivity;
import edu.wuwang.opengl.vary.VaryActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView mList;
    private ArrayList<MenuBean> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mList= (RecyclerView)findViewById(R.id.mList);
        mList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        data=new ArrayList<>();
        add("Drawing a shape",FGLViewActivity.class);
        add("Image processing",SGLViewActivity.class);
        add("Graphic transformation",VaryActivity.class);
        add("Camera 1 animation",CameraActivity.class);
        add("Camera 2 animation",Camera2Activity.class);
        add("Camera 3 animation",Camera3Activity.class);
        add("Compressed texture animation",ZipActivity.class);
        add("Use FBO",FBOActivity.class);
        add("EGL Back End",EGLBackEnvActivity.class);
        add("3D obj model",ObjLoadActivity.class);
        add("obj+mtl model",ObjLoadActivity2.class);
        add("VR Effect",VrContextActivity.class);
        add("Color mixing",BlendActivity.class);
        add("Illumination",LightActivity.class);
        mList.setAdapter(new MenuAdapter());
    }

    private void add(String name,Class<?> clazz){
        MenuBean bean=new MenuBean();
        bean.name=name;
        bean.clazz=clazz;
        data.add(bean);
    }

    private class MenuBean{

        String name;
        Class<?> clazz;

    }

    private class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuHolder>{


        @Override
        public MenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MenuHolder(getLayoutInflater().inflate(R.layout.item_button,parent,false));
        }

        @Override
        public void onBindViewHolder(MenuHolder holder, int position) {
            holder.setPosition(position);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MenuHolder extends RecyclerView.ViewHolder{

            private Button mBtn;

            MenuHolder(View itemView) {
                super(itemView);
                mBtn= (Button)itemView.findViewById(R.id.mBtn);
                mBtn.setOnClickListener(MainActivity.this);
            }

            public void setPosition(int position){
                MenuBean bean=data.get(position);
                mBtn.setText(bean.name);
                mBtn.setTag(position);
            }
        }

    }

    @Override
    public void onClick(View view){
        int position= (int)view.getTag();
        MenuBean bean=data.get(position);
        startActivity(new Intent(this,bean.clazz));
    }
}
