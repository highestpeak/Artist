package com.example.artistcamera.PresentationLayer.ViewLib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import java.util.Random;

public class DirectSuggest  extends AppCompatImageView {
    private Context context;
    private Paint suggestPaint;
    //是否正在建议
    private boolean isSuggesting;

    public DirectSuggest(Context context) {
        this(context, null, 0);
    }

    public DirectSuggest(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DirectSuggest(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        //画笔设置
        suggestPaint = new Paint();
        suggestPaint.setColor(Color.YELLOW);
        suggestPaint.setStyle(Paint.Style.STROKE);
        suggestPaint.setStrokeWidth(3);
    }

    public boolean isSuggesting() {
        return isSuggesting;
    }

    public void setSuggesting(boolean suggesting) {
        isSuggesting = suggesting;
    }

    public enum SUGGEST_DIRECT {
        LEFT_TOP,       TOP,        RIGHT_TOP,
        LEFT_CENTER,    CENTER,     RIGHT_CENTER,
        LEFT_BOTTOM,    BOTTOM,     RIGHT_BOTTOM,
        NONE;
        public static SUGGEST_DIRECT getRandom() {
            return values()[(int) (Math.random() * values().length)];
        }
    }

    private SUGGEST_DIRECT suggestDirect=SUGGEST_DIRECT.NONE;

    public SUGGEST_DIRECT getSuggestDirect() {
        return suggestDirect;
    }

    public void setSuggestDirect(SUGGEST_DIRECT suggestDirect) {
        this.suggestDirect = suggestDirect;
    }

    public Point getCenterPoint() {
        return centerPoint;
    }

    public void setCenterPoint(Point centerPoint) {
        this.centerPoint = centerPoint;
    }

    private Integer left;
    private Integer top;
    private Integer right;
    private Integer bottom;
    private Point centerPoint;
    public void setSuggest(final Point centerPoint,
                            final int rectSize,
                            SUGGEST_DIRECT suggestDirect){
        if(centerPoint==null){
            return;
        }
        left=(int)(centerPoint.x-rectSize/2);
        top=(int)(centerPoint.y-rectSize/2);
        right=(int)(centerPoint.x+rectSize/2);
        bottom=(int)(centerPoint.y+rectSize/2);
        setSuggestDirect(suggestDirect);
        setCenterPoint(centerPoint);
        postInvalidate();
    }

    private Integer linePadding=2;
    private void drawSuggest(Canvas canvas) {
        if(centerPoint==null || suggestDirect==null){
            return;
        }
        int xToCenter=centerPoint.x;
        int yToCenter=centerPoint.y;
        switch (suggestDirect){
            case LEFT_TOP:
                canvas.drawRect(left,top,centerPoint.x,top+linePadding,suggestPaint);
                canvas.drawRect(left,top,left+linePadding,centerPoint.y,suggestPaint);
                xToCenter=left;
                yToCenter=top;
                break;
            case TOP:
                canvas.drawLine(centerPoint.x,top,left,centerPoint.y,suggestPaint);
                canvas.drawLine(centerPoint.x,top,right,centerPoint.y,suggestPaint);
                xToCenter=centerPoint.x;
                yToCenter=top;
                break;
            case RIGHT_TOP:
                canvas.drawRect(centerPoint.x,top,right,top+linePadding,suggestPaint);
                canvas.drawRect(right-linePadding,top,right,centerPoint.y,suggestPaint);
                xToCenter=right;
                yToCenter=top;
                break;
            case LEFT_CENTER:
                canvas.drawLine(left,centerPoint.y,centerPoint.x,top,suggestPaint);
                canvas.drawLine(left,centerPoint.y,centerPoint.x,bottom,suggestPaint);
                xToCenter=left;
                yToCenter=centerPoint.y;
                break;
            case CENTER:
                canvas.drawRect((centerPoint.x+left)/2,
                        (centerPoint.y+top)/2,
                        (centerPoint.x+right)/2,
                        (centerPoint.y+bottom)/2,
                        suggestPaint);
                break;
            case RIGHT_CENTER:
                canvas.drawLine(right,centerPoint.y,centerPoint.x,top,suggestPaint);
                canvas.drawLine(right,centerPoint.y,centerPoint.x,bottom,suggestPaint);
                xToCenter=right;
                yToCenter=centerPoint.y;
                break;
            case LEFT_BOTTOM:
                canvas.drawRect(left,bottom-linePadding,centerPoint.x,bottom,suggestPaint);
                canvas.drawRect(left,centerPoint.y,left+linePadding,bottom,suggestPaint);
                xToCenter=left;
                yToCenter=bottom;
                break;
            case BOTTOM:
                canvas.drawLine(centerPoint.x,bottom,left,centerPoint.y,suggestPaint);
                canvas.drawLine(centerPoint.x,bottom,right,centerPoint.y,suggestPaint);
                xToCenter=centerPoint.x;
                yToCenter=bottom;
                break;
            case RIGHT_BOTTOM:
                canvas.drawRect(centerPoint.x,bottom-linePadding,right,bottom,suggestPaint);
                canvas.drawRect(right,centerPoint.y,right-linePadding,bottom,suggestPaint);
                xToCenter=right;
                yToCenter=bottom;
                break;
            case NONE:
                break;
        }
        if(xToCenter!=centerPoint.x || yToCenter!=centerPoint.y){
            canvas.drawLine(xToCenter,yToCenter,centerPoint.x,centerPoint.y,suggestPaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawSuggest(canvas);
        super.onDraw(canvas);
    }
}
