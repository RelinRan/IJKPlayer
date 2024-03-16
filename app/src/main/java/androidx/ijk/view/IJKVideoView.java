package androidx.ijk.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.ijk.IJK;
import androidx.ijk.IJKOption;
import androidx.ijk.R;
import androidx.ijk.helper.IJKHelper;
import androidx.ijk.helper.OnIjkVideoTouchListener;
import androidx.ijk.helper.Orientation;
import androidx.ijk.listener.OnIJKVideoListener;
import androidx.ijk.listener.OnIJKVideoSwitchScreenListener;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Author: Relin
 * Describe:IJK视频播放器
 * Date:2020/5/11 17:32
 */
public class IJKVideoView extends FrameLayout implements TextureView.SurfaceTextureListener, IMediaPlayer.OnInfoListener,
        IMediaPlayer.OnPreparedListener, IMediaPlayer.OnVideoSizeChangedListener, IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnErrorListener, IMediaPlayer.OnSeekCompleteListener,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener, OnIjkVideoTouchListener, Cloneable {

    private String TAG = IJKVideoView.class.getSimpleName();
    /**
     * 播放对象
     */
    private IjkMediaPlayer mediaPlayer;
    private IMediaPlayer iMediaPlayer;
    /**
     * 显示容器
     */
    private IJKTextureView textureView;
    /**
     * Video - surface
     */
    private Surface surface;
    /**
     * 视频加载Header
     */
    private Map<String, String> header;
    /**
     * 视频URI
     */
    private Uri uri;
    /**
     * 视频路径
     */
    private String path;
    /**
     * 视频监听
     */
    private OnIJKVideoListener onIJKVideoListener;
    /**
     * 视频控制View
     */
    private View controlView;
    /**
     * 控制器ViewHolder
     */
    private IJKControlViewHolder controlViewHolder;
    /**
     * 是否播放结束
     */
    private boolean isPlayEnd;
    /**
     * IJK助手
     */
    private IJKHelper ijkHelper;
    /**
     * 视频控件父容器
     */
    private ViewGroup container;
    /**
     * 最新图
     */
    private Bitmap bitmap;
    /**
     * 是否是直播数据源
     */
    private boolean liveSource;
    /**
     * 直播开始时间
     */
    private long liveStartTime = 0;
    /**
     * 是否已经准备过
     */
    private boolean isPrepared;

    /**
     * 屏幕切换监听
     *
     * @param context
     */
    private OnIJKVideoSwitchScreenListener onIJKVideoSwitchScreenListener;


    public IJKVideoView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public IJKVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public IJKVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 设置是否是直播源
     *
     * @param liveSource
     */
    public void setLiveSource(boolean liveSource) {
        this.liveSource = liveSource;
        if (isLiveSource()) {
            controlViewHolder.getSeekBar().setEnabled(false);
            controlViewHolder.getSeekBar().setThumb(null);
        } else {
            controlViewHolder.getSeekBar().setEnabled(true);
            controlViewHolder.getSeekBar().setThumb(ContextCompat.getDrawable(getContext(), R.drawable.ijk_seek_dot));
        }
    }

    /**
     * 是否是直播源
     *
     * @return
     */
    public boolean isLiveSource() {
        return liveSource;
    }

    /**
     * 设置屏幕切换监听
     *
     * @param onIJKVideoSwitchScreenListener
     */
    public void setOnIJKVideoSwitchScreenListener(OnIJKVideoSwitchScreenListener onIJKVideoSwitchScreenListener) {
        this.onIJKVideoSwitchScreenListener = onIJKVideoSwitchScreenListener;
    }

    /**
     * 初始化播放器
     *
     * @param context 上下文
     * @param attrs   xml参数
     */
    private void init(Context context, AttributeSet attrs) {
        setBackgroundColor(Color.BLACK);
        ijkHelper = new IJKHelper();
        container = (ViewGroup) getParent();
        initMediaPlayer();
        initVideoSurface(context);
        initControlViews();
    }

    /**
     * 播放操作助手
     *
     * @return
     */
    public IJKHelper getIjkHelper() {
        return ijkHelper;
    }

    /**
     * 初始化媒体对象
     */
    public void initMediaPlayer() {
        //禁用多点触控
        setMotionEventSplittingEnabled(false);
        //初始化
        mediaPlayer = new IjkMediaPlayer();
        //配置参数
        List<IJKOption> options = IJK.config().options();
        for (int i = 0; i < options.size(); i++) {
            IJKOption option = options.get(i);
            if (option.getValue() instanceof String) {
                mediaPlayer.setOption(option.getCategory(), option.getName(), (String) option.getValue());
            }
            if (option.getValue() instanceof Long) {
                mediaPlayer.setOption(option.getCategory(), option.getName(), (Long) option.getValue());
            }
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setScreenOnWhilePlaying(true);
        //设置监听
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnVideoSizeChangedListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnErrorListener(this);
        if (onIJKVideoListener != null) {
            onIJKVideoListener.onVideoSeekEnable(true);
        }
    }

    /**
     * 初始化视频显示器
     *
     * @param context
     */
    public void initVideoSurface(Context context) {
        //视频视图
        LayoutParams textureViewParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        textureViewParams.gravity = Gravity.CENTER;
        if (textureView != null && getChildAt(0) instanceof TextureView) {
            removeView(textureView);
        }
        textureView = new IJKTextureView(context);
        if (textureView != null) {
            textureView.setKeepScreenOn(true);
        }
        addView(textureView, 0, textureViewParams);
        if (textureView != null) {
            textureView.setSurfaceTextureListener(this);
        }
    }

    /**
     * 初始化控制器View
     */
    public void initControlViews() {
        //控制器
        controlView = IJK.config().controlView();
        if (controlView == null) {
            if (IJK.config().controlLayoutId() == 0) {
                IJK.config().controlLayoutId(R.layout.ijk_video_control);
            }
            controlView = LayoutInflater.from(getContext()).inflate(IJK.config().controlLayoutId(), null);
            LayoutParams controlViewParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            controlViewParams.gravity = Gravity.BOTTOM;
            addView(controlView, controlViewParams);
        }
        controlViewHolder = new IJKControlViewHolder(this, controlView);
        controlViewHolder.findViews();
        //中间控件隐藏
        controlViewHolder.getCenterImageView().setVisibility(GONE);
        controlViewHolder.getVoiceLightProgressView().setVisibility(GONE);
        controlViewHolder.getLoadingView().setVisibility(GONE);
        controlViewHolder.getCoverImageView().setVisibility(GONE);
        //底部播放按钮监听
        controlViewHolder.getPlayView().setOnClickListener(this);
        //底部屏幕转换按钮监听
        controlViewHolder.getScreenSwitchView().setOnClickListener(this);
        //中间播放按钮监听
        controlViewHolder.getCenterImageView().setOnClickListener(this);
        //进度条监听
        controlViewHolder.getSeekBar().setOnSeekBarChangeListener(this);
        //是否直播
        controlView.setVisibility(isLiveSource() ? GONE : VISIBLE);
    }

    @Override
    public void onClick(View view) {
        //底部播放、暂停 || 屏幕中间播放、暂停、重播
        if (view.getId() == R.id.iv_ijk_play || view.getId() == R.id.iv_ijk_center) {
            controlPlay(isPlayEnd);
        }
        //底部屏幕转换
        if (view.getId() == R.id.iv_ijk_screen) {
            Orientation orientation;
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                orientation = Orientation.LANDSCAPE;
            } else {
                orientation = Orientation.PORTRAIT;
            }
            //如果用户自己想操作屏幕的缩放，就使用此方法就行了
            if (onIJKVideoSwitchScreenListener != null) {
                onIJKVideoSwitchScreenListener.onIJKVideoSwitchScreen(orientation);
            } else {
                ijkHelper.switchScreen(getContext(), this, orientation);
            }
        }
    }

    //************************************[SeekBar监听]**************************************
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mediaPlayer.seekTo(progress);
            long current = mediaPlayer.getCurrentPosition();
            boolean isForward = progress - current > 0;
            float percent = progress * 1f / seekBar.getMax();
            showCircleProgressPercent(percent, isForward ? ProgressType.FORWARD : ProgressType.BACKWARD);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        controlViewHolder.getVoiceLightProgressView().setVisibility(GONE);
    }

    /**
     * 设置播放源
     *
     * @param path
     */
    public void setDataSource(String path) {
        this.path = path;
        if (!TextUtils.isEmpty(path)) {
            setDataSource(Uri.parse(path));
        }
    }

    /**
     * 设置播放源
     *
     * @param uri
     */
    public void setDataSource(Uri uri) {
        setDataSource(uri, null);
    }

    /**
     * 设置播放源
     *
     * @param uri
     * @param header
     */
    public void setDataSource(Uri uri, Map<String, String> header) {
        this.uri = uri;
        if (uri != null) {
            if (header == null) {
                header = new HashMap<>();
            }
            header.put("Cache-Control", "no-store");
        }
        this.header = header;
        try {
            mediaPlayer.setDataSource(getContext(), uri, header);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 控制视频播放
     *
     * @param isPlayEnd
     */
    private void controlPlay(boolean isPlayEnd) {
        if (isPlayEnd) {
            restart();
        } else {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                pause();
            } else {
                if (isPrepared) {
                    resume();
                } else {
                    start();
                }
            }
        }
    }

    /**
     * 是否正在播放
     *
     * @return
     */
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    /**
     * 获取播放源
     *
     * @return
     */
    public String getDataSource() {
        return mediaPlayer.getDataSource();
    }

    /**
     * 获取视频时长
     *
     * @return
     */
    public long getDuration() {
        return mediaPlayer.getDuration();
    }

    /**
     * 获取当前进度位置
     *
     * @return
     */
    public long getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    /**
     * 进度控制
     *
     * @param msec
     */
    public void seekTo(long msec) {
        mediaPlayer.seekTo(msec);
    }

    /**
     * 将播放器设置为循环播放机或非循环播放机。
     *
     * @param looping
     */
    public void setLooping(boolean looping) {
        mediaPlayer.setLooping(looping);
    }

    /**
     * 设置显示器
     *
     * @param holder
     */
    public void setDisplay(SurfaceHolder holder) {
        mediaPlayer.setDisplay(holder);
    }

    /**
     * 保持屏幕持续点亮 --避免息屏
     *
     * @param keepInBackground
     */
    public void setKeepInBackground(boolean keepInBackground) {
        mediaPlayer.setKeepInBackground(keepInBackground);
    }

    /**
     * 重置播放器
     */
    public void reset() {
        mediaPlayer.reset();
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
    }

    /**
     * 视频重新播放
     */
    public void restart() {
        showLoading();
        mediaPlayer.reset();
        mediaPlayer.setSurface(surface);
        controlViewHolder.getSeekBar().setProgress(0);
        showVideoTime(0, controlViewHolder.getCurrentView());
        showVideoTime(0, controlViewHolder.getDurationView());
        setDataSource(path);
        start();
    }

    /**
     * 视频暂停
     */
    public void pause() {
        controlViewHolder.getLoadingView().setVisibility(GONE);
        controlViewHolder.getCenterImageView().setVisibility(VISIBLE);
        controlViewHolder.getCenterImageView().setImageResource(R.mipmap.ic_ijk_pause_center);
        controlViewHolder.getPlayView().setImageResource(R.mipmap.ic_ijk_pause_control);
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
        stopVideoProgress();
    }

    /**
     * 视频播放完毕
     */
    protected void onCompletion() {
        controlViewHolder.getCenterImageView().setVisibility(VISIBLE);
        controlViewHolder.getCenterImageView().setImageResource(R.mipmap.ic_ijk_replay);
        controlViewHolder.getPlayView().setImageResource(R.mipmap.ic_ijk_pause_control);
        stopVideoProgress();
    }

    /**
     * 视频恢复播放
     */
    public void resume() {
        controlViewHolder.getCenterImageView().setVisibility(GONE);
        controlViewHolder.getPlayView().setImageResource(R.mipmap.ic_ijk_play_control);
        if (!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
        startVideoProgress();
    }

    /**
     * 异步准备
     */
    public void prepareAsync() {
        isPlayEnd = false;
        isPrepared = false;
        controlViewHolder.getCenterImageView().setVisibility(GONE);
        mediaPlayer.prepareAsync();
    }

    /**
     * 开始播放
     */
    public void start() {
        showLoading();
        isPlayEnd = false;
        isPrepared = false;
        controlViewHolder.getCenterImageView().setVisibility(GONE);
        mediaPlayer.prepareAsync();
    }

    /**
     * 释放播放器
     */
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    /**
     * 销毁资源
     */
    public void destroy() {
        release();
        AudioManager am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(null);
        stopVideoProgress();
        IjkMediaPlayer.native_profileEnd();
    }


    /**
     * 获取控制器View
     *
     * @return
     */
    public View getControlView() {
        return controlView;
    }

    /**
     * 获取控制器ViewHolder
     *
     * @return
     */
    public IJKControlViewHolder getControlViewHolder() {
        return controlViewHolder;
    }

    /**
     * 设置视频监听
     *
     * @param onIJKVideoListener
     */
    public void setOnIJKVideoListener(OnIJKVideoListener onIJKVideoListener) {
        this.onIJKVideoListener = onIJKVideoListener;
    }

    /**
     * 获取视频监听
     *
     * @return
     */
    public OnIJKVideoListener getOnIJKVideoListener() {
        return onIJKVideoListener;
    }

    /**
     * 获取播放器控制对象
     *
     * @return
     */
    public IjkMediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    /**
     * 获取视频路径
     *
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * 获取视频URI地址
     *
     * @return
     */
    public Uri getUri() {
        return uri;
    }

    /**
     * 获取显示器对象
     *
     * @return
     */
    public IJKTextureView getTextureView() {
        return textureView;
    }

    /**
     * @return
     */
    public SurfaceTexture getSurfaceTexture() {
        return textureView.getSurfaceTexture();
    }

    /**
     * 获取视频显示器
     *
     * @return
     */
    public Surface getSurface() {
        return surface;
    }

    /**
     * 设置视频显示器
     *
     * @param surface
     */
    public void setSurface(Surface surface) {
        this.surface = surface;
        if (mediaPlayer != null) {
            mediaPlayer.setSurface(surface);
        }
    }

    /**
     * 设置播放期间屏幕常亮
     *
     * @param screenOn
     */
    public void setScreenOnWhilePlaying(boolean screenOn) {
        if (mediaPlayer != null) {
            mediaPlayer.setScreenOnWhilePlaying(screenOn);
        }
    }

    /**
     * 获取容器
     *
     * @return
     */
    public ViewGroup getContainer() {
        return container;
    }

    //****************************************[TextureView - SurfaceTextureListener]**********************************************
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Log.i(TAG, "onSurfaceTextureAvailable " + width + "," + height);
        surface = new Surface(surfaceTexture);
        controlViewHolder.getCoverImageView().setVisibility(GONE);
        setSurface(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        Log.i(TAG, "onSurfaceTextureSizeChanged " + width + "," + height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        Log.i(TAG, "onSurfaceTextureDestroyed");
        surface.release();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        bitmap = textureView.getBitmap();
        Log.i(TAG, "onSurfaceTextureUpdated");
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            controlViewHolder.getCoverImageView().setVisibility(GONE);
        }
    }

    //****************************************[TextureView - SurfaceTextureListener]**********************************************
    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        this.iMediaPlayer = iMediaPlayer;
        isPrepared = true;
        if (isLiveSource()) {
            liveStartTime = System.currentTimeMillis();
        }
        if (surface != null) {
            iMediaPlayer.setSurface(surface);
        }
        Log.i(TAG, "onPrepared");
        if (onIJKVideoListener != null) {
            onIJKVideoListener.onVideoPrepared(iMediaPlayer);
        }
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int width, int height, int sarNum, int sarDen) {
        this.iMediaPlayer = iMediaPlayer;
        if (textureView != null) {
            textureView.setVideoSize(width, height);
        }
        Log.i(TAG, "onVideoSizeChanged width:" + width + ",height:" + height + ",sarNum：" + sarNum + ",sarDen:" + sarDen);
        if (onIJKVideoListener != null) {
            onIJKVideoListener.onVideoSizeChanged(iMediaPlayer, width, height, sarNum, sarDen);
        }
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int args) {
        this.iMediaPlayer = iMediaPlayer;
        Log.i(TAG, "onInfo what:" + what + ",args:" + args);
        if (what == IjkMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            controlViewHolder.getPlayView().setImageResource(R.mipmap.ic_ijk_play_control);
            if (onIJKVideoListener != null) {
                onIJKVideoListener.onVideoRenderingStart(iMediaPlayer, args);
            }
            //消失封面
            controlViewHolder.getCoverImageView().setVisibility(GONE);
            //消失Loading
            dismissLoading();
            //开始计时
            startVideoProgress();
            //如不是自动播放暂停
            if (!IJK.config().isAutoPlay()) {
                pause();
            }
        }
        if (what == IjkMediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
            if (onIJKVideoListener != null) {
                onIJKVideoListener.onVideoSeekEnable(false);
            }
        }
        if (what == IjkMediaPlayer.MEDIA_INFO_BUFFERING_START) {
            showLoading();
            if (onIJKVideoListener != null) {
                onIJKVideoListener.onVideoBufferingStart(iMediaPlayer, args);
            }
            stopVideoProgress();
        }
        if (what == IjkMediaPlayer.MEDIA_INFO_BUFFERING_END) {
            //消失封面
            controlViewHolder.getCoverImageView().setVisibility(GONE);
            //消失加载
            dismissLoading();
            if (onIJKVideoListener != null) {
                onIJKVideoListener.onVideoBufferingEnd(iMediaPlayer, args);
            }
            startVideoProgress();
        }
        if (what == IjkMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED) {
            if (onIJKVideoListener != null) {
                onIJKVideoListener.onVideoRotationChanged(iMediaPlayer, args);
            }
        }
        if (what == IjkMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING) {
            if (onIJKVideoListener != null) {
                onIJKVideoListener.onVideoTrackLagging(iMediaPlayer, args);
            }
        }
        if (what == IjkMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING) {
            dismissLoading();
            if (onIJKVideoListener != null) {
                onIJKVideoListener.onVideoBadInterleaving(iMediaPlayer, args);
            }
        }
        return false;
    }

    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {
        this.iMediaPlayer = iMediaPlayer;
        Log.i(TAG, "onSeekComplete");
        if (onIJKVideoListener != null) {
            onIJKVideoListener.onVideoSeekComplete(iMediaPlayer);
        }
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        isPlayEnd = true;
        onCompletion();
        this.iMediaPlayer = iMediaPlayer;
        //播放完毕，进度条满格
        showVideoTime(iMediaPlayer.getDuration(), controlViewHolder.getCurrentView());
        Log.i(TAG, "onCompletion");
        if (onIJKVideoListener != null) {
            onIJKVideoListener.onVideoCompletion(iMediaPlayer);
        }
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int framework_err, int impl_err) {
        dismissLoading();
        this.iMediaPlayer = iMediaPlayer;
        Log.i(TAG, "onError framework_err:" + framework_err + ",impl_err:" + impl_err);
        if (onIJKVideoListener != null) {
            onIJKVideoListener.onVideoError(iMediaPlayer, framework_err, impl_err);
        }
        return false;
    }

    /**
     * 开始进度获取标识
     */
    private int WHAT_GET_DURATION = 0;

    /**
     * 开始进度获取
     */
    private void startVideoProgress() {
        if (durationHandler != null) {
            durationHandler.sendEmptyMessage(WHAT_GET_DURATION);
        }
    }

    /**
     * 停止进度获取
     */
    private void stopVideoProgress() {
        if (durationHandler != null) {
            durationHandler.removeMessages(WHAT_GET_DURATION);
        }
    }

    /**
     * 时长Handler
     */
    private Handler durationHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_GET_DURATION) {
                long duration = isLiveSource() ? 0 : iMediaPlayer.getDuration();
                liveStartTime = liveStartTime == 0 ? System.currentTimeMillis() : liveStartTime;
                long current = isLiveSource() ? System.currentTimeMillis() - liveStartTime : iMediaPlayer.getCurrentPosition();
                Log.i(TAG, "onVideoProgress duration=" + duration + ",current=" + current + ",isLiveSource=" + isLiveSource());
                onVideoProgress(iMediaPlayer, duration, current);
                if (onIJKVideoListener != null) {
                    onIJKVideoListener.onVideoProgress(iMediaPlayer, iMediaPlayer.getDuration(), iMediaPlayer.getCurrentPosition());
                }
                sendEmptyMessageDelayed(WHAT_GET_DURATION, 1000);
            }
        }
    };

    /**
     * 视频进度
     *
     * @param iMediaPlayer 视频对象
     * @param duration     时长
     * @param current      当前进度
     */
    protected void onVideoProgress(IMediaPlayer iMediaPlayer, long duration, long current) {
        controlViewHolder.getSeekBar().setMax((int) duration);
        controlViewHolder.getSeekBar().setProgress((int) current);
        showVideoTime(current, controlViewHolder.getCurrentView());
        showVideoTime(duration, controlViewHolder.getDurationView());
    }

    /**
     * 显示视频时间
     *
     * @param time   时间
     * @param tvShow 控件
     */
    private static void showVideoTime(long time, TextView tvShow) {
        DecimalFormat format = new DecimalFormat("00");
        long second = time / 1000;
        long hour = second / 60 / 60;
        String timeText;
        if (hour > 0) {
            long videoMinutes = (second - hour * 3600) / 60;
            long videoSecond = second % 60;
            timeText = format.format(hour) + ":" + format.format(videoMinutes) + ":" + format.format(videoSecond);
        } else {
            long videoSecond = second % 60;
            long videoMinutes = second / 60;
            timeText = format.format(videoMinutes) + ":" + format.format(videoSecond);
        }
        tvShow.setText(timeText);
    }

    /**
     * 显示Loading
     */
    public void showLoading() {
        controlViewHolder.getLoadingView().setVisibility(VISIBLE);
        AnimationDrawable drawable = (AnimationDrawable) controlViewHolder.getLoadingImageView().getBackground();
        drawable.start();
    }

    /**
     * 消失Loading
     */
    public void dismissLoading() {
        controlViewHolder.getLoadingView().setVisibility(GONE);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                Log.i(TAG, "onInterceptTouchEvent ACTION_DOWN");
//                break;
//            case MotionEvent.ACTION_MOVE:
//                Log.i(TAG, "onInterceptTouchEvent ACTION_MOVE");
//                break;
//            case MotionEvent.ACTION_UP:
//                Log.i(TAG, "onInterceptTouchEvent ACTION_UP");
//                break;
//        }
        return super.onInterceptTouchEvent(ev);
    }

    //*******************************[onTouchEvent]*********************************
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return ijkHelper.onTouchEvent(getContext(), isLiveSource(), event, this, mediaPlayer == null ? 0 : mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration(), this);
    }

    @Override
    public void onVideoChangeBrightness(float value, float percent) {
        ijkHelper.changeBrightness(getContext(), value);
        showCircleProgressPercent(percent, ProgressType.BRIGHTNESS);
    }

    @Override
    public void onVideoChangeVoice(int value, float percent) {
        ijkHelper.changeVoice(getContext(), value);
        showCircleProgressPercent(percent, ProgressType.VOICE);
    }

    @Override
    public void onVideoStartChangeProgress(long value, float percent) {
        long current = mediaPlayer.getCurrentPosition();
        boolean isForward = value - current > 0;
        controlViewHolder.getSeekBar().setProgress((int) value);
        showCircleProgressPercent(percent, isForward ? ProgressType.FORWARD : ProgressType.BACKWARD);
    }

    @Override
    public void onVideoStopChangeProgress(long value, float percent) {
        mediaPlayer.seekTo(value);
        controlViewHolder.getSeekBar().setProgress((int) value);
    }

    @Override
    public void onVideoControlViewShow(MotionEvent event) {

    }

    @Override
    public void onVideoControlViewHide(MotionEvent event) {
        controlViewHolder.getVoiceLightProgressView().setVisibility(GONE);
    }

    @Override
    public void onVideoActionDown(MotionEvent event) {
        if (onClickListener!=null){
            onClickListener.onClick(this);
        }
    }

    @Override
    public void onVideoActionMove(MotionEvent event) {

    }

    @Override
    public void onVideoActionUp(MotionEvent event) {

    }


    private OnClickListener onClickListener;
    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
        onClickListener = l;
    }

    public enum ProgressType {
        VOICE, BRIGHTNESS, FORWARD, BACKWARD
    }

    /**
     * 显示声音亮度百分比
     *
     * @param percent 百分比[0-1]
     * @param type    进度类型
     */
    private void showCircleProgressPercent(float percent, ProgressType type) {
        controlViewHolder.getVoiceLightProgressView().setVisibility(VISIBLE);
        String progressText = "";
        if (type == ProgressType.VOICE) {
            progressText = "音";
        }
        if (type == ProgressType.BRIGHTNESS) {
            progressText = "亮";
        }
        if (type == ProgressType.FORWARD) {
            progressText = "进";
        }
        if (type == ProgressType.BACKWARD) {
            progressText = "退";
        }
        controlViewHolder.getVoiceLightProgressView().setProgressText(progressText);
        controlViewHolder.getVoiceLightProgressView().setMax(100);
        controlViewHolder.getVoiceLightProgressView().setProgress((int) (percent * 100f));
    }

}
