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
import androidx.ijk.model.Ratio;

/**
 * 视频显示图层
 */
public class VideoTextureView extends TextureView {

    private String TAG = VideoTextureView.class.getSimpleName();
    private int displayWidth = -1;
    private int displayHeight = -1;
    private int videoWidth = -1;
    private int videoHeight = -1;
    private boolean debug;

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
     * 设置是否调试模式
     *
     * @param debug
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
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
     * 设置显示类型
     *
     * @param display 显示类型
     */
    public void setDisplay(Display display) {
        IJK.config().display(display);
        if (displayWidth > 0 && displayHeight > 0 && videoWidth > 0 && videoHeight > 0) {
            setDisplay(display, displayWidth, displayHeight, videoWidth, videoHeight);
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
            setDisplaySize(display, displayWidth, displayHeight, videoWidth, videoHeight);
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
    private void setDisplaySize(Display display, int displayWidth, int displayHeight, int videoWidth, int videoHeight) {
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        float ratio = videoWidth * 1.0f / videoHeight;
        ViewGroup.LayoutParams params = getLayoutParams();
        //自动适配
        if (display == Display.AUTO) {
            if (displayWidth > displayHeight) {
                params.width = (int) (displayHeight * ratio);
                params.height = displayHeight;
            } else {
                params.width = displayWidth;
                params.height = (int) (displayWidth / ratio);
            }
        }
        //宽度适配
        else if (display == Display.MATCH_WIDTH) {
            params.width = displayWidth;
            params.height = (int) (displayWidth / ratio);
        }
        //高度适配
        else if (display == Display.MATCH_HEIGHT) {
            params.width = (int) (displayHeight * ratio);
            params.height = displayHeight;
        }
        //原尺寸
        else if (display == Display.ORIGINAL) {
            params.width = videoWidth;
            params.height = videoHeight;
        }
        //宽度比例显示
        else if (display == Display.RATIO_WIDTH) {
            Ratio videoRatio = IJK.config().ratio();
            float vRatio = videoRatio.getRatio();
            params.width = (int) (videoHeight * vRatio);
            params.height = videoHeight;
        }
        //高度比例显示
        else if (display == Display.RATIO_HEIGHT) {
            Ratio videoRatio = IJK.config().ratio();
            float vRatio = videoRatio.getRatio();
            params.width = videoWidth;
            params.height = (int) (videoWidth / vRatio);
        }
        //比例显示
        else if (display == Display.MATCH_PARENT) {
            params.width = displayWidth;
            params.height = displayHeight;
        }
        setLayoutParams(params);
        debug(display + " display:[" + displayWidth + "," + displayHeight + "],video:[" + videoWidth + "," + videoHeight + ",ratio:" + ratio + "],params:[" + params.width + "," + params.height + "]");
    }

    /**
     * 设置显示参数
     *
     * @param width  宽度
     * @param height 高度
     */
    public void setDisplayParams(int width, int height) {
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = width;
        params.height = height;
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
