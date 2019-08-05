package com.example.artistcamera.DataLayer.Bean;

import java.util.List;

public class PoemBean {
    private Boolean IsAdult;
    private Boolean IsPolitician;
    private Boolean ShowImage;
    private List<OpenPoemsBean> OpenPoems;

    public Boolean getAdult() {
        return IsAdult;
    }

    public void setAdult(Boolean adult) {
        IsAdult = adult;
    }

    public Boolean getPolitician() {
        return IsPolitician;
    }

    public void setPolitician(Boolean politician) {
        IsPolitician = politician;
    }

    public Boolean getShowImage() {
        return ShowImage;
    }

    public void setShowImage(Boolean showImage) {
        ShowImage = showImage;
    }

    public List<OpenPoemsBean> getOpenPoems() {
        return OpenPoems;
    }

    public void setOpenPoems(List<OpenPoemsBean> openPoems) {
        OpenPoems = openPoems;
    }
}
