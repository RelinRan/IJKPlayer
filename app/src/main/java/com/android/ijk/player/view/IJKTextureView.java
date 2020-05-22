package com.android.ijk.player.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.view.View;

import com.android.ijk.player.IJK;

/**
 * Author: Relin
 * Describe:ijk视频显示图层
 * Date:2020/5/11 16:21
 */
public class IJKTextureView extends TextureView {

    protected static final String TAG = "IJKTextureView";

    public int currentVideoWidth = 0;
    public int currentVideoHeight = 0;

    public IJKTextureView(Context context) {
        super(context);
        currentVideoWidth = 0;
        currentVideoHeight = 0;
    }

    public IJKTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        currentVideoWidth = 0;
        currentVideoHeight = 0;
    }

    public void setVideoSize(int currentVideoWidth, int currentVideoHeight) {
        if (this.currentVideoWidth != currentVideoWidth || this.currentVideoHeight != currentVideoHeight) {
            this.currentVideoWidth = currentVideoWidth;
            this.currentVideoHeight = currentVideoHeight;
            requestLayout();
        }
    }

    @Override
    public void setRotation(float rotation) {
        if (rotation != getRotation()) {
            super.setRotation(rotation);
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(TAG, "onMeasure " + " [" + this.hashCode() + "] ");
        int viewRotation = (int) getRotation();
        int videoWidth = currentVideoWidth;
        int videoHeight = currentVideoHeight;
        int parentHeight = ((View) getParent()).getMeasuredHeight();
        int parentWidth = ((View) getParent()).getMeasuredWidth();
        if (parentWidth != 0 && parentHeight != 0 && videoWidth != 0 && videoHeight != 0) {
            //填充全屏
            if (IJK.config().displayType() == IJK.DISPLAY_MATCH_PARENT) {
                if (viewRotation == 90 || viewRotation == 270) {
                    int tempSize = parentWidth;
                    parentWidth = parentHeight;
                    parentHeight = tempSize;
                }
                videoHeight = videoWidth * parentHeight / parentWidth;
            }
        }
        // 如果判断成立，则说明显示的TextureView和本身的位置是有90度的旋转的，所以需要交换宽高参数。
        if (viewRotation == 90 || viewRotation == 270) {
            int tempMeasureSpec = widthMeasureSpec;
            widthMeasureSpec = heightMeasureSpec;
            heightMeasureSpec = tempMeasureSpec;
        }
        int width = getDefaultSize(videoWidth, widthMeasureSpec);
        int height = getDefaultSize(videoHeight, heightMeasureSpec);
        if (videoWidth > 0 && videoHeight > 0) {
            int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
            Log.i(TAG, "widthMeasureSpec  [" + MeasureSpec.toString(widthMeasureSpec) + "]");
            Log.i(TAG, "heightMeasureSpec [" + MeasureSpec.toString(heightMeasureSpec) + "]");
            if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                width = widthSpecSize;
                height = heightSpecSize;
                if (videoWidth * height < width * videoHeight) {
                    width = height * videoWidth / videoHeight;
                } else if (videoWidth * height > width * videoHeight) {
                    height = width * videoHeight / videoWidth;
                }
            } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                width = widthSpecSize;
                height = width * videoHeight / videoWidth;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    height = heightSpecSize;
                    width = height * videoWidth / videoHeight;
                }
            } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                height = heightSpecSize;
                width = height * videoWidth / videoHeight;
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    width = widthSpecSize;
                    height = width * videoHeight / videoWidth;
                }
            } else {
                width = videoWidth;
                height = videoHeight;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    height = heightSpecSize;
                    width = height * videoWidth / videoHeight;
                }
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    width = widthSpecSize;
                    height = width * videoHeight / videoWidth;
                }
            }
        } else {

        }
        if (parentWidth != 0 && parentHeight != 0 && videoWidth != 0 && videoHeight != 0) {
            if (IJK.config().displayType() == IJK.DISPLAY_WRAP_CONTENT) {
                height = videoHeight;
                width = videoWidth;
            } else if (IJK.config().displayType() == IJK.DISPLAY_MATCH_CROP) {
                if (viewRotation == 90 || viewRotation == 270) {
                    int tempSize = parentWidth;
                    parentWidth = parentHeight;
                    parentHeight = tempSize;
                }
                if (((double) videoHeight / videoWidth) > ((double) parentHeight / parentWidth)) {
                    height = (int) (((double) parentWidth / (double) width * (double) height));
                    width = parentWidth;
                } else if (((double) videoHeight / videoWidth) < ((double) parentHeight / parentWidth)) {
                    width = (int) (((double) parentHeight / (double) height * (double) width));
                    height = parentHeight;
                }
            }
        }
        setMeasuredDimension(width, height);
    }
}
