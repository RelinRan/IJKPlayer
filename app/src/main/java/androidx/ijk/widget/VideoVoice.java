package androidx.ijk.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.ijk.R;

/**
 * 视频声音图标
 */
public class VideoVoice extends View {

    /**
     * 视图宽度、高度
     */
    private int width, height;
    /**
     * 画笔
     */
    private Paint paint;
    /**
     * 喇叭颜色
     */
    private int solid = Color.CYAN;
    /**
     * 喇叭底座宽度
     */
    private int cornerWidth = 0;
    /**
     * 喇叭底座高度
     */
    private int cornerHeight = 0;
    /**
     * 线条宽度
     */
    private float strokeWidth = 15;
    /**
     * 音量百分比
     */
    private float percent = 1.0f;
    /**
     * 音量线条角度
     */
    private int angle = 45;
    /**
     * 音量线条个数
     */
    private int count = 4;
    /**
     * 左边中心X
     */
    private int leftCenterX = 0;

    public VideoVoice(Context context) {
        this(context, null, 0);
    }

    public VideoVoice(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoVoice(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public VideoVoice(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VideoVoice);
            solid = array.getColor(R.styleable.VideoVoice_ijkSolid, solid);
            cornerWidth = array.getDimensionPixelSize(R.styleable.VideoVoice_ijkCornerWidth, cornerWidth);
            cornerHeight = array.getDimensionPixelSize(R.styleable.VideoVoice_ijkCornerHeight, cornerHeight);
            strokeWidth = array.getDimension(R.styleable.VideoVoice_ijkStrokeWidth, strokeWidth);
            angle = array.getInt(R.styleable.VideoVoice_ijkAngle, angle);
            count = array.getInt(R.styleable.VideoVoice_ijkCount, count);
            leftCenterX = array.getDimensionPixelOffset(R.styleable.VideoVoice_ijkLeftCenterX, leftCenterX);
            array.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        leftCenterX = leftCenterX == 0 ? width / 2 : leftCenterX;
        cornerWidth = cornerWidth == 0 ? leftCenterX / 2 : cornerWidth;
        cornerHeight = cornerHeight == 0 ? height / 5 : cornerHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLeft(canvas);
        drawRight(canvas);
    }

    /**
     * 绘制左边
     *
     * @param canvas
     */
    private void drawLeft(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(solid);
        Path path = new Path();
        path.moveTo(leftCenterX, 0);
        path.lineTo(leftCenterX, height);
        path.lineTo(leftCenterX - cornerWidth, height - cornerHeight);
        path.lineTo(0, height - cornerHeight);
        path.lineTo(0, cornerHeight);
        path.lineTo(leftCenterX - cornerWidth, cornerHeight);
        path.close();
        canvas.drawPath(path, paint);
    }

    /**
     * 绘制右边
     *
     * @param canvas
     */
    private void drawRight(Canvas canvas) {
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        int centerX = leftCenterX;
        int centerY = height / 2;
        int rightAngle = angle / count;
        float rectRadius = (width - leftCenterX - strokeWidth);
        for (int i = 0; i < count; i++) {
            paint.setColor(getArcColor(i));
            int radius = (int) (rectRadius * (i + 1) / count);
            RectF rectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
            canvas.drawArc(rectF, 0 - rightAngle * (i + 1), rightAngle * (i + 1) * 2f, false, paint);
        }
    }

    /**
     * 获取扇形颜色
     *
     * @param index
     * @return
     */
    private int getArcColor(int index) {
        float value = (index + 1.0f) / count;
        return percent >= value ? solid : Color.WHITE;
    }

    /**
     * 设置颜色
     *
     * @param solid
     */
    public void setSolid(int solid) {
        this.solid = solid;
        invalidate();
    }

    /**
     * 设置音量百分比
     *
     * @param percent
     */
    public void setValue(float percent) {
        this.percent = percent;
        invalidate();
    }

    /**
     * 设置喇叭底座宽度
     *
     * @param cornerWidth
     */
    public void setCornerWidth(int cornerWidth) {
        this.cornerWidth = cornerWidth;
        invalidate();
    }

    /**
     * 喇叭底座高度
     *
     * @param cornerHeight
     */
    public void setCornerHeight(int cornerHeight) {
        this.cornerHeight = cornerHeight;
        invalidate();
    }

    /**
     * 设置线条宽度
     *
     * @param strokeWidth
     */
    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        invalidate();
    }

    /**
     * 设置音量线条角度
     *
     * @param angle
     */
    public void setAngle(int angle) {
        this.angle = angle;
        invalidate();
    }

    /**
     * 设置音量线条个数
     *
     * @param count
     */
    public void setCount(int count) {
        this.count = count;
        invalidate();
    }

}