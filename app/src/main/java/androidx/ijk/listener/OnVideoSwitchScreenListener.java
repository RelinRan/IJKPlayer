package androidx.ijk.listener;

import androidx.ijk.enums.Orientation;

public interface OnVideoSwitchScreenListener {

    /**
     * IJK屏幕切换
     * @param orientation
     */
    void onVideoSwitchScreen(Orientation orientation);

}
