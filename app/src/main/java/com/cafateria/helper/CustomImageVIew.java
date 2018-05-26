package com.cafateria.helper;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;


public class CustomImageVIew extends ImageView implements View.OnTouchListener {

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;

    private int mode = NONE;

    private PointF mStartPoint = new PointF();
    private PointF mMiddlePoint = new PointF();

    private float oldDist = 1f;
    private float matrixValues[] = {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    private float scale;
    private int mViewWidth = -1;
    private int mViewHeight = -1;
    private int mBitmapWidth = -1;
    private int mBitmapHeight = -1;


    public CustomImageVIew(Context context) {
        this(context, null, 0);
    }

    public CustomImageVIew(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomImageVIew(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setOnTouchListener(this);
    }

    @Override
    public void onSizeChanged (int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = 20;//w;
        mViewHeight = 20; //h;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event){
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                mStartPoint.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if(oldDist > 10f){
                    savedMatrix.set(matrix);
                    midPoint(mMiddlePoint, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if(mode == DRAG){
                    drag(event);
                } else if(mode == ZOOM){
                    zoom(event);
                }
                break;
        }

        return true;
    }



    public void drag(MotionEvent event){
        System.out.println("moving---drag");

        matrix.set(savedMatrix);
        matrix.postTranslate(event.getX() - mStartPoint.x, event.getY() - mStartPoint.y); // create the transformation in the matrix  of points
        this.setImageMatrix(matrix);

    }

    public void zoom(MotionEvent event){
        matrix.getValues(matrixValues);

        float newDist = spacing(event);
        //float bitmapWidth = matrixValues[0] * mBitmapWidth;
        //float bimtapHeight = matrixValues[0] * mBitmapHeight;
        //boolean in = newDist > oldDist;

        //if(!in && matrixValues[0] < 1){
        //    return;
        //}

        //float midX = (mViewWidth / 2);
        //float midY = (mViewHeight / 2);

        matrix.set(savedMatrix);
        scale = newDist / oldDist;
        //System.out.println("mViewWidth:"+mViewWidth+",mViewHeight:"+mViewHeight+",,"+bitmapWidth);
        matrix.postScale(scale, scale,mMiddlePoint.x , mMiddlePoint.y);

        this.setImageMatrix(matrix);


    }





    /** Determine the space between the first two fingers */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return (float)Math.sqrt(x * x + y * y);
    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}
