package androidx.ijk.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.ijk.R;


/**
 * Author: Relin
 * Describe:控制器ViewHolder
 * Date:2020/5/12 18:24
 */
public class VideoHolder {

    /**
     * 父视图
     */
    private View parent;
    /**
     * 控制器View
     */
    private View controlView;
    /**
     * 播放/暂停
     */
    private ImageView iv_ijk_play;
    /**
     * 底部控制栏
     */
    private LinearLayout group_bottom;
    /**
     * 当前时间
     */
    private TextView tv_ijk_current;
    /**
     * 进度
     */
    private SeekBar seek_ijk_bar;
    /**
     * 时长
     */
    private TextView tv_ijk_duration;
    /**
     * 屏幕转换
     */
    private ImageView iv_ijk_screen;
    /**
     * 屏幕中间控件
     */
    private ImageView iv_ijk_center;
    /**
     * 音量和亮度
     */
    private FrameLayout ijk_voice_brightness;
    /**
     * 视频音量图标
     */
    private VideoVoice ijk_video_voice;
    /**
     * 视频亮度图标
     */
    private VideoBrightness ijk_video_brightness;
    /**
     * 屏幕中间进度条
     */
    private VideoCircleProgress ijk_circle_progress;
    /**
     * ImageView Loading
     */
    private TextView tv_ijk_speed;
    /**
     * 封面
     */
    private ImageView iv_ijk_cover;


    public VideoHolder(View parent, View controlView) {
        this.parent = parent;
        this.controlView = controlView;
    }

    /**
     * 找到所有控件
     */
    public void findViews() {
        group_bottom = controlView.findViewById(R.id.group_bottom);
        iv_ijk_play = controlView.findViewById(R.id.iv_ijk_play);
        tv_ijk_current = controlView.findViewById(R.id.tv_ijk_current);
        seek_ijk_bar = controlView.findViewById(R.id.seek_ijk_bar);
        tv_ijk_duration = controlView.findViewById(R.id.tv_ijk_duration);
        iv_ijk_screen = controlView.findViewById(R.id.iv_ijk_screen);
        iv_ijk_center = controlView.findViewById(R.id.iv_ijk_center);
        ijk_voice_brightness = controlView.findViewById(R.id.ijk_voice_brightness);
        ijk_circle_progress = controlView.findViewById(R.id.ijk_circle_progress);
        ijk_video_voice = controlView.findViewById(R.id.ijk_video_voice);
        ijk_video_brightness = controlView.findViewById(R.id.ijk_video_brightness);
        tv_ijk_speed = controlView.findViewById(R.id.tv_ijk_speed);
        iv_ijk_cover = controlView.findViewById(R.id.iv_ijk_cover);
    }

    /**
     * 获取父级视图
     *
     * @return
     */
    public View getParentView() {
        return parent;
    }

    /**
     * 获取控制View
     *
     * @return
     */
    public View getControlView() {
        return controlView;
    }

    /**
     * 获取底部控制栏
     *
     * @return
     */
    public LinearLayout getControlGroup() {
        return group_bottom;
    }

    /**
     * 设置控制组是否可见
     *
     * @param show
     */
    public void setControlGroupVisibility(boolean show) {
        getControlGroup().setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 获取暂停、播放View
     *
     * @return
     */
    public ImageView getPlayView() {
        return iv_ijk_play;
    }

    /**
     * 设置播放按钮图标资源
     *
     * @param resId
     */
    public void setPlayImageResource(int resId) {
        getPlayView().setImageResource(resId);
    }

    /**
     * 获取当前时间View
     *
     * @return
     */
    public TextView getCurrentView() {
        return tv_ijk_current;
    }

    /**
     * 获取拖动bar
     *
     * @return
     */
    public SeekBar getSeekBar() {
        return seek_ijk_bar;
    }

    /**
     * 设置拖动bar是否可用
     *
     * @param enabled
     */
    public void setSeekBarEnabled(boolean enabled) {
        getSeekBar().setEnabled(enabled);
    }

    /**
     * 设置拖动bar按钮图标
     *
     * @param context 上下文
     * @param resId   资源
     */
    public void setSeekBarThumb(Context context, int resId) {
        getSeekBar().setThumb(ContextCompat.getDrawable(context, resId));
    }

    /**
     * 设置拖动bar图标
     *
     * @param drawable
     */
    public void setSeekBarThumb(Drawable drawable) {
        getSeekBar().setThumb(drawable);
    }

    /**
     * 设置拖动bar最大值
     *
     * @param max 最大值
     */
    public void setSeekBarMax(int max) {
        getSeekBar().setMax(max);
    }

    /**
     * 设置拖动bar进度
     *
     * @param progress 进度值
     */
    public void setSeekBarProgress(int progress) {
        getSeekBar().setProgress(progress);
    }

    /**
     * 获取视频时长View
     *
     * @return
     */
    public TextView getDurationView() {
        return tv_ijk_duration;
    }

    /**
     * 获取屏幕转换视图
     *
     * @return
     */
    public ImageView getScreenSwitchView() {
        return iv_ijk_screen;
    }

    /**
     * 获取屏幕中间图片
     *
     * @return
     */
    public ImageView getCenterImageView() {
        return iv_ijk_center;
    }

    /**
     * 设置中间图是否可见
     *
     * @param show
     */
    public void setCenterImageVisibility(boolean show) {
        getCenterImageView().setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置中间图资源id
     *
     * @param resId
     */
    public void setCenterImageResource(int resId) {
        getCenterImageView().setImageResource(resId);
    }

    /**
     * 获取音量、亮度组View
     *
     * @return
     */
    public FrameLayout getVoiceBrightnessGroup() {
        return ijk_voice_brightness;
    }

    /**
     * 设置音量、亮度组View是否可见
     *
     * @param show
     */
    public void setVoiceBrightnessGroupVisibility(boolean show) {
        getVoiceBrightnessGroup().setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 获取圆圈进度
     *
     * @return
     */
    public VideoCircleProgress getVideoCircleProgressView() {
        return ijk_circle_progress;
    }

    /**
     * 设置进度文字是否可见
     *
     * @param show
     */
    public void setProgressTextVisibility(boolean show) {
        getVideoCircleProgressView().setProgressTextVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置进度文字
     *
     * @param value
     */
    public void setCircleProgressText(String value) {
        getVideoCircleProgressView().setProgressText(value);
    }

    /**
     * 设置圆圈进度最大值
     *
     * @param max
     */
    public void setCircleProgressMax(int max) {
        getVideoCircleProgressView().setMax(max);
    }

    /**
     * 设置圆圈进度值
     *
     * @param progress
     */
    public void setCircleProgress(int progress) {
        getVideoCircleProgressView().setProgress(progress);
    }

    /**
     * 获取视频音量图标
     *
     * @return
     */
    public VideoVoice getVideoVoiceView() {
        return ijk_video_voice;
    }

    /**
     * 设置视频音量是否可见
     *
     * @param show
     */
    public void setVideoVoiceVisibility(boolean show) {
        getVideoVoiceView().setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置音量的百分比值
     *
     * @param percent
     */
    public void setVideoVoiceValue(float percent) {
        getVideoVoiceView().setValue(percent);
    }

    /**
     * 获取视频亮度图标
     *
     * @return
     */
    public VideoBrightness getVideoBrightnessView() {
        return ijk_video_brightness;
    }

    /**
     * 设置视频亮度是否可见
     *
     * @param show
     */
    public void setVideoBrightnessVisibility(boolean show) {
        getVideoBrightnessView().setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置视频亮度值
     *
     * @param percent
     */
    public void setVideoBrightnessValue(float percent) {
        getVideoBrightnessView().setValue(percent);
    }

    /**
     * 获取缓冲速度TextView
     *
     * @return
     */
    public TextView getSpeedTextView() {
        return tv_ijk_speed;
    }

    /**
     * 设置网速文字是否可见
     *
     * @param show
     */
    public void setSpeedTextViewVisibility(boolean show) {
        getSpeedTextView().setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 网速文字是否可见
     *
     * @return
     */
    public boolean isSpeedTextViewVisible() {
        return getSpeedTextView() != null && getSpeedTextView().getVisibility() == View.VISIBLE;
    }

    /**
     * 设置网速文字
     *
     * @param value
     */
    public void setSpeedText(String value) {
        getSpeedTextView().setText(value);
    }

    /**
     * 封面控件
     *
     * @return
     */
    public ImageView getCoverImageView() {
        return iv_ijk_cover;
    }

    /**
     * 设置封面是否显示
     *
     * @param show
     */
    public void setCoverVisibility(boolean show) {
        getCoverImageView().setVisibility(show ? View.VISIBLE : View.GONE);
    }

}
