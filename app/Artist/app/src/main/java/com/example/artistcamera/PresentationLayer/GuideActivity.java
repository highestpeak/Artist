package com.example.artistcamera.PresentationLayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.artistcamera.PresentationLayer.ViewLib.Adapter.GuidePageAdapter;
import com.example.artistcamera.R;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    private ViewPager vp;
    private int []imageIdArray;//图片资源的数组
    private List<View> viewList;//图片资源的集合
    private ViewGroup vg;//放置圆点

    //实例化原点View
    private ImageView iv_point;
    private ImageView []ivPointArray;

    //最后一页的按钮
    private ImageButton ib_start;
    private LinearLayout.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);

        initGuidePager();
    }

    private void initGuidePager() {
        ib_start = (ImageButton) findViewById(R.id.guide_ib_start);
        ib_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GuideActivity.this,MainActivity.class));
                finish();
            }
        });
        //加载ViewPager
        initViewPager();
        //加载底部圆点
        initPoint();
    }

    /**
     * 加载底部圆点
     */
    private void initPoint() {
        //这里实例化LinearLayout
        vg = (ViewGroup) findViewById(R.id.guide_ll_point);
        //根据ViewPager的item数量实例化数组
        ivPointArray = new ImageView[viewList.size()];
        //循环新建底部圆点ImageView，将生成的ImageView保存到数组中
        int size = viewList.size();
        for (int i = 0;i<size;i++){
            iv_point = new ImageView(this);
            layoutParams = new LinearLayout.LayoutParams(15,15);

            //TODO 第一个页面需要设置为选中状态，这里采用两张不同的图片 ????
            if (i == 0){
                iv_point.setBackgroundResource(R.drawable.shape_bg_point_enable);
            }else{
                layoutParams.leftMargin=20;
                iv_point.setBackgroundResource(R.drawable.shape_bg_point_disable);
            }
            iv_point.setLayoutParams(layoutParams);
            iv_point.setPadding(30,0,30,0);//left,top,right,bottom
            ivPointArray[i] = iv_point;

            //将数组中的ImageView加入到ViewGroup
            vg.addView(ivPointArray[i]);
        }
    }

    /**
     * 加载图片ViewPager
     */
    private void initViewPager() {
        vp = (ViewPager) findViewById(R.id.guide_vp);
        //动态加载 实例化图片资源 而不是写死的？？
        imageIdArray = new int[]{R.drawable.guide1,R.drawable.guide2,R.drawable.guide3,R.drawable.guide4};
        viewList = new ArrayList<>();
        //获取一个Layout参数，设置为全屏
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);

        //循环创建View并加入到集合中
        int len = imageIdArray.length;
        for (int i = 0;i<len;i++){
            //new ImageView并设置全屏和图片资源
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(params);
            imageView.setBackgroundResource(imageIdArray[i]);

            //将ImageView加入到集合中
            viewList.add(imageView);
        }

        //View集合初始化好后，设置Adapter
        vp.setAdapter(new GuidePageAdapter(viewList));
        //设置滑动监听
        vp.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * 滑动后的监听
     * @param position
     */
    @Override
    public void onPageSelected(int position) {
        //循环设置当前页的标记图
        int length = imageIdArray.length;
        for (int i = 0;i<length;i++){
            ivPointArray[position].setBackgroundResource(R.drawable.shape_bg_point_enable);
            if (position != i){
                ivPointArray[i].setBackgroundResource(R.drawable.shape_bg_point_disable);
            }
        }

        //判断是否是最后一页，若是则显示按钮
        if (position == imageIdArray.length - 1){
            ib_start.setVisibility(View.VISIBLE);
        }else {
            ib_start.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
