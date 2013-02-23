
package jp.ddo.trismegistos.androidutil.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

/**
 * 拡大・縮小・移動を可能としたImageViewクラス。<br>
 * http://blog.livedoor.jp/sylc/archives/1470690.htmlをベースに作成。
 */
public class ScalableView extends ImageView implements OnTouchListener {

    /** タグ。 */
    private static final String TAG = ScalableView.class.getSimpleName();
    private static final float MAX_SCALE = 5;
    private static final float MIN_SCALE = 0.3f;
    private static final float MIN_LENGTH = 30f;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;

    /** MatrixのgetValues用。 */
    private float[] values = new float[9];

    /** ドラッグ用マトリックス。 */
    private Matrix moveMatrix = new Matrix();

    /** マトリックス。 */
    private Matrix matrix = new Matrix();

    /** 画像移動用の位置。 */
    private PointF point = new PointF();

    /** ズーム時の座標。 */
    private PointF middle = new PointF();

    /** タッチモード。何も無し、ドラッグ、ズーム。 */
    private int mode = NONE;

    /** Zoom開始時の二点間距離。 */
    private float initLength = 1;

    /**
     * コンストラクタ。
     * 
     * @param context コンテキスト
     */
    public ScalableView(final Context context) {
        this(context, null, 0);
        matrix = new Matrix();
        matrix.setScale(1, 1);
        setOnTouchListener(this);
    }

    /**
     * コンストラクタ。
     * 
     * @param context コンテキスト
     * @param attrs パラメータ
     */
    public ScalableView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
        matrix = new Matrix();
        matrix.setScale(1, 1);
        setOnTouchListener(this);
    }

    /**
     * コンストラクタ。
     * 
     * @param context コンテキスト
     * @param attrs パラメータ
     * @param defStyle スタイル
     */
    public ScalableView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        matrix = new Matrix();
        matrix.setScale(1, 1);
        setOnTouchListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        ImageView view = (ImageView) v;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "mode=DRAG");
                mode = DRAG;
                point.set(event.getX(), event.getY());
                moveMatrix.set(matrix);
                break;
            case MotionEvent.ACTION_POINTER_2_UP:
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "mode=NONE");
                mode = NONE;
                break;
            case MotionEvent.ACTION_POINTER_2_DOWN:
                if (ScaleType.MATRIX.equals(getScaleType()) == false) {
                    setScaleType(ScaleType.MATRIX);
                    matrix.setScale(1, 1);
                    matrix.setTranslate((getWidth() - getDrawable().getIntrinsicWidth()) / 2,
                            (getHeight() - getDrawable().getIntrinsicHeight()) / 2);
                    view.setImageMatrix(matrix);
                }
                initLength = getLength(event);
                if (initLength > MIN_LENGTH) {
                    Log.d(TAG, "mode=ZOOM");
                    moveMatrix.set(matrix);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                switch (mode) {
                    case DRAG:
                        matrix.set(moveMatrix);
                        matrix.postTranslate(event.getX() - point.x, event.getY() - point.y);
                        view.setImageMatrix(matrix);
                        break;
                    case ZOOM:
                        if (mode == ZOOM) {
                            float currentLength = getLength(event);
                            middle = getMiddle(event, middle);
                            if (currentLength > MIN_LENGTH) {
                                matrix.set(moveMatrix);
                                float scale = filter(matrix, currentLength / initLength);
                                matrix.postScale(scale, scale, middle.x, middle.y);
                                view.setImageMatrix(matrix);
                            }
                            break;
                        }
                        break;
                }
        }
        return false;
    }

    /**
     * 拡大縮小可能かどうかを判定する
     * 
     * @param m
     * @param s
     * @return
     */
    private float filter(Matrix m, float s) {
        m.getValues(values);
        float nextScale = values[0] * s;
        if (nextScale > MAX_SCALE) {
            s = MAX_SCALE / values[0];
        } else if (nextScale < MIN_SCALE) {
            s = MIN_SCALE / values[0];
        }
        return s;
    }

    /**
     * 比率を計算
     * 
     * @param x
     * @param y
     * @return
     */
    private float getLength(MotionEvent e) {
        if (e.getPointerCount() > 1) {
            float xx = e.getX(1) - e.getX(0);
            float yy = e.getY(1) - e.getY(0);
            return FloatMath.sqrt(xx * xx + yy * yy);
        } else {
            return 1;
        }
    }

    /**
     * 中間点を求める
     * 
     * @param e
     * @param p
     * @return
     */
    private PointF getMiddle(MotionEvent e, PointF p) {
        if (e.getPointerCount() > 1) {
            float x = e.getX(0) + e.getX(1);
            float y = e.getY(0) + e.getY(1);
            p.set(x / 2, y / 2);
        }
        return p;
    }
}
