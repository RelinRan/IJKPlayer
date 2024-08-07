package androidx.ijk.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.ijk.IJK;
import androidx.ijk.enums.Display;

/**
 * 视频显示图层
 */
public class VideoTextureView extends TextureView {

    private String TAG = VideoTextureView.class.getSimpleName();
    private int displayWidth = -1;
    private int displayHeight = -1;
    private int videoWidth = -1;
    private int videoHeight = -1;

    public VideoTextureView(Context context) {
        this(context, null);
    }

    public VideoTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoTextureView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public VideoTextureView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 设置显示类型
     *
     * @param display 显示类型
     */
    public void setDisplay(Display display) {
        IJK.config().display(display);
        if (displayWidth > 0 && displayHeight > 0 && videoWidth > 0 && videoHeight > 0) {
            setVideoSize(displayWidth, displayHeight, videoWidth, videoHeight);
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
        if (displayWidth > 0 && displayHeight > 0 && videoWidth > 0 && videoHeight > 0) {
            IJK.config().display(display);
            setVideoSize(displayWidth, displayHeight, videoWidth, videoHeight);
        }
    }

    /**
     * 设置视频的大小
     *
     * @param displayWidth  显示宽度
     * @param displayHeight 显示高度
     * @param videoWidth    视频宽度
     * @param videoHeight   视频高度
     */
    public void setVideoSize(int displayWidth, int displayHeight, int videoWidth, int videoHeight) {
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        Display displayType = IJK.config().display();
        float ratio = videoWidth * 1.0f / videoHeight;
        Log.i(TAG, "display:[" + displayWidth + "," + displayHeight + "],video:[" + videoWidth + "," + videoHeight + "],ratio:" + ratio);
        ViewGroup.LayoutParams params = getLayoutParams();
        //自动适配
        if (displayType == Display.AUTO) {
            if (displayWidth > displayHeight) {
                params.width = (int) (displayHeight * ratio);
                params.height = displayHeight;
            } else {
                params.width = displayWidth;
                params.height = (int) (displayWidth / ratio);
            }
        }
        //宽度适配
        else if (displayType == Display.WIDTH) {
            params.width = (int) (videoWidth * ratio);
            params.height = (int) (videoWidth * ratio);
        }
        //高度适配
        else if (displayType == Display.HEIGHT) {
            params.width = (int) (videoHeight * ratio);
            params.height = (int) (videoHeight * ratio);
        }
        //原尺寸
        else if (displayType == Display.ORIGINAL) {
            params.width = videoWidth;
            params.height = videoHeight;
        }
    }

}
