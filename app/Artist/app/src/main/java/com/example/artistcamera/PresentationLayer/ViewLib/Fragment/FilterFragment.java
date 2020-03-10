package com.example.artistcamera.PresentationLayer.ViewLib.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import android.app.Fragment;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.example.artistcamera.R;
import com.zomato.photofilters.SampleFilters;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.chengdazhi.styleimageview.Styler;

/**
 * Created by highestpeak on 2019/10/5.
 */

public class FilterFragment extends Fragment {

    ImageView filteredPhoto;
    @BindView(R.id.filter_button_group)
    RadioGroup filterButtonGroup;
    private FilterButton[] filterButtons = {
            new FilterButton("灰度", R.drawable.filter_greyscale_pressed, new GreyScaleFilter()),
            new FilterButton("反色", R.drawable.filter_invert_pressed, new InvertFilter()),
            new FilterButton("RGB2BGR", R.drawable.filter_rgb2bgr_pressed, new RGB2BGRFilter()),
            new FilterButton("黑白", R.drawable.filter_blackwhite_pressed, new BlackWhiteFilter()),
            new FilterButton("高亮", R.drawable.filter_bright_pressed, new BrightFilter()),
            new FilterButton("复古", R.drawable.filter_vintagepinhole_pressed, new VintagePinholeFilter()),
            new FilterButton("柯达", R.drawable.filter_kodachrome_pressed, new KodachromeFilter()),
            new FilterButton("彩色", R.drawable.filter_technicolor_pressed, new TechnicolorFilter()),
            new FilterButton("星际", R.drawable.filter_starlit_pressed, new StarLitFilter()),
            new FilterButton("蓝影", R.drawable.filter_bluemess_pressed, new BlueMessFilter()),
            new FilterButton("震撼", R.drawable.filter_awestruckvibe_pressed, new AweStruckVibeFilter()),
            new FilterButton("默语", R.drawable.filter_limestutter_pressed, new LimeStutterFilter()),
            new FilterButton("夜雨", R.drawable.filter_nightwhisper_pressed, new NightWhisperFilter()),
    };
    Unbinder unbinder;

    //创建视图
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        unbinder = ButterKnife.bind(this, view);
        filteredPhoto = getActivity().findViewById(R.id.processed_photo);
        // Use fields...
        generateRadioButtons(inflater.getContext());
        return view;
    }

    private void generateRadioButtons(Context context) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 10, 0);
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(30));
        myFilter.addSubFilter(new ContrastSubFilter(1.1f));
        //
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inMutable = true;
//        Bitmap inputImage = BitmapFactory.decodeResource(context.getResources(),R.drawable.origin,options);
//        ImageView imageView=new ImageView(context);
        //

        for (FilterButton filterButton : filterButtons) {
            RadioButton radioButton = new RadioButton(context);
            radioButton.setText(filterButton.text);
            Drawable top = getResources().getDrawable(filterButton.drawableId);
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    filterButton.filterProcess.process(filteredPhoto);
//                    imageView.setImageBitmap(inputImage);
//                    filterButton.filterProcess.process(imageView);
                }
            });

            radioButton.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
            radioButton.setButtonDrawable(null);
            radioButton.setChecked(false);
            radioButton.setGravity(Gravity.CENTER);
            radioButton.setTextColor(R.drawable.nav_word);
            radioButton.setLayoutParams(params);
            filterButtonGroup.addView(radioButton);
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    class FilterButton {
        String text;
        int drawableId;
        ArtistFilter filterProcess;

        FilterButton(String text, int drawableId, ArtistFilter filterProcess) {
            this.text = text;
            this.drawableId = drawableId;
            this.filterProcess = filterProcess;
        }
    }

    /**
     * filter class
     * Styler from StyleImageView repo
     * Filter from AndroidPhotoFilters repo
     */
    interface ArtistFilter{
        void process(ImageView processView);
        static Bitmap getBitmapOfView(ImageView imageView){
            Object ob =  imageView.getDrawable();
            Bitmap bm = null;
            if (ob instanceof GlideBitmapDrawable){
                bm = ((GlideBitmapDrawable) ob).getBitmap();
            }else{
                bm = ((BitmapDrawable) ob).getBitmap();
            }
            return bm.copy(bm.getConfig(),true);
        }
    }

    /**
     * <a href="https://github.com/chengdazhi/StyleImageView/wiki">StyleImageView</a>
     */
    class GreyScaleFilter implements ArtistFilter{

        @Override
        public void process(ImageView processView) {
            Bitmap outputImage = ArtistFilter.getBitmapOfView(processView);
            outputImage=Styler.addStyleToBitmap(getContext(), outputImage, Styler.Mode.GREY_SCALE);
            processView.setImageBitmap(outputImage);
        }
    }

    class InvertFilter implements ArtistFilter{

        @Override
        public void process(ImageView processView) {
            Bitmap outputImage = ArtistFilter.getBitmapOfView(processView);
            outputImage=Styler.addStyleToBitmap(getContext(), outputImage, Styler.Mode.INVERT);
            processView.setImageBitmap(outputImage);
        }
    }

    class RGB2BGRFilter implements ArtistFilter{

        @Override
        public void process(ImageView processView) {
            Bitmap outputImage = ArtistFilter.getBitmapOfView(processView);
            outputImage=Styler.addStyleToBitmap(getContext(), outputImage, Styler.Mode.RGB_TO_BGR);
            processView.setImageBitmap(outputImage);
        }
    }

    class BlackWhiteFilter implements ArtistFilter{

        @Override
        public void process(ImageView processView) {
            Bitmap outputImage = ArtistFilter.getBitmapOfView(processView);
            outputImage=Styler.addStyleToBitmap(getContext(), outputImage, Styler.Mode.BLACK_AND_WHITE);
            processView.setImageBitmap(outputImage);
        }
    }

    class BrightFilter implements ArtistFilter{

        @Override
        public void process(ImageView processView) {
            Bitmap outputImage = ArtistFilter.getBitmapOfView(processView);
            outputImage=Styler.addStyleToBitmap(getContext(), outputImage, Styler.Mode.BRIGHT);
            processView.setImageBitmap(outputImage);
        }
    }

    class VintagePinholeFilter implements ArtistFilter{

        @Override
        public void process(ImageView processView) {
            Bitmap outputImage = ArtistFilter.getBitmapOfView(processView);
            outputImage=Styler.addStyleToBitmap(getContext(), outputImage, Styler.Mode.VINTAGE_PINHOLE);
            processView.setImageBitmap(outputImage);
        }
    }

    class KodachromeFilter implements ArtistFilter{

        @Override
        public void process(ImageView processView) {
            Bitmap outputImage = ArtistFilter.getBitmapOfView(processView);
            outputImage=Styler.addStyleToBitmap(getContext(), outputImage, Styler.Mode.KODACHROME);
            processView.setImageBitmap(outputImage);
        }
    }

    class TechnicolorFilter implements ArtistFilter{

        @Override
        public void process(ImageView processView) {
            Bitmap outputImage = ArtistFilter.getBitmapOfView(processView);
            outputImage=Styler.addStyleToBitmap(getContext(), outputImage, Styler.Mode.TECHNICOLOR);
            processView.setImageBitmap(outputImage);
        }
    }

    /** todo:add filters
     * <a href="https://github.com/Zomato/AndroidPhotoFilters">AndroidPhotoFilters</a>
     */
    class StarLitFilter implements ArtistFilter{

        @Override
        public void process(ImageView processView) {
            Filter myFilter = SampleFilters.getStarLitFilter();
            Bitmap outputImage = myFilter.processFilter(ArtistFilter.getBitmapOfView(processView));
            processView.setImageBitmap(outputImage);
        }
    }

    class BlueMessFilter implements ArtistFilter{

        @Override
        public void process(ImageView processView) {
            Filter myFilter = SampleFilters.getBlueMessFilter();
            Bitmap outputImage = myFilter.processFilter(ArtistFilter.getBitmapOfView(processView));
            processView.setImageBitmap(outputImage);
        }
    }

    class AweStruckVibeFilter implements ArtistFilter{

        @Override
        public void process(ImageView processView) {
            Filter myFilter = SampleFilters.getAweStruckVibeFilter();
            Bitmap outputImage = myFilter.processFilter(ArtistFilter.getBitmapOfView(processView));
            processView.setImageBitmap(outputImage);

        }
    }

    class LimeStutterFilter implements ArtistFilter{

        @Override
        public void process(ImageView processView) {
            Filter myFilter = SampleFilters.getLimeStutterFilter();
            Bitmap outputImage = myFilter.processFilter(ArtistFilter.getBitmapOfView(processView));
            processView.setImageBitmap(outputImage);
        }
    }

    class NightWhisperFilter implements ArtistFilter{

        @Override
        public void process(ImageView processView) {
            Filter myFilter = SampleFilters.getNightWhisperFilter();
            Bitmap outputImage = myFilter.processFilter(ArtistFilter.getBitmapOfView(processView));
            processView.setImageBitmap(outputImage);
        }
    }

}
