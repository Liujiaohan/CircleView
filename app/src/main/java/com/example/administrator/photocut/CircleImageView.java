package com.example.administrator.photocut;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2016/11/8/008.
 */

public class CircleImageView extends ImageView {
    public static final ScaleType SCALE_TYPE=ScaleType.CENTER;

    private static final Bitmap.Config BITMAP_CONFIG=Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION=1;

    private static final int DEFAULT_BORDER_WIDTH=0;
    private static final int DEFAULT_BORDER_COLOR= Color.BLACK;

    private static final RectF mDrawableRect=new RectF();
    private static final RectF mBorderRect=new RectF();

    private final Matrix mShaderMatrix = new Matrix();
    private final Paint mBitMapPaint=new Paint();
    private final Paint mBorderPaint=new Paint();

    private int mBorderColor=DEFAULT_BORDER_COLOR;
    private int mBorderWidth=DEFAULT_BORDER_WIDTH;

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;

    private float mDrawableRaduis;
    private float mBorderRadius;

    private boolean mReady;
    private boolean mSetupPending;

    public Path mPath=new Path();
    public float angle;
    public int count=6;

    public int centerX;
    public int centerY;

    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setScaleType(SCALE_TYPE);

        TypedArray a=context.obtainStyledAttributes(attrs,R.styleable.CircleImageView,defStyleAttr,0);
        mBorderWidth=a.getDimensionPixelSize(R.styleable.CircleImageView_border_width,DEFAULT_BORDER_WIDTH);
        mBorderColor=a.getColor(R.styleable.CircleImageView_border_color,DEFAULT_BORDER_COLOR);
        a.recycle();//为了和面可以重用

        mReady=true;

        if (mSetupPending){
            setup();
            mSetupPending=false;
        }
    }

    @Override
    public ScaleType getScaleType(){
        return SCALE_TYPE;
    }

    @Override
    public void setScaleType(ScaleType sT){
        if (sT!=SCALE_TYPE){
            throw new IllegalArgumentException(String.format("ScaleType %s not support",sT));
        }
    }

    @Override
    public void onDraw(Canvas canvas){
        if (getDrawable()==null){
            return;
        }
       drawPath();
        //canvas.drawCircle(getWidth()/2,getHeight()/2,mDrawableRaduis,mBitMapPaint);
        canvas.drawPath(mPath,mBitMapPaint);
        canvas.drawCircle(getWidth()/2,getHeight()/2,mBorderRadius,mBorderPaint);

    }

    public void drawPath(){
        angle=(float)(Math.PI*2/count);

        centerX=getWidth()/2;
        centerY=getHeight()/2;

        for (int i=1;i<=count;i++){
            float x=(float) (centerX+mDrawableRaduis*Math.cos(angle*i));
            float y=(float) (centerY+mDrawableRaduis*Math.sin(angle*i));
            if (i==1){
                mPath.moveTo(x,y);
            }
            else mPath.lineTo(x,y);
        }
        mPath.close();
    }
    @Override
    public void onSizeChanged(int w,int h,int oldw,int oldh){
       super.onSizeChanged(w,h,oldh,oldw);
        setup();
    }

    public void setCount(int sCount){
        count=sCount;
    }

    public void changeCount(){
        mPath.reset();
        invalidate();
    }
    public int getBorderColor(){
        return mBorderColor;
    }

    public void setBorderColor(int color){
        if (color==mBorderColor){
            return;
        }
        mBorderColor=color;
        mBorderPaint.setColor(mBorderColor);
        invalidate();
    }

    public int getDefaultBorderWidth(){
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth){
        if (borderWidth==mBorderWidth){
            return;
        }
        mBorderColor=borderWidth;
        setup();
    }

    @Override
    public void setImageBitmap(Bitmap bitmap){
        super.setImageBitmap(bitmap);
        mBitmap=bitmap;
        setup();
    }

    @Override
    public  void setImageDrawable(Drawable drawable){
        super.setImageDrawable(drawable);
        mBitmap=getBitmapFromDrawable(drawable);
        setup();
    }
    @Override
    public void setImageResource(int resourceID){
        super.setImageResource(resourceID);
        mBitmap=getBitmapFromDrawable(getDrawable());
    }

    public Bitmap getBitmapFromDrawable(Drawable drawable){
        if (drawable==null){
            return null;
        }
        if (drawable instanceof BitmapDrawable){
            return ((BitmapDrawable)drawable).getBitmap();
        }

        try {
            Bitmap bitmap;
            if (drawable instanceof ColorDrawable){
                bitmap=Bitmap.createBitmap(COLORDRAWABLE_DIMENSION,COLORDRAWABLE_DIMENSION,BITMAP_CONFIG);
            }
            else
            {
                bitmap=Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(),BITMAP_CONFIG);
            }
            Canvas canvas=new Canvas(bitmap);
            drawable.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }catch (OutOfMemoryError error){
            error.printStackTrace();
            return null;
        }
    }
    private void setup(){
        if (!mReady){
            mSetupPending=true;
            return;
        }
        if (mBitmap==null){
            return;
        }
        mBitmapShader=new BitmapShader(mBitmap, Shader.TileMode.CLAMP,Shader.TileMode.CLAMP);

        mBitMapPaint.setAntiAlias(true);
        mBitMapPaint.setShader(mBitmapShader);

        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);

        mBitmapHeight=mBitmap.getHeight();
        mBitmapWidth=mBitmap.getWidth();

        mBorderRect.set(0,0,getWidth(),getHeight());
        mBorderRadius=Math.min((mBorderRect.width()-mBorderWidth)/2,(mBorderRect.height()-mBorderWidth)/2);

        mDrawableRect.set(mBorderWidth,mBorderWidth,mBorderRect.width()-mBorderWidth,mBorderRect.height()-mBorderWidth);
        mDrawableRaduis=Math.min(mDrawableRect.width()/2,mDrawableRect.height()/2);

        updateShaderMatrix();
        invalidate();
    }
    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }
        //选择缩放比例大的会把整张图给弄进来，短边进行拉伸，选择缩放比例小的会丢失一部分
        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth, (int) (dy + 0.5f) + mBorderWidth);
        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }
}
