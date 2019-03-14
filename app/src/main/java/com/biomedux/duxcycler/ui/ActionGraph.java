// ============================================================
// FileName		: ActionGraph.java
// Author		: JaeHong Min
// Date			: 2017.07.04
// ============================================================
// 현재 이 기능은 사용되지 않고 있음
// 사용시 GOTO 영역 구분이 안되는 문제가 있으므로 수정 바람
//
// 'MainActivity.java', 'fragment_main.xml'에 ActionGraph 주석
// 해제시 적용 됨
// ============================================================

package com.biomedux.duxcycler.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

import com.biomedux.duxcycler.beans.Action;
import com.biomedux.duxcycler.util.Util;

public class ActionGraph extends View {

    // ============================================================
    // Constants
    // ============================================================

    private static final int COLOR_BACKGROUND		= 0xFFFFFFFF;
    private static final int COLOR_LINE				= 0xFF3F51B5;
    private static final int COLOR_GOTO_LINE		= 0x40FF7F27;
    private static final int COLOR_GOTO_TEXT		= 0xFFFF7F27;
    private static final int COLOR_GRID_LINE		= 0xFFEBEBEB;

    private static final int TIME_GAP_SIZE			= 30;

    // ============================================================
    // Fields
    // ============================================================

    private ArrayList<Action> actions;

    private int frameWidth;
    private int frameHeight;
    private float frameScaleX;
    private float frameScaleY;

    private int width;
    private int height;

    private int viewX;
    private int viewY;

    private int time;

    // ============================================================
    // Constructors
    // ============================================================

    public ActionGraph(Context context) {
        super(context);
    }

    public ActionGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ActionGraph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // ============================================================
    // Getter & Setter
    // ============================================================

    // ============================================================
    // Methods for/from SuperClass/Interfaces
    // ============================================================

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setClickable(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wmode = MeasureSpec.getMode(widthMeasureSpec);
        int hmode = MeasureSpec.getMode(heightMeasureSpec);
        int wsize = MeasureSpec.getSize(widthMeasureSpec);
        int hsize = MeasureSpec.getSize(heightMeasureSpec);
        int width = 0;
        int height = 0;

        switch (wmode) {
            case MeasureSpec.UNSPECIFIED:
                width = frameWidth;
                break;

            case MeasureSpec.AT_MOST:
                if (hmode == MeasureSpec.EXACTLY) {
                    width = Math.min(hsize * frameWidth / frameHeight, wsize);
                    break;
                } else {
                    width = frameWidth;
                }
                break;

            case MeasureSpec.EXACTLY:
                width = wsize;
                break;
        }

        switch (hmode) {
            case MeasureSpec.UNSPECIFIED:
                height = frameHeight;
                break;

            case MeasureSpec.AT_MOST:
                if (wmode == MeasureSpec.EXACTLY) {
                    height = Math.min(wsize * frameHeight / frameWidth, hsize);
                    break;
                } else {
                    height = frameHeight;
                }
                break;

            case MeasureSpec.EXACTLY:
                height = hsize;
                break;
        }

        frameScaleX = (float) width / frameWidth;
        frameScaleY = (float) height / frameHeight;

        this.width = (int) (frameWidth * frameScaleX);
        this.height = (int) (frameHeight * frameScaleY);

        setMeasuredDimension(width, height + 15);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // Log.d("ActionGraph", "onLayout(" + changed + ", " + left + ", " + top + ", " + right + ", " + bottom + ")");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // Log.d("ActionGraph", "onSizeChanged(" + w + ", " + h + ", " + oldw + ", " + oldh + ")");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float startX, startY, stopX, stopY;
        int timeSum, timeSum2, tempSum;
        int nTime, nTemp;

        int gap;

        Paint paint = new Paint();
        paint.setStrokeWidth(5.0f);
        paint.setTextSize(50);


        /* drawFrame */

        canvas.drawColor(COLOR_BACKGROUND);

        paint.setColor(COLOR_GRID_LINE);
        canvas.drawLine(0, 0, width, 0, paint);
        canvas.drawLine(0, height, width, height, paint);

        if (actions.size() == 0)
            return;


        /* drawGrid */

        for (int i = 0, j = 0; i <= width; i += width / time * TIME_GAP_SIZE, j++) {
            canvas.drawLine(i, 0, i, height, paint);
            canvas.drawText(Util.toHMS(j * 30), i + 15, height - 15, paint);
        }


        /* drawGraph */

        paint.setColor(COLOR_LINE);

        timeSum = 0;
        tempSum = Integer.parseInt(actions.get(0).getTemp());
        for (int i = 0; i < actions.size(); i++) {
            if (!actions.get(i).getLabel().equals("GOTO")) {
                nTime = Integer.parseInt(actions.get(i).getTime());
                nTemp = Integer.parseInt(actions.get(i).getTemp());
                gap = Math.min(width / time * (nTime / 10), 20);

                startX = timeSum * (width / time);
                startY = height - tempSum * (height / viewY);
                stopX = (nTime + timeSum) * (width / time);
                stopY = height - nTemp * (height / viewY);

                canvas.drawLine(startX, startY, startX + gap, stopY, paint);
                canvas.drawLine(startX + gap, stopY, stopX, stopY, paint);
                canvas.drawText(actions.get(i).getTemp() , (startX + stopX) / 2 , stopY - 30, paint);

                timeSum += nTime;
                tempSum = nTemp;
            }
        }


        /* drawGoto */

        paint.setTypeface(Typeface.DEFAULT_BOLD);

        timeSum = 0;
        for (int i = 0; i < actions.size(); i++) {
            nTime = Integer.parseInt(actions.get(i).getTime());

            if (actions.get(i).getLabel().equals("GOTO")) {
                gap = Math.min(width / time * (nTime / 10), 20);

                stopX = timeSum * (width / time);

                timeSum2 = 0;
                for (int j = 0; j < actions.size(); j++) {
                    if (actions.get(j).getLabel().equals(actions.get(i).getTemp()))
                        break;
                    if (!actions.get(j).getLabel().equals("GOTO"))
                        timeSum2 += Integer.parseInt(actions.get(j).getTime());
                }

                startX = timeSum2 * (width / time) + gap;

                paint.setColor(COLOR_GOTO_LINE);
                canvas.drawRect(new RectF(startX, 0, stopX, height), paint);

                paint.setColor(COLOR_GOTO_TEXT);
                canvas.drawText(actions.get(i).getTime() + "x GOTO", startX + 15, 65, paint);
            } else {
                timeSum += nTime;
            }
        }
    }

    // ============================================================
    // Methods
    // ============================================================

    public void update(ArrayList<Action> actions, int vWidth, int vHeight, int vTime, int vTemp) {
        this.actions = actions;

        time = Util.calcProtocolTimeLite(actions);

        frameWidth = Math.max(vWidth, vWidth / vTime * time);
        frameHeight = vHeight;

        viewX = vTime;
        viewY = vTemp;

        measure(frameWidth, frameHeight);
        requestLayout();
        invalidate();
    }

    // ============================================================
    // Inner and Anonymous Classes
    // ============================================================

}
