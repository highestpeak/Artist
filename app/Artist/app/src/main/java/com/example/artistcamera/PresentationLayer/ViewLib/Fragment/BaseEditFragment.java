package com.example.artistcamera.PresentationLayer.ViewLib.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.artistcamera.R;

/**
 * Created by 恶灵退散 on 2019/10/5.
 */

public class BaseEditFragment extends Fragment {
    //创建视图
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_base_edit, container, false );  //要加载的layout文件
    }
}
