package androidx.ijk.listener;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Author: Relin
 * Describe:ijk 视频监听
 * Date:2020/5/11 17:24
 */
public interface OnIJKVideoListener {

    /**
     * 视频准备结束
     *
     * @param player
     */
    void onVideoPrepared(IMediaPlayer player);

    /**
     * 视频大小改变
     *
     * @param player 媒体对象
     * @param width  宽度
     * @param height 高度
     * @param sarNum 横向采样
     * @param sarDen 纵向采样<br/>
     *               SAR —— Sample Aspect Ratio 采样横纵比。表示横向的像素点数和纵向的像素点数的比值。<br/>
     *               DAR —— Display Aspect Ratio 显示横纵比。最终显示的图像在长度单位上的横纵比。<br/>
     *               PAR —— Pixel Aspect Ratio 像素横纵比。表示每个像素的宽度与长度的比值。可以认为每个像素不是正方形的。<br/>
     */
    void onVideoSizeChanged(IMediaPlayer player, int width, int height, int sarNum, int sarDen);

    /**
     * 是否支持seek <br/>
     * {@link IMediaPlayer#MEDIA_INFO_NOT_SEEKABLE}
     *
     * @param enable
     */
    void onVideoSeekEnable(boolean enable);

    /**
     * 缓冲开始 <br/>
     * {@link IMediaPlayer#MEDIA_INFO_BUFFERING_START}
     *
     * @param player 媒体对象
     * @param args   参数
     */
    void onVideoBufferingStart(IMediaPlayer player, int args);

    /**
     * 缓冲结束<br/>
     * {@link IMediaPlayer#MEDIA_INFO_BUFFERING_END}
     *
     * @param player 媒体对象
     * @param args   参数
     */
    void onVideoBufferingEnd(IMediaPlayer player, int args);

    /**
     * 渲染开始<br/>
     * {@link IMediaPlayer#MEDIA_INFO_VIDEO_RENDERING_START}
     *
     * @param player 媒体对象
     * @param args   参数
     */
    void onVideoRenderingStart(IMediaPlayer player, int args);

    /**
     * 视频旋转信息<br/>
     * {@link IMediaPlayer#MEDIA_INFO_VIDEO_ROTATION_CHANGED}
     *
     * @param player 媒体对象
     * @param args   参数
     */
    void onVideoRotationChanged(IMediaPlayer player, int args);

    /**
     * 视频过于复杂，无法解码：不能快速解码帧。此时可能只能正常播放音频。<br/>
     * {@link IMediaPlayer#MEDIA_INFO_VIDEO_TRACK_LAGGING}
     *
     * @param player 媒体对象
     * @param args   参数
     */
    void onVideoTrackLagging(IMediaPlayer player, int args);

    /**
     * 当音频和视频数据不正确的交错时<br/>
     * {@link IMediaPlayer#MEDIA_INFO_BAD_INTERLEAVING}
     *
     * @param player 媒体对象
     * @param args   参数
     */
    void onVideoBadInterleaving(IMediaPlayer player, int args);

    /**
     * 视频Seek完毕
     *
     * @param player
     */
    void onVideoSeekComplete(IMediaPlayer player);

    /**
     * 视频播放进度
     *
     * @param player   媒体对象
     * @param duration 视频时长
     * @param current  当前进度
     */
    void onVideoProgress(IMediaPlayer player, long duration, long current);

    /**
     * 视频播放完毕
     *
     * @param player
     */
    void onVideoCompletion(IMediaPlayer player);

    /**
     * 视频错误
     *
     * @param player        媒体对象
     * @param framework_err 底层错误代码
     * @param impl_err      实现层错误代码
     *                      {@link IMediaPlayer#MEDIA_ERROR_SERVER_DIED}视频中断，一般是视频源异常或者不支持的视频类型<br/>
     *                      {@link IMediaPlayer#MEDIA_ERROR_UNSUPPORTED}比特流符合相关编码标准和文件规范，但是media框架不被支持。<br/>
     *                      {@link IMediaPlayer#MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK}播放错误（一般视频播放比较慢或视频本身有问题会引发）<br/>
     *                      {@link IMediaPlayer#MEDIA_ERROR_MALFORMED}比特流不符合相关的编码标准和文件规范<br/>
     *                      {@link IMediaPlayer#MEDIA_ERROR_IO}本地文件或网络相关错误<br/>
     *                      {@link IMediaPlayer#MEDIA_ERROR_TIMED_OUT}播放超时错误<br/>
     *                      {@link IMediaPlayer#MEDIA_ERROR_UNKNOWN}播放错误，未知错误<br/>
     */
    void onVideoError(IMediaPlayer player, int framework_err, int impl_err);

}
