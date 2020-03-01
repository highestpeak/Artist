package com.example.artistcamera.PresentationLayer.ViewLib.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.example.artistcamera.PresentationLayer.PicEditActivity;
import com.example.artistcamera.PresentationLayer.StyleMigrationActivity;
import com.example.artistcamera.R;
import com.example.artistcamera.Util.DialogShowHelp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class FragmentTransfer extends Fragment {
    @BindView(R.id.btn_trans)
    Button btnTrans;
    Unbinder unbinder;

    //创建视图
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transfer, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_trans)
    public void onViewClicked() {
        Intent intent = new Intent(getActivity(), StyleMigrationActivity.class);
        intent.putExtra("from", "onePhoto");
        intent.putExtra("photoUri", ((PicEditActivity)getActivity()).getUriProcessed().toString());
        startActivity(intent);
    }
}