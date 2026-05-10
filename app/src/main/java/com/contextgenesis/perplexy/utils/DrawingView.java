package com.contextgenesis.perplexy.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.core.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.contextgenesis.perplexy.R;
import com.contextgenesis.perplexy.ui.QuestionsActivity;

/**
 * Created by bhutanidhruv16 on 24-Mar-16.
 */

public class DrawingView extends View {

    public static Paint mPaint;
    public int width;
    public int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    Context context;
    private Paint circlePaint;
    private Path circlePath;

    static RelativeLayout canvas_main;
    RelativeLayout canvas_screen;
    static ImageView canvas_cancel;
    static ImageView pen;
    static ImageView eraser;
    static ImageView refresh;
    static int eraser_s = 0;
    static int pen_s = 0;

    public static void setUpCanvas(final Context context, int questionHeight) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;


        final DialogPlus dialog = DialogPlus.newDialog(context)
                .setGravity(Gravity.BOTTOM)
                .setExpanded(true, 3 * height / 4)                        // Change here
                .setOverlayBackgroundResource(Color.TRANSPARENT)
                .setContentHolder(new ViewHolder(R.layout.dialog_canvas))
                .setContentWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setMargin(QuestionsActivity.convertDip2Pixels(context, 16), QuestionsActivity.convertDip2Pixels(context, 16), QuestionsActivity.convertDip2Pixels(context, 16), QuestionsActivity.convertDip2Pixels(context, 16))
                .create();
        dialog.show();

        View dialogView = dialog.getHolderView();
        canvas_main = (RelativeLayout) dialogView.findViewById(R.id.canvas_main);
        pen = (ImageView) dialogView.findViewById(R.id.canvas_pen);
        eraser = (ImageView) dialogView.findViewById(R.id.canvas_eraser);
        refresh = (ImageView) dialogView.findViewById(R.id.canvas_refresh);
        canvas_cancel = (ImageView) dialogView.findViewById(R.id.canvas_cancel);

        canvas_main.addView(new DrawingView(context));

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(ContextCompat.getColor(context, R.color.color_pen));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(3);

        pen.setBackground(ContextCompat.getDrawable(context, R.drawable.button_ripple_selected));

        eraser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPaint.setColor(ContextCompat.getColor(context, R.color.canvas_bg));
                mPaint.setStrokeWidth(20);
                if (eraser_s == 0) {
                    pen.setBackground(ContextCompat.getDrawable(context, R.drawable.button_ripple));
                    eraser.setBackground(ContextCompat.getDrawable(context, R.drawable.button_ripple_selected));
                    eraser_s = 1;
                    pen_s = 0;
                }
            }
        });

        pen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPaint.setColor(ContextCompat.getColor(context, R.color.color_pen));
                mPaint.setStrokeWidth(3);
                if (pen_s == 0) {
                    pen.setBackground(ContextCompat.getDrawable(context, R.drawable.button_ripple_selected));
                    eraser.setBackground(ContextCompat.getDrawable(context, R.drawable.button_ripple));
                    pen_s = 1;
                    eraser_s = 0;
                }
            }
        });

        refresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                canvas_main.removeAllViews();
                canvas_main.addView(new DrawingView(context));

                mPaint.setColor(ContextCompat.getColor(context, R.color.color_pen));
                mPaint.setStrokeWidth(3);

                pen.setBackground(ContextCompat.getDrawable(context, R.drawable.button_ripple_selected));
                eraser.setBackground(ContextCompat.getDrawable(context, R.drawable.button_ripple));

                eraser_s = 0;
                pen_s = 1;
            }
        });

        canvas_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    public DrawingView(Context c) {
        super(c);
        context = c;
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.YELLOW);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(2f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(circlePath, circlePaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;

            circlePath.reset();
            circlePath.addCircle(mX, mY, 20, Path.Direction.CW);
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        circlePath.reset();
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }
}