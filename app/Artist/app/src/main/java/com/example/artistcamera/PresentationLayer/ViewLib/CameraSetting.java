package com.example.artistcamera.PresentationLayer.ViewLib;

import android.hardware.Camera;

public class CameraSetting {
    private static Camera mCamera;
    private static Camera.Parameters mParameters;

    public static void passCamera(Camera camera) {
        mCamera = camera;
        mParameters = camera.getParameters();
    }


    public void setFlash(){

    }

    public void setFocus(){

    }

    public void setPreviewQuality(){

    }

    public void setPhotoQuality(){

    }
}
