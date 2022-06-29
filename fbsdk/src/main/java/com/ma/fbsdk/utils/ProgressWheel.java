package com.ma.fbsdk.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.ma.fbsdk.R;


/**
 * Created by Ali Noureddine on 2019-11-16.
 */
public class ProgressWheel extends View {

    private int layoutHeight = 0;
    private int layoutWidth = 0;
    private int barLength = 20;
    private int barWidth = 50;
    private int rimWidth = 20;
    private int paddingTop = 5;
    private int paddingBottom = 5;
    private int paddingLeft = 5;
    private int paddingRight = 5;
    private Paint barPaint = new Paint();
    private RectF circleBounds = new RectF();
    private Rect circleOuterContour = new Rect();
    private Rect circleInnerContour = new Rect();

    private float spinSpeed = 10f;
    private int delayMillis = 10;
    private float progress = 0;
    boolean isSpinning = false;

    public ProgressWheel(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(context.obtainStyledAttributes(attrs, R.styleable.ProgressWheel));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        int size = 0;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (heightMode != MeasureSpec.UNSPECIFIED && widthMode != MeasureSpec.UNSPECIFIED) {
            if (widthWithoutPadding > heightWithoutPadding) {
                size = heightWithoutPadding;
            } else {
                size = widthWithoutPadding;
            }
        } else {
            size = Math.max(heightWithoutPadding, widthWithoutPadding);
        }

        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }


    @Override
    protected void onSizeChanged(int newWidth, int newHeight, int oldWidth, int oldHeight) {
        super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight);
        layoutWidth = newWidth;
        layoutHeight = newHeight;
        setupBounds();
        setupPaints();
        invalidate();
    }


    private void setupPaints() {
        barPaint.setColor(Color.WHITE);
        barPaint.setAntiAlias(true);
        barPaint.setStyle(Paint.Style.STROKE);
        barPaint.setStrokeWidth(10);
    }

    private void setupBounds() {
        int minValue = Math.min(layoutWidth, layoutHeight);

        int xOffset = layoutWidth - minValue;
        int yOffset = layoutHeight - minValue;

        paddingTop = this.getPaddingTop() + (yOffset / 2);
        paddingBottom = this.getPaddingBottom() + (yOffset / 2);
        paddingLeft = this.getPaddingLeft() + (xOffset / 2);
        paddingRight = this.getPaddingRight() + (xOffset / 2);

        int width = getWidth();
        int height = getHeight();


        circleBounds = new RectF(
                barWidth + barWidth,
                barWidth + barWidth,
                width - barWidth,
                height - barWidth);
        //bigger image
        circleInnerContour = new Rect(
                (int) (circleBounds.left + (rimWidth / 4.0f)),
                (int) (circleBounds.top + (rimWidth / 4.0f)),
                (int) (circleBounds.right - (rimWidth / 4.0f)),
                (int) (circleBounds.bottom - (rimWidth / 4.0f)));
        circleOuterContour = new Rect(
                (int) (circleBounds.left - (rimWidth / 4.0f)),
                (int) (circleBounds.top - (rimWidth / 4.0f)),
                (int) (circleBounds.right + (rimWidth / 4.0f)),
                (int) (circleBounds.bottom + (rimWidth / 4.0f)));
    }

    private void parseAttributes(TypedArray a) {
        barWidth = (int) a.getDimension(R.styleable.ProgressWheel_pwBarWidth, barWidth);
        rimWidth = (int) a.getDimension(R.styleable.ProgressWheel_pwRimWidth, rimWidth);
        spinSpeed = (int) a.getDimension(R.styleable.ProgressWheel_pwSpinSpeed, spinSpeed);
        barLength = (int) a.getDimension(R.styleable.ProgressWheel_pwBarLength, barLength);
        a.recycle();

    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isSpinning) {
            canvas.drawArc(circleBounds, progress - 90, barLength, false, barPaint);
        } else {
            canvas.drawArc(circleBounds, -90, progress, false, barPaint);
        }
//        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Paint p = new Paint();
        p.setColor(Color.WHITE);
//        canvas.drawBitmap(getCircleBitmap(bm), circleOuterContour, circleInnerContour, new Paint(Paint.FILTER_BITMAP_FLAG|Paint.ANTI_ALIAS_FLAG));
        if (isSpinning) {
            scheduleRedraw();
        }
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);
        final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();
        return output;
    }

    private void scheduleRedraw() {
        progress += spinSpeed;
        if (progress > 360) {
            progress = 0;
        }
        postInvalidateDelayed(delayMillis);
    }

    public void stopSpinning() {
        isSpinning = false;
        progress = 0;
        postInvalidate();
    }


    public void startSpinning() {
        isSpinning = true;
        postInvalidate();
    }


    public void setProgress(int i) {
        isSpinning = false;
        progress = i;
        postInvalidate();
    }


    public void setBarLength(int barLength) {
        this.barLength = barLength;
    }

    public void setBarWidth(int barWidth) {
        this.barWidth = barWidth;

        if (this.barPaint != null) {
            this.barPaint.setStrokeWidth(this.barWidth);
        }
    }

    public int getPaddingTop() {
        return paddingTop;
    }


    public int getPaddingBottom() {
        return paddingBottom;
    }


    public int getPaddingLeft() {
        return paddingLeft;
    }


    public int getPaddingRight() {
        return paddingRight;
    }


    public void setSpinSpeed(float spinSpeed) {
        this.spinSpeed = spinSpeed;
    }


    public void setDelayMillis(int delayMillis) {
        this.delayMillis = delayMillis;
    }


    public int getProgress() {
        return (int) progress;
    }
}
