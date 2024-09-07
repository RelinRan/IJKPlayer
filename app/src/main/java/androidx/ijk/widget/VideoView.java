package androidx.ijk.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
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
import androidx.ijk.enums.Display;
import androidx.ijk.enums.Orientation;
import androidx.ijk.helper.OnVideoTouchListener;
import androidx.ijk.helper.VideoHelper;
import androidx.ijk.listener.OnVideoListener;
import androidx.ijk.listener.OnVideoSwitchScreenListener;
import androidx.ijk.model.Ratio;

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
public class VideoView extends FrameLayout implements android.view.TextureView.SurfaceTextureListener, IMediaPlayer.OnInfoListener,
        IMediaPlayer.OnPreparedListener, IMediaPlayer.OnVideoSizeChangedListener, IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnErrorListener, IMediaPlayer.OnSeekCompleteListener, IMediaPlayer.OnBufferingUpdateListener,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener, OnVideoTouchListener, Cloneable {

    private String TAG = VideoView.class.getSimpleName();
    /**
     * 播放对象
     */
    private IjkMediaPlayer mediaPlayer;
    private IMediaPlayer iMediaPlayer;
    /**
     * 显示容器
     */
    private VideoTextureView textureView;
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
    private OnVideoListener onIJKVideoListener;
    /**
     * 视频控制View
     */
    private View controlView;
    /**
     * 控制器ViewHolder
     */
    private VideoHolder videoHolder;
    /**
     * 是否播放结束
     */
    private boolean isPlayEnd;
    /**
     * IJK助手
     */
    private VideoHelper videoHelper;
    /**
     * 视频控件父容器
     */
    private ViewGroup container;
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
     * 时长Handler
     */
    private DurationTimer durationTimer;
    /**
     * 屏幕切换监听
     *
     * @param context
     */
    private OnVideoSwitchScreenListener onVideoSwitchScreenListener;
    /**
     * 调试模式
     */
    private boolean debug;

    public VideoView(@NonNull Context context) {
        this(context, null);
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(Color.BLACK);
        durationTimer = new DurationTimer();
        videoHelper = new VideoHelper();
        container = (ViewGroup) getParent();
        initMediaPlayer();
        initVideoSurface(context);
        initControlViews();
    }

    /**
     * 是否调试模式
     *
     * @return
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * 设置是否调试
     *
     * @param debug
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
        if (videoHelper != null) {
            videoHelper.setDebug(debug);
        }
        if (textureView != null) {
            textureView.setDebug(debug);
        }
    }

    /**
     * 设置是否是直播源
     *
     * @param liveSource
     */
    public void setLiveSource(boolean liveSource) {
        this.liveSource = liveSource;
        if (isLiveSource()) {
            videoHolder.setSeekBarEnabled(false);
            videoHolder.setSeekBarThumb(null);
        } else {
            videoHolder.setSeekBarEnabled(true);
            videoHolder.setSeekBarThumb(ContextCompat.getDrawable(getContext(), R.drawable.ijk_seek_dot));
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
     * @param onVideoSwitchScreenListener
     */
    public void setOnVideoSwitchScreenListener(OnVideoSwitchScreenListener onVideoSwitchScreenListener) {
        this.onVideoSwitchScreenListener = onVideoSwitchScreenListener;
    }

    /**
     * 播放操作助手
     *
     * @return
     */
    public VideoHelper getVideoHelper() {
        return videoHelper;
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
        mediaPlayer.setOnBufferingUpdateListener(this);
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
        if (textureView != null && getChildAt(0) instanceof android.view.TextureView) {
            removeView(textureView);
        }
        textureView = new VideoTextureView(context);
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
        videoHolder = new VideoHolder(this, controlView);
        videoHolder.findViews();
        //中间控件隐藏
        videoHolder.setCenterImageVisibility(false);
        videoHolder.setVoiceBrightnessGroupVisibility(false);
        videoHolder.setSpeedTextViewVisibility(false);
        videoHolder.setCoverVisibility(false);
        //底部播放按钮监听
        videoHolder.getPlayView().setOnClickListener(this);
        //底部屏幕转换按钮监听
        videoHolder.getScreenSwitchView().setOnClickListener(this);
        //中间播放按钮监听
        videoHolder.getCenterImageView().setOnClickListener(this);
        //进度条监听
        videoHolder.getSeekBar().setOnSeekBarChangeListener(this);
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
            if (onVideoSwitchScreenListener != null) {
                onVideoSwitchScreenListener.onVideoSwitchScreen(orientation);
            } else {
                videoHelper.switchScreen(getContext(), this, orientation);
                setDisplay(IJK.config().display(), getMeasuredHeight(), getMeasuredWidth());
            }
        }
    }

    /**
     * 设置显示类型
     *
     * @param display 显示类型
     */
    public void setDisplay(Display display) {
        if (textureView != null && mediaPlayer != null) {
            textureView.setDisplay(display, getWidth(), getHeight(), iMediaPlayer.getVideoWidth(), iMediaPlayer.getVideoHeight());
        }
    }

    /**
     * 设置显示类型
     *
     * @param display       显示类型
     * @param displayWidth  显示宽度
     * @param displayHeight 显示高度
     * @param videoWidth    视频宽度
     * @param videoHeight   视频高度
     */
    public void setDisplay(Display display, int displayWidth, int displayHeight, int videoWidth, int videoHeight) {
        if (textureView != null) {
            textureView.setDisplay(display, displayWidth, displayHeight, videoWidth, videoHeight);
        }
    }

    /**
     * 设置视频显示大小
     *
     * @param display       显示类型
     * @param displayWidth  显示宽度
     * @param displayHeight 显示高度
     */
    public void setDisplay(Display display, int displayWidth, int displayHeight) {
        if (textureView != null && mediaPlayer != null) {
            textureView.setDisplay(display, displayWidth, displayHeight, iMediaPlayer.getVideoWidth(), iMediaPlayer.getVideoHeight());
        }
    }

    /**
     * 设置视频显示高度和宽度
     *
     * @param width
     * @param height
     */
    public void setDisplayParams(int width, int height) {
        if (textureView != null) {
            textureView.setDisplayParams(width, height);
        }
    }

    /**
     * 设置显示比例
     *
     * @param display 显示类型
     * @param width   宽度值或宽比值，例如1920或16
     * @param height  高度值或高比值，例如1080或9
     */
    public void setRatio(Display display, int width, int height) {
        IJK ijk = IJK.config();
        ijk.display(display);
        ijk.ratio(width, height);
        setDisplay(display);
    }

    /**
     * 获取显示比例
     *
     * @return
     */
    public Ratio getDisplayRatio() {
        return IJK.config().ratio();
    }

    /**
     * 获取视频显示大小，注意：只有视频加载完成才能正常获取该视频的尺寸
     *
     * @return
     */
    public Size getVideoDisplaySize() {
        return new Size(textureView.getMeasuredWidth(), textureView.getMeasuredHeight());
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
        videoHolder.setVoiceBrightnessGroupVisibility(false);
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
        if (mediaPlayer.isPlaying()) {
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
        videoHolder.setSeekBarProgress(0);
        showVideoTime(0, videoHolder.getCurrentView());
        showVideoTime(0, videoHolder.getDurationView());
        setDataSource(path);
        start();
    }

    /**
     * 视频暂停
     */
    public void pause() {
        videoHolder.setSpeedTextViewVisibility(false);
        videoHolder.setCenterImageVisibility(true);
        videoHolder.setCenterImageResource(R.mipmap.ic_ijk_pause_center);
        videoHolder.setPlayImageResource(R.mipmap.ic_ijk_pause_control);
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        durationTimer.stop();
    }

    /**
     * 视频播放完毕
     */
    protected void onCompletion() {
        videoHolder.setCenterImageVisibility(true);
        videoHolder.setCenterImageResource(R.mipmap.ic_ijk_replay);
        videoHolder.setPlayImageResource(R.mipmap.ic_ijk_pause_control);
        durationTimer.stop();
    }

    /**
     * 视频恢复播放
     */
    public void resume() {
        videoHolder.setCenterImageVisibility(false);
        videoHolder.setPlayImageResource(R.mipmap.ic_ijk_play_control);
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        durationTimer.start();
    }

    /**
     * 异步准备
     */
    public void prepareAsync() {
        isPlayEnd = false;
        isPrepared = false;
        videoHolder.setCenterImageVisibility(false);
        mediaPlayer.prepareAsync();
    }

    /**
     * 开始播放
     */
    public void start() {
        showLoading();
        isPlayEnd = false;
        isPrepared = false;
        videoHolder.setCenterImageVisibility(false);
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
        durationTimer.stop();
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
    public VideoHolder getVideoHolder() {
        return videoHolder;
    }

    /**
     * 设置视频监听
     *
     * @param onIJKVideoListener
     */
    public void setOnIJKVideoListener(OnVideoListener onIJKVideoListener) {
        this.onIJKVideoListener = onIJKVideoListener;
    }

    /**
     * 获取视频监听
     *
     * @return
     */
    public OnVideoListener getOnIJKVideoListener() {
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
    public VideoTextureView getTextureView() {
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
        debug("surface texture available " + width + "," + height);
        surface = new Surface(surfaceTexture);
        videoHolder.setCoverVisibility(false);
        setSurface(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        debug("surface texture size changed width:" + width + ",height" + height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        debug("surface texture destroyed");
        surface.release();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        debug("surface texture updated");
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            videoHolder.setCoverVisibility(false);
            videoHolder.setSpeedTextViewVisibility(false);
        }
    }

    //****************************************[TextureView - SurfaceTextureListener]**********************************************
    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        this.iMediaPlayer = iMediaPlayer;
        isPrepared = true;
        if (iMediaPlayer.getDuration() == 0) {
            setLiveSource(true);
        }
        if (isLiveSource()) {
            liveStartTime = System.currentTimeMillis();
        }
        if (surface != null) {
            iMediaPlayer.setSurface(surface);
        }
        debug("prepared");
        if (onIJKVideoListener != null) {
            onIJKVideoListener.onVideoPrepared(iMediaPlayer);
        }
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int width, int height, int sarNum, int sarDen) {
        this.iMediaPlayer = iMediaPlayer;
        setDisplay(IJK.config().display(), getMeasuredWidth(), getMeasuredHeight(), width, height);
        debug("video size changed width:" + width + ",height:" + height + ",sarNum：" + sarNum + ",sarDen:" + sarDen);
        if (onIJKVideoListener != null) {
            onIJKVideoListener.onVideoSizeChanged(iMediaPlayer, width, height, sarNum, sarDen);
        }
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int args) {
        this.iMediaPlayer = iMediaPlayer;
        debug("info what:" + what + ",args:" + args);
        if (what == IjkMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            videoHolder.setPlayImageResource(R.mipmap.ic_ijk_play_control);
            if (onIJKVideoListener != null) {
                onIJKVideoListener.onVideoRenderingStart(iMediaPlayer, args);
            }
            //消失封面
            videoHolder.setCoverVisibility(false);
            //消失Loading
            dismissLoading();
            //开始计时
            durationTimer.start();
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
        if (what == IjkMediaPlayer.MEDIA_INFO_MEDIA_ACCURATE_SEEK_COMPLETE) {
            showLoading();
        }
        if (what == IjkMediaPlayer.MEDIA_INFO_AUDIO_SEEK_RENDERING_START) {

        }
        if (what == IjkMediaPlayer.MEDIA_INFO_VIDEO_SEEK_RENDERING_START) {
            dismissLoading();
        }
        if (what == IjkMediaPlayer.MEDIA_INFO_BUFFERING_START) {
            showLoading();
            if (onIJKVideoListener != null) {
                onIJKVideoListener.onVideoBufferingStart(iMediaPlayer, args);
            }
            durationTimer.stop();
        }
        if (what == IjkMediaPlayer.MEDIA_INFO_BUFFERING_END) {
            //消失封面
            videoHolder.setCoverVisibility(false);
            //消失加载
            dismissLoading();
            if (onIJKVideoListener != null) {
                onIJKVideoListener.onVideoBufferingEnd(iMediaPlayer, args);
            }
            durationTimer.start();
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
        debug("seek complete");
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
        showVideoTime(iMediaPlayer.getDuration(), videoHolder.getCurrentView());
        debug("completion");
        if (onIJKVideoListener != null) {
            onIJKVideoListener.onVideoCompletion(iMediaPlayer);
        }
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int framework_err, int impl_err) {
        dismissLoading();
        this.iMediaPlayer = iMediaPlayer;
        debug("error framework_err:\" + framework_err + \",impl_err:\" + impl_err");
        if (onIJKVideoListener != null) {
            onIJKVideoListener.onVideoError(iMediaPlayer, framework_err, impl_err);
        }
        return false;
    }

    private class DurationTimer extends Handler {

        private int WHAT_GET_DURATION = 0;


        public void start() {
            sendEmptyMessage(WHAT_GET_DURATION);
        }

        public void stop() {
            removeMessages(WHAT_GET_DURATION);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_GET_DURATION && iMediaPlayer != null) {
                long duration = isLiveSource() ? 0 : iMediaPlayer.getDuration();
                liveStartTime = liveStartTime == 0 ? System.currentTimeMillis() : liveStartTime;
                long current = isLiveSource() ? System.currentTimeMillis() - liveStartTime : iMediaPlayer.getCurrentPosition();
                debug("video progress duration=" + duration + ",current=" + current + ",isLiveSource=" + isLiveSource());
                showSpeed();
                onVideoProgress(iMediaPlayer, duration, current);
                if (onIJKVideoListener != null) {
                    onIJKVideoListener.onVideoProgress(iMediaPlayer, iMediaPlayer.getDuration(), iMediaPlayer.getCurrentPosition());
                }
                sendEmptyMessageDelayed(WHAT_GET_DURATION, 1000);
            }
        }
    }

    /**
     * 视频进度
     *
     * @param iMediaPlayer 视频对象
     * @param duration     时长
     * @param current      当前进度
     */
    protected void onVideoProgress(IMediaPlayer iMediaPlayer, long duration, long current) {
        videoHolder.setSeekBarMax((int) duration);
        videoHolder.setSeekBarProgress((int) current);
        showVideoTime(current, videoHolder.getCurrentView());
        showVideoTime(duration, videoHolder.getDurationView());
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
        videoHolder.setSpeedTextViewVisibility(true);
    }

    /**
     * 消失Loading
     */
    public void dismissLoading() {
        videoHolder.setSpeedTextViewVisibility(false);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    //*******************************[onTouchEvent]*********************************
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return videoHelper.onTouchEvent(getContext(), isLiveSource(), event, this, mediaPlayer == null ? 0 : mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration(), this);
    }

    @Override
    public void onVideoChangeBrightness(float value, float percent) {
        videoHelper.changeBrightness(getContext(), value);
        showCircleProgressPercent(percent, ProgressType.BRIGHTNESS);
    }

    @Override
    public void onVideoChangeVoice(int value, float percent) {
        videoHelper.changeVoice(getContext(), value);
        showCircleProgressPercent(percent, ProgressType.VOICE);
    }

    @Override
    public void onVideoStartChangeProgress(long value, float percent) {
        long current = mediaPlayer.getCurrentPosition();
        boolean isForward = value - current > 0;
        videoHolder.setSeekBarProgress((int) value);
        showCircleProgressPercent(percent, isForward ? ProgressType.FORWARD : ProgressType.BACKWARD);
    }

    @Override
    public void onVideoStopChangeProgress(long value, float percent) {
        mediaPlayer.seekTo(value);
        videoHolder.setSeekBarProgress((int) value);
    }

    @Override
    public void onVideoControlViewShow(MotionEvent event) {

    }

    @Override
    public void onVideoControlViewHide(MotionEvent event) {
        videoHolder.setVoiceBrightnessGroupVisibility(false);
    }

    @Override
    public void onVideoActionDown(MotionEvent event) {
        if (onClickListener != null) {
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

    private long lastRxBytes = 0;
    private long lastRxTime = 0;

    /**
     * 显示网速
     */
    private void showSpeed() {
        if (videoHolder.isSpeedTextViewVisible()) {
            long totalRxBytes = TrafficStats.getTotalRxBytes();
            if (lastRxBytes == 0) {
                lastRxBytes = totalRxBytes;
            }
            long rxBytes = totalRxBytes - lastRxBytes;
            long time = System.currentTimeMillis();
            long rxTime = time - lastRxTime;
            long speed = rxBytes * 1000 / rxTime;
            String speedText;
            if (speed >= 1073741824) {
                speedText = speed / 1073741824 + "Gbps";
            } else if (speed >= 1048576) {
                speedText = speed / 1048576 + "Mbps";
            } else {
                speedText = speed / 1024 + "Kbps";
            }
            videoHolder.setSpeedText(speedText);
            lastRxBytes = totalRxBytes;
            lastRxTime = time;
        }
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, int percent) {

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
        percent = percent > 1 || percent < 0 ? 0 : percent;
        int progress = (int) (percent * 100);
        progress = progress > 100 ? 100 : progress;
        videoHolder.setVoiceBrightnessGroupVisibility(true);
        String progressText = "";
        //音量
        if (type == ProgressType.VOICE) {
            videoHolder.setProgressTextVisibility(false);
            videoHolder.setVideoVoiceVisibility(true);
            videoHolder.setVideoVoiceValue(percent);
            videoHolder.setVideoBrightnessVisibility(false);
        }
        //亮度
        else if (type == ProgressType.BRIGHTNESS) {
            videoHolder.setProgressTextVisibility(false);
            videoHolder.setVideoBrightnessVisibility(true);
            videoHolder.setVideoBrightnessValue(percent);
            videoHolder.setVideoVoiceVisibility(false);
        }
        //快进 快退
        else if (type == ProgressType.FORWARD || type == ProgressType.BACKWARD) {
            videoHolder.setVideoBrightnessVisibility(false);
            videoHolder.setVideoVoiceVisibility(false);
            videoHolder.setProgressTextVisibility(true);
        }
        progressText = progress + "%";
        videoHolder.setCircleProgressText(progressText);
        videoHolder.setCircleProgressMax(100);
        videoHolder.setCircleProgress(progress);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (durationTimer != null) {
            durationTimer.stop();
        }
    }

    /**
     * 调试打印
     *
     * @param content
     */
    private void debug(String content) {
        if (isDebug()) {
            Log.d(TAG, content);
        }
    }

}
