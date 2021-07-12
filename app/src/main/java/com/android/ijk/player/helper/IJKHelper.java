package com.android.ijk.player.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.android.ijk.player.view.IJKVideoView;

/**
 * Author: Relin
 * Describe:ijk视频助手
 * Date:2020/5/20 14:21
 */
public class IJKHelper {

    /**
     * 标识
     */
    private static String TAG = "IJKHelper";
    /**
     * 当前音量
     */
    private float currentVoice = 0f;
    /**
     * 按下坐标
     */
    private float downX, downY;
    /**
     * 视频当前位置
     */
    private long position;
    /**
     * 是否改变进度
     */
    private boolean isChangeVideoProgress;
    /**
     * 竖屏布局参数
     */
    private ViewGroup.LayoutParams portraitParams;
    /**
     * 是否有ActionBar
     */
    private boolean isHaveActionBar;

    /**
     * 保持屏幕常亮
     * 要在setContentView()之前调用
     */
    public void keepScreenOn(Context context) {
        Activity activity = (Activity) context;
        if (activity == null) {
            return;
        }
        if (activity.findViewById(android.R.id.content) != null) {
            new RuntimeException("Setting screen constants requires a call before the setContentView () method super");
            return;
        }
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 找到容器视图
     *
     * @param context 上下文对象
     * @return
     */
    public FrameLayout findContent(Context context) {
        Activity activity = (Activity) context;
        if (activity == null) {
            return null;
        }
        return activity.findViewById(android.R.id.content);
    }

    /**
     * 转换屏幕
     *
     * @param orientation 方向
     */
    @SuppressLint("SourceLockedOrientationActivity")
    public void switchScreen(Context context, ViewGroup parent, IJKVideoView videoView, Surface surface, Orientation orientation) {
        AppCompatActivity activity = (AppCompatActivity) context;
//        FrameLayout content = findContent(context);
//        if (content == null) {
//            new RuntimeException("switch screen failed,find activity content is null.");
//            return;
//        }
//        if (videoView == null) {
//            new RuntimeException("switch screen failed,don't find view to do anything.");
//        }
        //切换横屏
        if (orientation == Orientation.Horizontal) {
            Log.i(TAG,"->switchScreen Horizontal");
            portraitParams = videoView.getLayoutParams();
            //隐藏ActionBar
            if (activity.getSupportActionBar() != null) {
                isHaveActionBar = activity.getSupportActionBar().isShowing();
                activity.getSupportActionBar().hide();
            }
            //横屏
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            //全屏标识
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //隐藏状态栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            ViewGroup.LayoutParams params = videoView.getLayoutParams();
            params.width = FrameLayout.LayoutParams.MATCH_PARENT;
            params.height = FrameLayout.LayoutParams.MATCH_PARENT;
            videoView.setLayoutParams(params);

            //将View添加到Content
//            ViewGroup parentView = (ViewGroup) videoView.getParent();
//            parentView.removeView(videoView);
//            if (content != null) {
//                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
//                content.addView(videoView, params);
//                videoView.setSurface(surface);
//            }
        }
        //切换竖屏
        if (orientation == Orientation.Vertical) {
            Log.i(TAG,"->switchScreen Vertical");
            //显示ActionBar
            if (isHaveActionBar) {
                activity.getSupportActionBar().show();
            }
            //竖屏
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            //将View复原
            videoView.setLayoutParams(portraitParams);
            //清除全屏标识
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //显示状态栏
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


//            ViewGroup parentView = (ViewGroup) videoView.getParent();
//            parentView.removeView(videoView);
//            parent.addView(videoView, portraitParams);
//            videoView.setSurface(surface);
        }
    }

    /**
     * 当前屏幕亮度
     *
     * @param context 上下文
     * @return
     */
    public float currentBrightness(Context context) {
        Activity activity = (Activity) context;
        if (activity == null) {
            return 0.0f;
        }
        Window window = activity.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        float brightness = lp.screenBrightness;
        if (brightness == 0.0F || brightness == 1.0F) {
            try {
                brightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS) / 255.0F;
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        }
        return brightness;
    }

    /**
     * 改变亮度
     *
     * @param brightness [0-1]
     */
    public void changeBrightness(Context context, float brightness) {
        Activity activity = (Activity) context;
        if (activity == null) {
            return;
        }
        Window window = activity.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness;
        window.setAttributes(lp);
    }

    /**
     * 当前音量值
     *
     * @param context 上下文对象
     * @return
     */
    public float currentVoice(Context context) {
        if (currentVoice != 0) {
            return currentVoice;
        }
        AudioManager audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        currentVoice = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        return currentVoice;
    }

    /**
     * 最大音量
     *
     * @param context
     * @return
     */
    public float maxVoice(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 改变声音
     *
     * @param voiceValue 0-1
     */
    public void changeVoice(Context context, float voiceValue) {
        currentVoice = voiceValue;
        AudioManager audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) voiceValue, AudioManager.FLAG_PLAY_SOUND);
    }

    /**
     * 触摸事件
     *
     * @param context      上下文对象
     * @param isLiveSource 是否是直播
     * @param event        事件
     * @param view         控件
     * @param current      视频播放位置
     * @param duration     视频时长
     * @param listener     事件监听
     * @return
     */
    public boolean onTouchEvent(Context context, boolean isLiveSource, MotionEvent event, View view, long current, long duration, OnIjkVideoTouchListener listener) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "->onTouchEvent ACTION_DOWN");
                downX = event.getRawX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (listener != null) {
                    listener.onVideoControlViewShow(event);
                }
                float distanceXValue = event.getX() - downX;
                float distanceYValue = event.getY() - downY;
                float distanceX = Math.abs(distanceXValue);
                float distanceY = Math.abs(distanceYValue);
                float tan = distanceY / distanceX;
                float width = view.getMeasuredWidth();
                float height = view.getMeasuredHeight();
                if (tan <= 1) {
                    if (!isLiveSource) {
                        isChangeVideoProgress = true;
                        float horizontalPercent = distanceX / width;
                        position = (long) increaseDecreaseValue(distanceXValue, horizontalPercent, current, duration, 0, 1f, Orientation.Horizontal);
                        Log.i(TAG, "->onTouchEvent ACTION_MOVE Horizontal percent:" + (position * 1.0f / duration) + ",duration:" + duration + ",current:" + current + ",position:" + position);
                        if (listener != null) {
                            listener.onVideoStartChangeProgress(position, position * 1.0f / duration);
                        }
                    }
                } else {
                    float verticalPercent = distanceY / height;
                    //左边右边判断
                    float horizontalMiddleX = view.getMeasuredWidth() / 2;
                    if (downX < horizontalMiddleX) {//左边
                        //当前亮度值
                        float brightness = increaseDecreaseValue(distanceYValue, verticalPercent, currentBrightness(context), 1f, 0f, 0.02f, Orientation.Vertical);
                        Log.i(TAG, "->onTouchEvent ACTION_MOVE Vertical Left percent:" + (brightness / 1f) + ",brightness：" + brightness);
                        if (listener != null) {
                            listener.onVideoChangeBrightness(brightness, brightness / 1f);
                        }
                    } else {//右边
                        float currentVoice = currentVoice(context);
                        float maxVoice = maxVoice(context);
                        Log.i(TAG, "->onTouchEvent ACTION_MOVE Vertical Right currentVoice:" + currentVoice + ",maxVoice：" + maxVoice);
                        //当前亮度值
                        float voice = increaseDecreaseValue(distanceYValue, verticalPercent, currentVoice, maxVoice, 0, 0.02f, Orientation.Vertical);
                        Log.i(TAG, "->onTouchEvent ACTION_MOVE Vertical Right percent:" + verticalPercent + ",voice：" + voice);
                        if (listener != null) {
                            listener.onVideoChangeVoice(voice, voice / maxVoice);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "->onTouchEvent ACTION_UP");
                if (listener != null && isChangeVideoProgress) {
                    listener.onVideoStopChangeProgress(position, position * 1.0f / duration);
                    isChangeVideoProgress = false;
                }
                if (listener != null) {
                    listener.onVideoControlViewHide(event);
                }
                break;
        }
        return true;
    }

    /**
     * 增减值处理
     *
     * @param distanceValue   移动终点 - 起点的值
     * @param verticalPercent 竖向移动百分比
     * @param currentValue    当前值
     * @param maxValue        最大值
     * @param minValue        最小值
     * @param coefficient     系数值
     * @param orientation     滑动方向
     * @return
     */
    public float increaseDecreaseValue(float distanceValue, float verticalPercent, float currentValue, float maxValue, float minValue, float coefficient, Orientation orientation) {
        if (orientation == Orientation.Vertical) {
            if (distanceValue > 0) {//向下滑动
                currentValue -= verticalPercent * maxValue * coefficient;
            } else {//向上滑动
                currentValue += verticalPercent * maxValue * coefficient;
            }
        }
        if (orientation == Orientation.Horizontal) {
            if (distanceValue > 0) {//向右滑动
                currentValue += verticalPercent * maxValue * coefficient;
            } else {//向左滑动
                currentValue -= verticalPercent * maxValue * coefficient;
            }
        }
        if (currentValue > maxValue) {
            currentValue = maxValue;
        }
        if (currentValue < minValue) {
            currentValue = minValue;
        }
        return currentValue;
    }
}
