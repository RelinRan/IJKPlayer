package androidx.ijk.view;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.ijk.R;


/**
 * Author: Relin
 * Describe:控制器ViewHolder
 * Date:2020/5/12 18:24
 */
public class IJKControlViewHolder {

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
     * 屏幕中间进度条
     */
    private IJKCircleProgressView ijk_center_progress;
    /**
     * loading
     */
    private FrameLayout fl_ijk_loading;
    /**
     * ImageView Loading
     */
    private TextView tv_ijk_loading;
    /**
     * 封面
     */
    private ImageView iv_ijk_cover;


    public IJKControlViewHolder(View parent, View controlView) {
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
        ijk_center_progress = controlView.findViewById(R.id.ijk_center_progress);
        fl_ijk_loading = controlView.findViewById(R.id.fl_ijk_loading);
        tv_ijk_loading = controlView.findViewById(R.id.tv_ijk_loading);
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
     * @return
     */
    public LinearLayout getGroupBottom() {
        return group_bottom;
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
     * 获取当前时间View
     *
     * @return
     */
    public TextView getCurrentView() {
        return tv_ijk_current;
    }

    /**
     * 获取进度条View
     *
     * @return
     */
    public SeekBar getSeekBar() {
        return seek_ijk_bar;
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
     * 获取音量、亮度控件
     *
     * @return
     */
    public IJKCircleProgressView getVoiceLightProgressView() {
        return ijk_center_progress;
    }

    /**
     * Loading视频
     *
     * @return
     */
    public FrameLayout getLoadingView() {
        return fl_ijk_loading;
    }

    /**
     * Loading TextView
     * @return
     */
    public TextView getLoadingTextView(){
        return tv_ijk_loading;
    }

    /**
     * 封面控件
     * @return
     */
    public ImageView getCoverImageView(){
        return iv_ijk_cover;
    }



}
