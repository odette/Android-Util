
package jp.ddo.trismegistos.androidutil.view;

import jp.ddo.trismegistos.androidutil.R;
import jp.ddo.trismegistos.androidutil.view.helper.ImageCache;
import jp.ddo.trismegistos.androidutil.view.helper.WebImageLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ViewFlipper;

/**
 * Web上の画像を非同期で取得し、表示するViewクラス。<br>
 * 取得した画像のキャッシュも行う。
 * 
 * @author y_sugasawa
 * @since 2013/01/28
 */
public class WebImageView extends ViewFlipper implements LoaderCallbacks<Bitmap> {

    /** タグ。 */
    private static final String TAG = WebImageView.class.getSimpleName();

    private ScalableView image;

    /** 画像URL。 */
    private String url;

    /** NoImae画像のリソースID。 */
    private int defaultImage;

    /** キャッシュ管理オブジェクト。 */
    private ImageCache imageCache;

    private boolean scalable = false;

    private OnClickListener cl;

    private OnLongClickListener lcl;

    /**
     * コンストラクタ。
     * 
     * @param context コンテキスト
     */
    public WebImageView(final Context context) {
        super(context);
        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.loading, this);

        image = new ScalableView(context);
        addView(image);
    }

    /**
     * コンストラクタ。<br>
     * xmlで定義されている場合はこちらが呼ばれる。
     * 
     * @param context コンテキスト
     * @param attrs パラメータ
     */
    public WebImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.loading, this);

        image = new ScalableView(context);
        addView(image);
    }

//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public void setOnClickListener(final OnClickListener cl) {
//        this.cl = cl;
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public void setOnLongClickListener(final OnLongClickListener lcl) {
//        this.lcl = lcl;
//    }

    /**
     * 画像の取得、キャッシュ保存、表示を行う。
     */
    public void show() {
        setClickable(scalable);
        setFocusable(scalable);
        setFocusableInTouchMode(scalable);
        image.setClickable(scalable);
        image.setFocusable(scalable);
        image.setFocusableInTouchMode(scalable);
//        if (scalable) {
//            image.setOnClickListener(cl);
//            image.setOnLongClickListener(lcl);
//        } else {
//            super.setOnClickListener(cl);
//            super.setOnLongClickListener(lcl);
//        }
        if (url == null) {
            image.setImageResource(defaultImage);
            if (getDisplayedChild() == 0) {
                showNext();
            }
        }
        if (imageCache != null) {
            final Bitmap bm = imageCache.getImage(url);
            if (bm != null) {
                image.setImageBitmap(bm);
                if (getDisplayedChild() == 0) {
                    showNext();
                }
                return;
            }
        }
        if (getDisplayedChild() == 1) {
            showPrevious();
        }
        if (getContext() instanceof FragmentActivity) {
            ((FragmentActivity) getContext()).getSupportLoaderManager().initLoader(
                    url.hashCode(), null, this);
        } else {
            throw new ClassCastException("getContext() is not FragmentActivity.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Loader<Bitmap> onCreateLoader(final int id, final Bundle args) {
        return new WebImageLoader(getContext(), url, imageCache);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLoadFinished(final Loader<Bitmap> loader, final Bitmap bitmap) {
        if (loader.getId() == url.hashCode()) {
            if (bitmap != null) {
                image.setImageBitmap(bitmap);
            } else {
                image.setImageResource(defaultImage);
            }
            if (getDisplayedChild() == 0) {
                showNext();
            }
            loader.stopLoading();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLoaderReset(final Loader<Bitmap> loader) {
    }

    /**
     * 画像URLを設定する。
     * 
     * @param url 画像URL
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * NoImage画像のリソースIDを設定する。
     * 
     * @param resId NoImage画像のリソースID
     */
    public void setDefaultImage(final int resId) {
        this.defaultImage = resId;
    }

    /**
     * キャッシュ管理オブジェクトを設定する。
     * 
     * @param imageCache キャッシュ管理オブジェクト
     */
    public void setImageCache(final ImageCache imageCache) {
        this.imageCache = imageCache;
    }

    /**
     * 画像がスケーラブル可能かを設定する。<br>
     * デフォルトはfalse。
     * 
     * @param scalable スケーラブル可能な場合はtrue。
     */
    public void setScalable(boolean scalable) {
        this.scalable = scalable;
    }

}
