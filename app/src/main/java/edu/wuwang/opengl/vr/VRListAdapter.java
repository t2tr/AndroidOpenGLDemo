package edu.wuwang.opengl.vr;

import android.opengl.GLSurfaceView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.wuwang.opengl.R;

public class VRListAdapter extends RecyclerView.Adapter<VRListAdapter.VRListHolder> {
    public VRListAdapter() {

    }
    private static final String TAG = "VRListAdapter";
    private ArrayList<Object> mData = new ArrayList<>();
    public void setData(List<Object> data) {
        mData.clear();
        if(data!=null) mData.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VRListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new VRListHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_vr,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull VRListHolder vrListHolder, int i) {
        vrListHolder.bind();
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class VRListHolder extends RecyclerView.ViewHolder {
        View mView;
        public VRListHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView.findViewById(R.id.vr_view);
            if(mView instanceof GLSurfaceView)
            ((GLSurfaceView) mView).onResume();
            else if(mView instanceof GLTextureView)
                ((GLTextureView) mView).onResume();
        }

        public void bind() {
        }

    }
}
