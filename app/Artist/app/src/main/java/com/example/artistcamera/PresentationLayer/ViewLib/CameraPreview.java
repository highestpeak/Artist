package com.example.artistcamera.PresentationLayer.ViewLib;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.ExifInterface;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.artistcamera.DataLayer.Bean.PhotoInfo;
import com.example.artistcamera.PresentationLayer.Presenter.ProcessWithThreadPool;
import com.example.artistcamera.Util.WebHelp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/*
获取帧数据的接口:Camera.PreviewCallback
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback,Camera.PreviewCallback{
    private static final String TAG = "CameraPreview";
    private static final String ALBUM_NAME = "Artist";

    //default is back
    private Camera mCamera;
    private int CAMERA_NOW=Camera.CameraInfo.CAMERA_FACING_BACK;

    private SurfaceHolder mHolder;
    private Paint mPaint;
    private Path mPath;
    private Canvas mCanvas;
    private float mX, mY, newX, newY;
    /*
    Camera交给CameraHandlerThread来处理，
    在CameraHandlerThread线程中执行mCamera = Camera.open()
     */
    private CameraHandlerThread mThread;

    public CameraPreview(Context context) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mThread = new CameraHandlerThread("camera thread");
    }

    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = getCameraInstance();
        /*
        处理预览帧
         */
        mCamera.setPreviewCallback(this);
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        mHolder.removeCallback(this);
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    /*
    surfaceChanged
    预览画面跟随设备旋转
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        int rotation = getDisplayOrientation();
        mCamera.setDisplayOrientation(rotation);

        /*
        使得拍照得到的照片就和预览的方向一致
         */
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setRotation(rotation);
        mCamera.setParameters(parameters);

        /*
        预览分辨率的变化的触发
         */
        adjustDisplayRatio(rotation);

        center.x=(int) (this.getX()+this.getWidth()/2);
        center.y=(int) (this.getY()+this.getHeight()/2);
    }

    public Camera getCameraInstance() {
        if (mCamera == null) {
            switchCameraHelp();
        }
        return mCamera;
    }

    public void switchCamera(){
        switch (CAMERA_NOW){
            case Camera.CameraInfo.CAMERA_FACING_FRONT:
                CAMERA_NOW=Camera.CameraInfo.CAMERA_FACING_BACK;
                break;
            case Camera.CameraInfo.CAMERA_FACING_BACK:
                CAMERA_NOW=Camera.CameraInfo.CAMERA_FACING_FRONT;
                break;
            default:
                Log.d(TAG, "camera switch error");
        }
        switchCameraHelp();
    }
    private void switchCameraHelp(){
        synchronized (mThread) {
            mThread.openCamera();
        }
    }

    /*
    拍照
     */
    public void takePicture() {
        takePicture(null);
    }

    private String currScore=null;

    public void setCurrScore(String currScore) {
        this.currScore = currScore;
    }

    public void takePicture(final ImageView view) {
        //takePicture()是一个异步过程
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                saveImageToGallery(getContext(),data);
                if(view!=null){
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            view.setImageURI(outputMediaFileUri);
                            //图片保存到数据库
                            Thread thread=new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    PhotoInfo photoInfo=new PhotoInfo();
                                    String uriToIn=outputMediaFileUri.toString();
                                    photoInfo.setUri(uriToIn);
                                    photoInfo.setScore(currScore);
                                    photoInfo.save();
                                }
                            });
                            thread.run();
                        }
                    });

                }
                camera.startPreview();
            }
        });
    }

    /*
    触摸事件
     */
    private Integer scaleEvent=null;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG,"touched");
        if (scaleEvent!=null){
            event.setLocation(event.getX(),event.getY()-scaleEvent);
        }
        if (event.getPointerCount() == 1) {
//            handleFocus(event, mCamera);
//            drawRect(event.getX(),event.getY());
            if(focusRect!=null){
                focusRect.setTouchFoucusRect(mCamera, autoFocusCallback,event.getX(),event.getY());
            }
        } else {
            /*
            二指手势缩放
            */
            switch (event.getAction() & MotionEvent.ACTION_MASK)//获取手势类别
            {
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = getFingerSpacing(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float newDist = getFingerSpacing(event);
                    if (newDist > oldDist) {
                        handleZoom(true, mCamera);
                    } else if (newDist < oldDist) {
                        handleZoom(false, mCamera);
                    }
                    oldDist = newDist;
                    break;
            }
        }
        return true;
    }

    private FocusRect focusRect;
    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            focusRect.setFoucuing(false);
            focusRect.disDrawTouchFocusRect();
        }
    };
    public void setFocusRect(FocusRect focusRect) {
        this.focusRect = focusRect;
    }

    private void drawRect(float x, float y) {
//        if(focusRect!=null){
//            focusRect.setTouchFoucusRect(x,y);
//        }
    }

    /*
    处理预览帧数据data;
    当新的帧数据到达时，
    如果onPreviewFrame()正在执行还没有返回，这个帧数据就会被丢弃;
    被丢弃的意思是，这一帧没有进入onPreviewFrame()处理
    必须加快帧的处理速度,确保每一帧不丢失
     */
    private ProcessWithThreadPool processFrameThreadPool;

    public void setProcessFrameThreadPool(ProcessWithThreadPool processFrameThreadPool) {
        this.processFrameThreadPool = processFrameThreadPool;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera)  {
//        Log.i(TAG, "processing frame");
        Camera.Size size = mCamera.getParameters().getPreviewSize();
        try{
            YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
            if(image!=null){
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);

                Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                processFrameThreadPool.post(bmp);
                stream.close();
            }
        } catch (Exception e) {
            Log.d(TAG, "processing frame error "+e.getMessage());
        }

    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    private Point center;



    /*
    相机设置
     */
    private CameraSetting cameraSetting=new CameraSetting();
    public CameraSetting getCameraSetting(){
        return cameraSetting;
    }



    /*
    ============================================================
    =
    ============================================================
     */

    /*
    计算旋转角度
     */
    private int getDisplayOrientation() {
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        android.hardware.Camera.CameraInfo camInfo =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, camInfo);

        int result = (camInfo.orientation - degrees + 360) % 360;
        return result;
    }
    private void adjustDisplayRatio(int rotation) {
        /*
        记录父级frameLayout的记录父级的长和宽
         */
        ViewGroup parent = ((ViewGroup) getParent());
        Rect rect = new Rect();
        parent.getLocalVisibleRect(rect);
        int width = rect.width();
        int height = rect.height();
        /*
        预览分辨率
         */
        Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
        /*
        通过比较两者的纵横比，确定SurfaceView的调整方法，计算出需要调整的长度
         */
        int previewWidth;
        int previewHeight;
        if (rotation == 90 || rotation == 270) {
            previewWidth = previewSize.height;
            previewHeight = previewSize.width;
        } else {
            previewWidth = previewSize.width;
            previewHeight = previewSize.height;
        }

        /*
        通过layout()将新的SurfaceView的位置应用到布局中，完成纵横比的调整。
        TODO 可以通过layout设置不可见区域大小
         */


        if (width * previewHeight <= height * previewWidth) {//竖屏
            final int scaledChildWidth = previewWidth * height / previewHeight;
            layout((width - scaledChildWidth) / 2, 0,
                    (width + scaledChildWidth) / 2, height);
            scaleEvent=(scaledChildWidth-width)/2;
        } else {//横屏
            final int scaledChildHeight = previewHeight * width / previewWidth;
            layout(0, (height - scaledChildHeight) / 2,
                    width, (height + scaledChildHeight) / 2);
            scaleEvent=(scaledChildHeight-height)/2;
        }

    }

    /*
    对焦
    https://www.polarxiong.com/archives/Android%E7%9B%B8%E6%9C%BA%E5%BC%80%E5%8F%91-%E4%BA%94-%E8%A7%A6%E6%91%B8%E5%AF%B9%E7%84%A6-%E8%A7%A6%E6%91%B8%E6%B5%8B%E5%85%89-%E4%BA%8C%E6%8C%87%E6%89%8B%E5%8A%BF%E7%BC%A9%E6%94%BE.html
     */

    private static Rect calculateTapArea(float x, float y, float coefficient, int width, int height) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
        int centerX = (int) (x / width * 2000 - 1000);
        int centerY = (int) (y / height * 2000 - 1000);

        int halfAreaSize = areaSize / 2;
        RectF rectF = new RectF(clamp(centerX - halfAreaSize, -1000, 1000)
                , clamp(centerY - halfAreaSize, -1000, 1000)
                , clamp(centerX + halfAreaSize, -1000, 1000)
                , clamp(centerY + halfAreaSize, -1000, 1000));
        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private static int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    private void handleFocus(MotionEvent event, Camera camera) {
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        Rect focusRect = calculateTapArea(event.getX(), event.getY(), 1f, viewWidth, viewHeight);

        camera.cancelAutoFocus();
        Camera.Parameters params = camera.getParameters();
        if (params.getMaxNumFocusAreas() > 0) {
            List<Camera.Area> focusAreas = new ArrayList<>();
            focusAreas.add(new Camera.Area(focusRect, 800));
            params.setFocusAreas(focusAreas);
        } else {
            Log.i(TAG, "focus areas not supported");
        }
        final String currentFocusMode = params.getFocusMode();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
        camera.setParameters(params);

        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                Camera.Parameters params = camera.getParameters();
                params.setFocusMode(currentFocusMode);
                camera.setParameters(params);
            }
        });

        /*
        触摸测光
         */
        Rect meteringRect = calculateTapArea(event.getX(), event.getY(), 1.5f, viewWidth, viewHeight);

        if (params.getMaxNumMeteringAreas() > 0) {
            List<Camera.Area> meteringAreas = new ArrayList<>();
            meteringAreas.add(new Camera.Area(meteringRect, 800));
            params.setMeteringAreas(meteringAreas);
        } else {
            Log.i(TAG, "metering areas not supported");
        }
    }

    /*
    二指手势缩放
     */
    private float oldDist = 1f;
    private static float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void handleZoom(boolean isZoomIn, Camera camera) {
        Camera.Parameters params = camera.getParameters();
        if (params.isZoomSupported()) {
            int maxZoom = params.getMaxZoom();
            int zoom = params.getZoom();
            if (isZoomIn && zoom < maxZoom) {
                zoom++;
            } else if (zoom > 0) {
                zoom--;
            }
            params.setZoom(zoom);
            camera.setParameters(params);
        } else {
            Log.i(TAG, "zoom not supported");
        }
    }

    /*
    文件保存
     */
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;
    private static Uri outputMediaFileUri;
    private static String outputMediaFileType;

    private File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), ALBUM_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
            outputMediaFileType = "image/*";
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
            outputMediaFileType = "video/*";
        } else {
            return null;
        }
        outputMediaFileUri = Uri.fromFile(mediaFile);
        return mediaFile;
    }

    //保存文件到指定路径
    private static boolean saveImageToGallery(Context context, byte[] data) {
        // 首先保存图片
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +ALBUM_NAME ;
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            if (!appDir.mkdir()) {
                Log.d(TAG, "failed to create directory");
                return false;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String fileName = System.currentTimeMillis() + ".jpg";
        File mediaFile = new File(storePath + File.separator +
                "IMG_" + timeStamp + ".jpg");
        outputMediaFileType = "image/*";
        try {
            FileOutputStream fos = new FileOutputStream(mediaFile);
            //通过io流的方式来压缩保存图片
            fos.write(data);
            fos.close();
            //把文件插入到系统图库
            MediaStore.Images.Media.insertImage(context.getContentResolver(), mediaFile.getAbsolutePath(), fileName, null);
            outputMediaFileUri = Uri.fromFile(mediaFile);
            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(mediaFile);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Uri getOutputMediaFileUri() {
        return outputMediaFileUri;
    }

    private String getOutputMediaFileType() {
        return outputMediaFileType;
    }

    /*
    录像
    https://www.polarxiong.com/archives/Android%E7%9B%B8%E6%9C%BA%E5%BC%80%E5%8F%91-%E4%B8%89-%E5%AE%9E%E7%8E%B0%E6%8B%8D%E7%85%A7%E5%BD%95%E5%83%8F%E5%92%8C%E6%9F%A5%E7%9C%8B.html
     */
    private MediaRecorder mMediaRecorder;

    private boolean startRecording() {
        if (prepareVideoRecorder()) {
            mMediaRecorder.start();
            return true;
        } else {
            releaseMediaRecorder();
        }
        return false;
    }

    private void stopRecording() {
        stopRecording(null);
    }

    private void stopRecording(final ImageView view) {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            if(view!=null){
                Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(outputMediaFileUri.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
                view.setImageBitmap(thumbnail);
            }
        }
        releaseMediaRecorder();
    }

    private boolean isRecording() {
        return mMediaRecorder != null;
    }

    private boolean prepareVideoRecorder() {

        mCamera = getCameraInstance();
        mMediaRecorder = new MediaRecorder();

        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String prefVideoSize = prefs.getString("video_size", "");
        String[] split = prefVideoSize.split("x");
        mMediaRecorder.setVideoSize(Integer.parseInt(split[0]), Integer.parseInt(split[1]));

        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        mMediaRecorder.setPreviewDisplay(mHolder.getSurface());

        /*
        使得录像得到的视频就和预览的方向一致
         */
        int rotation = getDisplayOrientation();
        mMediaRecorder.setOrientationHint(rotation);

        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();
        }
    }

    /**
     * If you want to make the camera image show in the same orientation as the display, you can use the following code.
     */
    private void setCameraDisplayOrientation() {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(CAMERA_NOW, info);
        int rotation = getDisplayOrientation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(result);
    }

    /*
    让Camera.open()在非UI线程执行,
    保证UI线程速度
     */
    private void openCameraOriginal(){
        if (mCamera==null){
            try {
                mCamera = Camera.open();
            } catch (Exception e) {
                Log.d(TAG, "camera is not available "+e.getMessage());
            }
        }else {
            //switch camera
            int cameraCount = 0;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras();
            for(int i = 0; i < cameraCount; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == CAMERA_NOW) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    mCamera.setPreviewCallback(null);
                    mCamera.stopPreview();//停掉原来摄像头的预览
                    mCamera.lock();
                    mCamera.release();//释放资源
                    mCamera = null;//取消原来摄像头
                    mCamera = Camera.open(i);//打开当前选中的摄像头
                    try {
                        mCamera.setPreviewDisplay(mHolder);//通过surfaceview显示取景画面
                    } catch (IOException e) {
                        Log.d(TAG, "camera switch error "+e.getMessage());
                    }
                    mCamera.startPreview();//开始预览
                    break;
                }
            }
        }
        setCameraDisplayOrientation();
    }

    private class CameraHandlerThread extends HandlerThread {
        Handler mHandler;

        public CameraHandlerThread(String name) {
            super(name);
            start();
            mHandler = new Handler(getLooper());
        }

        synchronized void notifyCameraOpened() {
            notify();
        }

        void openCamera() {
            /*
            Runnable()异步执行
            post()执行立即返回
             */
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    openCameraOriginal();//执行camera的open
                    /*
                    加上notify-wait控制，确认打开相机后，openCamera()才返回
                     */
                    notifyCameraOpened();
                }
            });
            try {
                wait();
            } catch (InterruptedException e) {
                Log.w(TAG, "wait was interrupted "+e.getMessage());
            }
        }
    }

}
