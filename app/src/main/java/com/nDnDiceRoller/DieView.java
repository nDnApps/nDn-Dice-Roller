package com.nDnDiceRoller;


        import android.content.Context;
        import android.content.res.Resources;
        import android.graphics.Bitmap;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.ColorMatrixColorFilter;
        import android.graphics.Paint;
        import android.graphics.Rect;
        import android.graphics.drawable.BitmapDrawable;
        import android.util.AttributeSet;
        import android.view.View;

public class DieView extends View{

    int mBitmapSize;
    int mWidth, mHeight;
    //NORMAL_MODE: displays value of mDie, rather than default/hiding it
    //MENU_MODE: does not display mDie.value
    //ERROR_MODE: displays error picture
    public enum Mode{ NORMAL_MODE, MENU_MODE, ERROR_MODE}
    private Mode mMode;
    Paint mTextPaint, mDiePaint;
    Bitmap mBackground, mExplosion;

    Die mDie;
    private boolean mIsNonstandardDie;

    Rect mBitmapBounds;

    public DieView(Context context, Die d)
    {
        super(context);
        mDie = new Die(d);
        initView();
    }

    /**
     * Construct object, initializing with any attributes we understand from a
     * layout file. These attributes are defined in
     * SDK/assets/res/any/classes.xml.`
     *
     * @see android.view.View#View(android.content.Context, android.util.AttributeSet)
     */
    public DieView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDie = new Die(0);
        initView();
    }


    public DieView(Context context, AttributeSet attrs, Die d) {
        super(context, attrs);
        mDie = new Die(d);
        initView();
    }

    public boolean isError(){
        return mDie.sides == 0;
    }

    private void initView(){
        assert(mDie != null);

        mTextPaint = new Paint();
        setTextColor(mDie.textColor);

        mDiePaint = new Paint();
        setDieColor(mDie.dieColor);

        Resources res = getResources();

        mMode = Mode.NORMAL_MODE;
        mIsNonstandardDie = false;
        BitmapDrawable explosion = (BitmapDrawable) res.getDrawable(R.drawable.explosion);
        mExplosion = explosion.getBitmap();

        BitmapDrawable drawable;
        switch(mDie.sides){
            case 0:
                mMode = Mode.ERROR_MODE;
                drawable = (BitmapDrawable) res.getDrawable(R.drawable.blank);
                break;
            case 1:
                drawable = (BitmapDrawable) res.getDrawable(R.drawable.blank);
                break;
            case 2:
                drawable = (BitmapDrawable) res.getDrawable(R.drawable.d2);
                break;
            case 4:
                drawable = (BitmapDrawable) res.getDrawable(R.drawable.d4);
                break;
            case 6:
                drawable = (BitmapDrawable) res.getDrawable(R.drawable.d6);
                break;
            case 8:
                drawable = (BitmapDrawable) res.getDrawable(R.drawable.d8);
                break;
            case 10:
                drawable = (BitmapDrawable) res.getDrawable(R.drawable.d10);
                break;
            case 12:
                drawable = (BitmapDrawable) res.getDrawable(R.drawable.d12);
                break;
            case 20:
                drawable = (BitmapDrawable) res.getDrawable(R.drawable.d20);
                break;
            default:
                mIsNonstandardDie = true;
                drawable = (BitmapDrawable) res.getDrawable(R.drawable.blank);
                break;
        }
        mBackground = drawable.getBitmap();
        mBitmapSize = mBackground.getHeight();
        mBitmapBounds =  new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int chosenWidth = chooseDimension(widthMode, widthSize);
        int chosenHeight = chooseDimension(heightMode, heightSize);

        //view is a square
        mWidth = mHeight = Math.min(chosenHeight, chosenWidth);
        mTextPaint.setTextSize(3 * mWidth / 8);
        setMeasuredDimension(mWidth,mHeight);
    }

    private int chooseDimension(int mode, int size) {
        if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
            return Math.min(size, mBitmapSize);
        }else { // (mode == MeasureSpec.UNSPECIFIED)

            return mBackground.getHeight();
        }
    }

    public void setTextColor(int textColor){
        mDie.textColor = textColor;
        mTextPaint.setColor(textColor);
    }

    public void setDieColor(int dieColor){
        mDie.dieColor = dieColor;
        mDiePaint.setColor(dieColor);
        float r = Color.red(dieColor)/255f;
        float g = Color.green(dieColor)/255f;
        float b = Color.blue(dieColor)/255f;
        float[] colorFilterMatrix;


        //for most colors, replace the green lines with black lines
        //for darker colors, replace them with white lines for visibility
        if(dieColor != Color.BLACK && dieColor != Color.BLUE)
        {
           //Green replaced with white, red replaced with die color
           colorFilterMatrix = new float[]{
                    r,  0f, 0f, 0f, 0f,
                    g, 0f,  0f, 0f, 0f,
                    b, 0f,  0f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
            };
        }else{
            colorFilterMatrix = new float[]{
                    r,  1f, 0f, 0f, 0f,
                    g, 1f,  0f, 0f, 0f,
                    b, 1f,  0f, 0f, 0f,
                    0f, 1f, 0f, 1f, 0f
            };
        }

        mDiePaint.setColorFilter(new ColorMatrixColorFilter(colorFilterMatrix));
    }


    /**
     * Render the text
     *
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        mBitmapBounds.set(getPaddingLeft(), getPaddingTop(), mWidth - getPaddingRight(),
                mHeight - getPaddingBottom());

        //draw explosion background for dice with pictures built in (standard dice)
        if(mDie.isExploding() && !mIsNonstandardDie && mDie.sides != 1){
            canvas.drawBitmap(mExplosion,  null, mBitmapBounds, null);
        }

        canvas.drawBitmap(mBackground,  null, mBitmapBounds, mDiePaint);

        //if error, just quit after drawing background
        if(isError()){
            return;
        }

        String valueText;

        if(mDie.sides == 1){
            //sides==1 means that the "die" is an offset
            valueText = "+" + Integer.toString(mDie.value);
        }else{
            valueText = Integer.toString(mDie.value);
        }

        if(mIsNonstandardDie){
            if(mMode == Mode.MENU_MODE){
                fitText(canvas, mDie.getName(), mDiePaint.getColor());
            }else{
                nonstandardDieText(canvas, valueText, mDie.getName(), mTextPaint.getColor(), mDiePaint.getColor());
            }
        }else{
            centerText(canvas, valueText, mTextPaint);
        }

    }

    private void nonstandardDieText(Canvas canvas, String topText, String bottomText, int topColor, int bottomColor){
        Paint paint = new Paint();

        paint.setColor(topColor);
        paint.setTextSize(mHeight/2 - 6);

        float xText = mWidth/2 - paint.measureText(topText)/2;
        while(xText < 0){
            paint.setTextSize(paint.getTextSize() * 0.75f);
            xText = mWidth/2 - paint.measureText(topText)/2;
        }
        float yText = paint.getTextSize() + 1;
        canvas.drawText(topText, xText, yText, paint);

        paint.setColor(bottomColor);
        paint.setTextSize(mHeight/2 - 6);

        xText = mWidth/2 - paint.measureText(bottomText)/2;
        while(xText < 0){
            paint.setTextSize(paint.getTextSize() * 0.75f);
            xText = mWidth/2 - paint.measureText(bottomText)/2;
        }
        yText = paint.getTextSize() + mHeight/2 + 1;
        canvas.drawText(bottomText, xText, yText, paint);
    }

    private void fitText(Canvas canvas, String text, int color){
        Paint paint = new Paint();

        paint.setColor(color);
        paint.setTextSize(mHeight - 10);

        float xText = mWidth/2 - paint.measureText(text)/2;
        while(xText < 0){
            paint.setTextSize(paint.getTextSize() * 0.75f);
            xText = mWidth/2 - paint.measureText(text)/2;
        }
        float yText = mHeight/2 + paint.getTextSize()/2;
        canvas.drawText(text, xText, yText, paint);

    }

    private void centerText(Canvas canvas, String text, Paint paint){
        float xText = mWidth/2 - paint.measureText(text)/2;
        float yText = mHeight/2 + paint.getTextSize()/2;
        canvas.drawText(text, xText, yText, paint);
    }

    //depreciated
    public void setMenuMode(boolean isMenuMode){
        if(mMode == Mode.ERROR_MODE){
            return; //do nothing, can't get out of error mode like this
        }else if(isMenuMode){
            mMode = Mode.MENU_MODE;
        }else if( mMode == Mode.MENU_MODE){
            mMode = Mode.NORMAL_MODE;
        }

    }

    public void setValue(int value)
    {
        mDie.value = value;
    }

    public Die getDie(){
        return new Die(mDie);
    }

    public void setExploding(Boolean explodingBool){
        mDie.setExploding(explodingBool);
        invalidate();

    }

    public boolean getExploding() {
        return mDie.isExploding();
    }

    public void setDie(Die die) {
        // TODO Auto-generated method stub
        mDie = new Die(die);
        initView();
        invalidate();
    }


    public static boolean IsStandardDie(int sides)
    {
        return sides == 2 || sides == 4 || sides == 6 ||
         sides == 8 || sides == 10 || sides == 12 || sides == 20;
    }
}
