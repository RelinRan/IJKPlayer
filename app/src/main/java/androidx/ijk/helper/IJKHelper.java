package androidx.ijk.helper;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.ijk.view.IJKVideoView;


/**
 * Author: Relin
 * Describe:ijk视频助手
 * Date:2020/5/20 14:21
 */
public class IJKHelper {

    //标识
    private static String TAG = IJKHelper.class.getSimpleName();
    //按下坐标
    private float downX, downY;
    //是否改变进度
    private boolean isChangeVideoProgress;
    //竖屏布局参数
    private ViewGroup.LayoutParams portraitParams;
    private int portraitWidth = -3;
    private int portraitHeight = -3;
    //是否有ActionBar
    private boolean isHaveActionBar;
    //音量控制
    private AudioManager audioManager;
    //当前音量
    private int currentVolume = -1;
    //最大值音量
    private int maxVolume = -1;
    //当前亮度
    private float currentBrightness = -1F;
    //当前进度
    private long currentProgress = -1;
    //是否调试模式
    private boolean debug = true;


    public IJKHelper() {
        Log.d(TAG, "IJKHelper INIT");
    }

    /**
     * 设置是否调试模式
     *
     * @param debug
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

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
    public void switchScreen(Context context, IJKVideoView videoView, Orientation orientation) {
        AppCompatActivity activity = (AppCompatActivity) context;
        FrameLayout content = findContent(context);
        if (content == null) {
            new RuntimeException("switch screen failed,find activity content is null.");
            return;
        }
        if (videoView == null) {
            new RuntimeException("switch screen failed,don't find view to do anything.");
            return;
        }
        //切换横屏
        if (orientation == Orientation.LANDSCAPE) {
            Log.i(TAG, "->switchScreen Horizontal");
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
        } else {
            //显示ActionBar
            if (isHaveActionBar) {
                activity.getSupportActionBar().show();
            }
            //竖屏
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            //清除全屏标识
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        switchSettingViews(content, orientation);
    }

    /**
     * 设置屏幕切换对应View
     *
     * @param context     上下文
     * @param orientation 是否横屏
     */
    public void switchSettingViews(Context context, Orientation orientation) {
        switchSettingViews(findContent(context), orientation);
    }

    /**
     * 递归设置页面所有控件
     *
     * @param parent
     * @param orientation
     */
    public void switchSettingViews(ViewGroup parent, Orientation orientation) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                if (child instanceof IJKVideoView) {
                    ViewGroup.LayoutParams params = child.getLayoutParams();
                    if (orientation == Orientation.LANDSCAPE && portraitWidth == -3) {
                        portraitWidth = params.width;
                        portraitHeight = params.height;
                    }
                    params.width = orientation == Orientation.LANDSCAPE ? FrameLayout.LayoutParams.MATCH_PARENT : portraitWidth;
                    params.height = orientation == Orientation.LANDSCAPE ? FrameLayout.LayoutParams.MATCH_PARENT : portraitHeight;
                    child.setLayoutParams(params);
                    if (debug) {
                        Log.d(TAG, "child instanceof IJKVideoView portrait params width=" + portraitWidth + ",height=" + portraitHeight);
                    }
                } else {
                    switchSettingViews((ViewGroup) child, orientation);
                }
            } else {
                child.setVisibility(orientation == Orientation.LANDSCAPE ? View.GONE : View.VISIBLE);
            }
        }
    }

    /**
     * 是否存在视频播放的View
     *
     * @param parent
     * @return
     */
    private boolean hasVideoViewChild(ViewGroup parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (parent.getChildAt(i) instanceof IJKVideoView) {
                return true;
            }
        }
        return false;
    }

    /**
     * 当前屏幕亮度
     *
     * @param context 上下文
     * @return
     */
    public float getCurrentBrightness(Context context) {
        Activity activity = (Activity) context;
        if (activity == null) {
            return 0.0f;
        }
        Window window = activity.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        float brightness = lp.screenBrightness;
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
     * 获取音量控制
     *
     * @param context 上下文
     * @return
     */
    public AudioManager getAudioManager(Context context) {
        if (audioManager == null) {
            audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        }
        return audioManager;
    }

    /**
     * 当前音量值
     *
     * @param context 上下文对象
     * @return
     */
    public int getCurrentVoice(Context context) {
        return getAudioManager(context).getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 最大音量
     *
     * @param context
     * @return
     */
    public int getMaxVoice(Context context) {
        return getAudioManager(context).getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 改变声音
     *
     * @param voiceValue 0-1
     */
    public void changeVoice(Context context, int voiceValue) {
        getAudioManager(context).setStreamVolume(AudioManager.STREAM_MUSIC, (int) voiceValue, AudioManager.FLAG_PLAY_SOUND);
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
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (listener != null) {
                    listener.onVideoControlViewShow(event);
                }
                float deltaX = event.getX() - downX;
                float deltaY = event.getY() - downY;
                float absDeltaX = Math.abs(deltaX);
                float absDeltaY = Math.abs(deltaY);
                float width = view.getMeasuredWidth();
                float height = view.getMeasuredHeight();
                //向量角度
                double angle = Math.atan2(deltaY, deltaX) * 180 / Math.PI;
                boolean isHorizontalAngle = (angle > -45 && angle < 45) || (angle > -180 && angle < -135) || (angle > 135 && angle < 180);
                if (absDeltaX > 50 && absDeltaX > absDeltaY && isHorizontalAngle && isLiveSource == false) {
                    float horizontalPercent = deltaX / width;
                    if (currentProgress == -1) {
                        currentProgress = current;
                    }
                    long deltaProgress = (long) (duration * horizontalPercent);
                    long progress;
                    //根据手势方向调整音量
                    if (deltaX < 0) {
                        //向上滑动增大音量
                        progress = currentProgress - Math.abs(deltaProgress);
                        progress = progress < 0 ? 0 : progress;
                    } else {
                        //向下滑动减小音量
                        progress = currentProgress + Math.abs(deltaProgress);
                        progress = progress > duration ? duration : progress;
                    }
                    float progressPercent = progress * 1.0f / duration;
                    if (debug) {
                        Log.d(TAG, "deltaX：" + deltaX + ",progress:" + progress + ",duration:" + duration + ",progressPercent:" + progressPercent + ",angle:" + angle);
                    }
                    if (listener != null) {
                        listener.onVideoStartChangeProgress(progress, progressPercent);
                    }
                }

                boolean isVerticalAngle = ((angle > -135 && angle < -45) || (angle > 45 && angle < 135));
                if (absDeltaY > 50 && absDeltaY > absDeltaX && isVerticalAngle) {
                    float verticalPercent = deltaY / height;
                    //左边右边判断
                    float horizontalMiddleX = view.getMeasuredWidth() / 2;
                    if (downX < horizontalMiddleX) {//左边
                        //当前亮度值
                        if (currentBrightness == -1) {
                            currentBrightness = getCurrentBrightness(context);
                        }
                        float deltaBrightness = verticalPercent;
                        float brightness;
                        //根据手势方向调整音量
                        if (deltaY < 0) {
                            //向上滑动增大音量
                            brightness = currentBrightness + Math.abs(deltaBrightness);
                            brightness = brightness > 1f ? 1f : brightness;
                        } else {
                            //向下滑动减小音量
                            brightness = currentBrightness - Math.abs(deltaBrightness);
                            brightness = brightness < 0 ? 0 : brightness;
                        }
                        if (debug) {
                            Log.d(TAG, "brightness:" + brightness + ",brightnessPercent:" + brightness + ",verticalPercent:" + verticalPercent + ",angle:" + angle);
                        }
                        if (listener != null) {
                            listener.onVideoChangeBrightness(brightness, brightness);
                        }
                    } else {//右边
                        //根据 deltaY 的值来调整音量，可以根据具体需求进行调整
                        if (maxVolume == -1) {
                            maxVolume = getMaxVoice(context);
                        }
                        if (currentVolume == -1) {
                            currentVolume = getCurrentVoice(view.getContext());
                        }
                        //计算调整音量的大小
                        int deltaVolume = (int) (maxVolume * verticalPercent);
                        int volume;
                        //根据手势方向调整音量
                        if (deltaY < 0) {
                            //向上滑动增大音量
                            volume = currentVolume + Math.abs(deltaVolume);
                            volume = volume > maxVolume ? maxVolume : volume;
                        } else {
                            //向下滑动减小音量
                            volume = currentVolume - Math.abs(deltaVolume);
                            volume = volume < 0 ? 0 : volume;
                        }
                        float volumePercent = volume * 1F / maxVolume;
                        if (debug) {
                            Log.d(TAG, "maxVolume:" + maxVolume + ",volume:" + volume + ",volumePercent:" + volumePercent + ",verticalPercent:" + verticalPercent);
                        }
                        if (listener != null) {
                            listener.onVideoChangeVoice(volume, volume * 1F / maxVolume);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                currentProgress = -1;
                currentVolume = -1;
                currentBrightness = -1;
                if (listener != null) {
                    listener.onVideoControlViewHide(event);
                }
                break;
        }
        return true;
    }

}
