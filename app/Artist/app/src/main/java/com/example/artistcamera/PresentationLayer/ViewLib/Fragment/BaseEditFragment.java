package com.example.artistcamera.PresentationLayer.ViewLib.Fragment;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.SeekBar;

import android.app.Fragment;

import androidx.annotation.DrawableRes;

import com.bumptech.glide.Glide;
import com.example.artistcamera.MyApplication;
import com.example.artistcamera.PresentationLayer.CropActivity;
import com.example.artistcamera.PresentationLayer.PicEditActivity;
import com.example.artistcamera.R;
import com.example.artistcamera.Util.GlideImageLoader;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ImageLoader;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.galleryfinal.widget.GFImageView;
import it.chengdazhi.styleimageview.Styler;

import static com.example.artistcamera.Util.UriPhotoHelp.getRealPathFromUri;
import static com.example.artistcamera.Util.UriPhotoHelp.uriToBitmap;

public class BaseEditFragment extends Fragment {

    private final int CODE_CROP_RESULT = 2;
    private Styler styler;

    @BindView(R.id.crop)
    RadioButton crop;
    @BindView(R.id.style_edit)
    RadioButton filter2;
    PopupWindow popupWindow;
    SeekBar brightnessBar;
    SeekBar contrastBar;
    SeekBar saturationBar;

    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_base_edit, container, false);
        unbinder = ButterKnife.bind(this, view);

        ImageView editedPhoto = getActivity().findViewById(R.id.processed_photo);
        styler = new Styler.Builder(editedPhoto, Styler.Mode.NONE).enableAnimation(500).build();

        initPopupWindow();

        return view;
    }

    private void initPopupWindow() {

        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.popup_editphoto, null, false);

        popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true); //设置可以点击
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true); // 获取焦点

        brightnessBar =contentView.findViewById(R.id.seekbar_brightness);
        contrastBar = contentView.findViewById(R.id.seekbar_contrast);
        saturationBar = contentView.findViewById(R.id.seekbar_saturation);
        saturationBar.setProgress((int) (styler.getSaturation() * 100));

        initSeekBar();
    }

    private void initSeekBar() {

        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    styler.setBrightness(i - 255).updateStyle();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    styler.setBrightness(i - 255).updateStyle();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        saturationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    //todo: 色彩对比度 https://github.com/chengdazhi/StyleImageView/wiki/%E4%B8%AD%E6%96%87%E8%AF%B4%E6%98%8E%E9%A1%B5
                    styler.setSaturation(i / 100F).updateStyle();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.crop, R.id.style_edit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.crop:
                cropImage();
                break;
            case R.id.style_edit:
                showPopWindow();
                break;
        }
    }

    private void showPopWindow() {
        View rootview = LayoutInflater.from(getActivity()).inflate(R.layout.activity_main,
                null);
        popupWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
    }

    private void cropImage(){
        ImageView imageView = ((PicEditActivity)getActivity()).getProcessedPhoto();

        GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
            @Override
            public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                imageView.setImageBitmap(BitmapFactory.decodeFile(resultList.get(0).getPhotoPath()));
                System.out.println("CROP Success");
            }

            @Override
            public void onHanlderFailure(int requestCode, String errorMsg) {
                System.out.println("CROP Failure");
            }
        };

        //todo: https://github.com/pengjianbo/GalleryFinal
        Uri uri = ((PicEditActivity)getActivity()).getUriProcessed();
        GalleryFinal.openCrop(CODE_CROP_RESULT, getRealPathFromUri(getContext(),uri), mOnHanlderResultCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_CROP_RESULT){
            System.out.println("CODE_CROP_RESULT");
        }
    }

}
