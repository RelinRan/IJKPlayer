package androidx.ijk.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.ijk.R;

/**
 * 视频亮度图标
 */
public class VideoBrightness extends View {

    private Paint paint;
    /**
     * 填充颜色
     */
    private int solid = Color.CYAN;
    /**
     * 中心x
     */
    private float cx;
    /**
     * 中心y
     */
    private float cy;
    /**
     * 半径
     */
    private float radius = 0;
    /**
     * 线条长度
     */
    private float lineLength = 0;
    private float lineLengthClone;
    /**
     * 线条和中心圆间距
     */
    private int gap = 20;
    /**
     * 线条宽度
     */
    private float lineWidth = 5;

    public VideoBrightness(Context context) {
        this(context, null);
    }

    public VideoBrightness(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoBrightness(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VideoBrightness);
            solid = array.getColor(R.styleable.VideoBrightness_ijkSolid, solid);
            radius = array.getDimension(R.styleable.VideoBrightness_ijkRadius, radius);
            lineLength = array.getDimension(R.styleable.VideoBrightness_ijkLineLength, lineLength);
            gap = array.getDimensionPixelSize(R.styleable.VideoBrightness_ijkGap, gap);
            lineWidth = array.getDimension(R.styleable.VideoBrightness_ijkLineWidth, lineWidth);
            array.recycle();
        }
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        cx = getMeasuredWidth() / 2;
        cy = getMeasuredHeight() / 2;
        int slide = Math.min(getMeasuredHeight(), getMeasuredWidth());
        radius = radius == 0 ? slide / 6 : radius;
        lineLength = lineLength == 0 ? (radius / 2 - gap) : lineLength;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(solid);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cx, cy, radius, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        float length = radius + gap;
        for (int angle = 0; angle < 360; angle += 45) {
            double radians = Math.toRadians(angle);
            float startX = cx + (float) (length * Math.cos(radians));
            float startY = cy + (float) (length * Math.sin(radians));
            float endX = cx + (float) ((length + lineLength) * Math.cos(radians));
            float endY = cy + (float) ((length + lineLength) * Math.sin(radians));
            canvas.drawLine(startX, startY, endX, endY, paint);
        }
    }

    /**
     * 设置填充颜色
     *
     * @param solid
     */
    public void setSolid(int solid) {
        this.solid = solid;
        invalidate();
    }

    /**
     * 设置亮度百分比值
     *
     * @param percent
     */
    public void setValue(float percent) {
        setRotation(percent * 360);//旋转
        //线条长度
        lineLengthClone = lineLengthClone == 0 ? lineLength : lineLengthClone;
        lineLength = lineLengthClone * percent;
        invalidate();
    }

    /**
     * 设置半径大小
     *
     * @param radius
     */
    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();
    }

    /**
     * 设置线段长度
     *
     * @param lineLength
     */
    public void setLineLength(float lineLength) {
        this.lineLength = lineLength;
        invalidate();
    }

    /**
     * 设置线段和圆圈间距
     *
     * @param gap
     */
    public void setGap(int gap) {
        this.gap = gap;
        invalidate();
    }

    /**
     * 设置线段宽度
     *
     * @param lineWidth
     */
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
        invalidate();
    }

}
