package com.a51lupao.www.myapplication;

/**
 * Created by gaoTz on 2017/2/14.
 */

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class DataRing extends View {
    private Context context;
    private static final int DEFAULT_RING_WIDTH = 60;
    private static final int DEFAULT_DATA_TEXT_SIZE = 8;
    /**
     * 圆弧的宽度
     */
    private float ringWidth;
    /**
     * 三个圆环上文字的字体大小
     */
    private float dataTextSize;
    /**
     * 三个圆环对应的RectF集合
     */
    private RectF[] rectFs;
    private int drawColor[];
    private int drawColorBefore[];
    private float startAngle = -90;
    /**
     * 终点对应的角度和起始点对应的角度的夹角
     */
    private float angleLength1 = 0;
    private float angleLength2 = 0;
    private float angleLength3 = 0;


    //圆弧对应的渐变色
    private int[] colors1 = new int[]{Color.RED, Color.BLUE, Color.RED};
    private int[] colors2 = new int[]{Color.GREEN, Color.RED, Color.GREEN};
    private int[] colors3 = new int[]{Color.BLUE, Color.GREEN, Color.BLUE};

    private List<int[]> colorList = new ArrayList<>();


    public DataRing(Context context) {
        super(context);
        this.context = context;

    }

    public DataRing(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.context = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DataRing);
        ringWidth = typedArray.getDimensionPixelSize(R.styleable.DataRing_ringWidth, DEFAULT_RING_WIDTH);
        dataTextSize = typedArray.getDimensionPixelSize(R.styleable.DataRing_dataTextSize, DEFAULT_DATA_TEXT_SIZE);
        typedArray.recycle();

    }

    public DataRing(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

    }

    /**
     * 初始化3个圆环的RectF
     */
    private void initRectF() {
        float centerX = (getWidth()) / 2;
        rectFs = new RectF[3];
        for (int i = 1; i < rectFs.length + 1; i++) {
            rectFs[i - 1] = new RectF(ringWidth * (i), ringWidth * (i), 2 * centerX - ringWidth * i, 2 * centerX - ringWidth * i);
        }
        colorList.add(colors1);
        colorList.add(colors2);
        colorList.add(colors3);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initRectF();

        /**
         * 绘制圆弧
         * a b c 分别为圆弧头部与起始位置夹角的正弦
         * */
        double sina = ringWidth / (getWidth() / 2 - ringWidth / 2);
        double sinb = ringWidth / (getWidth() / 2 - ringWidth / 2 - ringWidth);
        double sinc = ringWidth / (getWidth() / 2 - ringWidth / 2 - ringWidth * 2);

        drawArc(canvas, rectFs[0], 0, angleLength1, sina);
        drawArc(canvas, rectFs[1], 1, angleLength2, sinb);
        drawArc(canvas, rectFs[2], 2, angleLength3, sinc);
        drawDataText(canvas, getWidth() / 2, "步频", 0);
        drawDataText(canvas, getWidth() / 2, "步幅", 1);
        drawDataText(canvas, getWidth() / 2, "时长", 2);
    }


    /**
     * 2.绘制圆弧
     * aSin 圆弧头部与起始位置夹角的弧度
     * degree  圆弧头部与起始位置夹角的度数
     */
    private void drawArc(Canvas canvas, RectF rectF, int position, float currentAngleLength, double a) {
        float aSin = (float) Math.asin(a);
        float degree = (float) (aSin * 180 / Math.PI);
        int count = ((int) currentAngleLength) / 360;
        Paint paintCurrent = new Paint();
        paintCurrent.setStrokeJoin(Paint.Join.ROUND);
        paintCurrent.setStrokeCap(Paint.Cap.ROUND);//圆角弧度
        paintCurrent.setStyle(Paint.Style.STROKE);//设置填充样式
        paintCurrent.setAntiAlias(true);//抗锯齿功能
        paintCurrent.setStrokeWidth(ringWidth);//设置画笔宽度
        drawColor = colorList.get(position);
        SweepGradient sweepGradient = new SweepGradient(getWidth() / 2, getHeight() / 2, drawColor, null);
        Matrix matrix = new Matrix();
        matrix.setRotate(startAngle - degree, getWidth() / 2, getHeight() / 2);//填充色起始位置
        sweepGradient.setLocalMatrix(matrix);
        paintCurrent.setShader(sweepGradient);
        if (count == 0) {//绘制第一圈圆弧
            canvas.drawArc(rectF, startAngle, currentAngleLength, false, paintCurrent);
        } else {
            int centerRed = Color.argb(255, 255, 0, 75 * count);
            int centerRedBefore = Color.argb(255, 255, 0, 75 * (count - 1));
            int centerGreen = Color.argb(255, 0, 255, 75 * count);
            int centerGreenBefore = Color.argb(255, 0, 255, 75 * (count - 1));
            int centerBlue = Color.argb(255, 75 * count, 0, 255);
            int centerBlueBefore = Color.argb(255, 75 * (count - 1), 0, 255);
            switch (position) {
                case 0:
                    drawColor = new int[]{Color.RED, centerGreen, Color.RED};
                    drawColorBefore = new int[]{Color.RED, centerGreenBefore, Color.RED};
                    break;
                case 1:
                    drawColor = new int[]{Color.GREEN, centerBlue, Color.GREEN};
                    drawColorBefore = new int[]{Color.GREEN, centerBlueBefore, Color.GREEN};
                    break;
                case 2:
                    drawColor = new int[]{Color.BLUE, centerRed, Color.BLUE};
                    drawColorBefore = new int[]{Color.BLUE, centerRedBefore, Color.BLUE};
                    break;

            }
            /**
             *    绘制第二圈圆弧的时候，要先绘制第一圈圆弧，作为底图 以此类推
             *    每当绘制第n圈圆弧的时候，首先绘制第n-1圈圆弧作为底图
             */

            if (count == 1) {
                sweepGradient = new SweepGradient(getWidth() / 2, getHeight() / 2, colorList.get(position), null);
            } else {
                sweepGradient = new SweepGradient(getWidth() / 2, getHeight() / 2, drawColorBefore, null);
            }
            matrix = new Matrix();
            matrix.setRotate(startAngle - degree, getWidth() / 2, getHeight() / 2);//填充色起始位置
            sweepGradient.setLocalMatrix(matrix);
            paintCurrent.setShader(sweepGradient);
            canvas.drawArc(rectF, startAngle, currentAngleLength - 360 * (count - 1), false, paintCurrent);
            sweepGradient = new SweepGradient(getWidth() / 2, getHeight() / 2, drawColor, null);
            matrix = new Matrix();
            matrix.setRotate(startAngle - degree, getWidth() / 2, getHeight() / 2);//填充色起始位置
            sweepGradient.setLocalMatrix(matrix);
            paintCurrent.setShader(sweepGradient);
            canvas.drawArc(rectF, startAngle, currentAngleLength - 360 * count, false, paintCurrent);

        }

    }


    /**
     * 4.圆弧开头文字
     */
    private void drawDataText(Canvas canvas, float centerX, String text, int type) {
        Paint vTextPaint = new Paint();
        vTextPaint.setTextSize(dataTextSize);
        vTextPaint.setTextAlign(Paint.Align.CENTER);
        vTextPaint.setAntiAlias(true);//抗锯齿功能
        vTextPaint.setColor(ContextCompat.getColor(context, R.color.gray));
        Rect bounds = new Rect();
        vTextPaint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, centerX + bounds.width() / 2, ringWidth + ringWidth * type + bounds.height() / 2, vTextPaint);

    }


    /**
     * dip 转换成px
     *
     * @param dip
     * @return
     */

    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }


    public void setCurrentCount(int animTime, int total1, int current1, int total2, int current2, int total3, int current3) {

        float currentAngleLength1 = (float) current1 / total1 * 360;
        float currentAngleLength2 = (float) current2 / total2 * 360;
        float currentAngleLength3 = (float) current3 / total3 * 360;
        /**
         * 圆弧最多转720度，超过720度按720度显示
         */
        currentAngleLength1 = currentAngleLength1 >= 720 ? 720 : currentAngleLength1;
        currentAngleLength2 = currentAngleLength2 >= 720 ? 720 : currentAngleLength2;
        currentAngleLength3 = currentAngleLength3 >= 720 ? 720 : currentAngleLength3;

        /**开始执行动画*/
        setAnimation(0, currentAngleLength1, animTime);
        setAnimation1(0, currentAngleLength2, animTime);
        setAnimation2(0, currentAngleLength3, animTime);

    }

    /**
     * 为进度设置动画
     * ValueAnimator是整个属性动画机制当中最核心的一个类，属性动画的运行机制是通过不断地对值进行操作来实现的，
     * 而初始值和结束值之间的动画过渡就是由ValueAnimator这个类来负责计算的。
     * 它的内部使用一种时间循环的机制来计算值与值之间的动画过渡，
     * 我们只需要将初始值和结束值提供给ValueAnimator，并且告诉它动画所需运行的时长，
     * 那么ValueAnimator就会自动帮我们完成从初始值平滑地过渡到结束值这样的效果。
     *
     * @param last
     * @param current
     */
    private void setAnimation(float last, float current, int length) {
        ValueAnimator progressAnimator = ValueAnimator.ofFloat(last, current);
        progressAnimator.setDuration(length);
        progressAnimator.setTarget(angleLength1);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                angleLength1 = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        progressAnimator.start();
    }

    private void setAnimation1(float last, float current, int length) {
        ValueAnimator progressAnimator = ValueAnimator.ofFloat(last, current);
        progressAnimator.setDuration(length);
        progressAnimator.setTarget(angleLength2);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                angleLength2 = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        progressAnimator.start();
    }

    private void setAnimation2(float last, float current, int length) {
        ValueAnimator progressAnimator = ValueAnimator.ofFloat(last, current);
        progressAnimator.setDuration(length);
        progressAnimator.setTarget(angleLength3);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                angleLength3 = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        progressAnimator.start();
    }


}
