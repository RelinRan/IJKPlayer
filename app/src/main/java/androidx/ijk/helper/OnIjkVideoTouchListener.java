package androidx.ijk.helper;


import android.view.MotionEvent;

/**
 * Author: Relin
 * Describe:ijk视频事件监听回调
 * Date:2020/5/20 14:22
 */
public interface OnIjkVideoTouchListener {
    /**
     * 改变亮度
     *
     * @param value   亮度值
     * @param percent 亮度百分比[0-1]
     */
    void onVideoChangeBrightness(float value, float percent);

    /**
     * 改变音量
     *
     * @param value   音量值
     * @param percent 音量百分比[0-1]
     */
    void onVideoChangeVoice(int value, float percent);

    /**
     * 改变进度开始
     *
     * @param value   进度值
     * @param percent 进度百分比
     */
    void onVideoStartChangeProgress(long value, float percent);

    /**
     * 改变进度结束
     *
     * @param value   进度值
     * @param percent 进度百分比[0-1]
     */
    void onVideoStopChangeProgress(long value, float percent);

    /**
     * 显示控制器视图
     *
     * @param event 事件对象
     */
    void onVideoControlViewShow(MotionEvent event);

    /**
     * 隐藏控制器视图
     *
     * @param event 事件对象
     */
    void onVideoControlViewHide(MotionEvent event);

    /**
     * 按下
     * @param event
     */
    void onVideoActionDown(MotionEvent event);

    /**
     * 移动
     * @param event
     */
    void onVideoActionMove(MotionEvent event);

    /**
     * 释放
     * @param event
     */
    void onVideoActionUp(MotionEvent event);


}