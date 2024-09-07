package androidx.ijk.model;

/**
 * 视频宽高比例
 */
public class Ratio {

    /**
     * 宽度比例值
     */
    private int width;
    /**
     * 高度比例值
     */
    private int height;

    /**
     * 构建视频宽高比例, 例如 4:3 16:9
     *
     * @param width  例如16
     * @param height 例如9
     */
    public Ratio(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getRatio() {
        return width * 1.0f / height;
    }

}
