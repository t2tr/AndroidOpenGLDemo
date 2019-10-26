package edu.wuwang.opengl.vr;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.wuwang.opengl.BaseActivity;
import edu.wuwang.opengl.R;

public class VRListActivity extends BaseActivity {
    RecyclerView mRecyclerView;
    VRListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vr_list);
        mRecyclerView = findViewById(R.id.recycler_view);
        mAdapter = new VRListAdapter();

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        ArrayList<Object> list = new ArrayList<>();
        list.add(5);
        list.add(5);
        list.add(5);
        list.add(5);
        list.add(5);
        list.add(5);
        list.add(5);
        list.add(5);
        list.add(5);
        list.add(5);
        list.add(5);
        list.add(5);
        list.add(5);
        mAdapter.setData(list);
    }
}
