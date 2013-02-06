package jp.ddo.trismegistos.androidutil.ui;

import java.io.File;

import jp.ddo.trismegistos.androidutil.R;
import jp.ddo.trismegistos.androidutil.ui.helper.ImageCache;
import jp.ddo.trismegistos.androidutil.ui.helper.WebImageLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.ViewFlipper;

/**
 * @author y_sugasawa
 * @since 2013/01/28
 */
public class WebImageView extends ViewFlipper implements LoaderCallbacks<Bitmap> {

	private static final String TAG = WebImageView.class.getSimpleName();

	private ImageView image;

	private String url;

	private File cacheDir;

	private int defaultImage;

	public WebImageView(final Context context) {
		super(context);
		final LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.loading, this);

		image = new ImageView(context);
		addView(image);
	}

	public WebImageView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		final LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.loading, this);

		image = new ImageView(context);
		addView(image);
	}

	public void draw() {
		if (url == null) {
			image.setImageResource(defaultImage);
		}
		Bitmap bm = ImageCache.getImage(cacheDir, url);
		if (bm != null) {
			image.setImageBitmap(bm);
			if (getDisplayedChild() == 0) {
				showNext();
			}
			return;
		}
		if (getDisplayedChild() == 1) {
			showPrevious();
		}
		if (getContext() instanceof FragmentActivity) {
			((FragmentActivity) getContext()).getSupportLoaderManager().restartLoader(
					url.hashCode(), null, this);
		} else {
			throw new ClassCastException("getContext() is not FragmentActivity.");
		}
	}

	@Override
	public Loader<Bitmap> onCreateLoader(final int id, final Bundle args) {
		return new WebImageLoader(getContext(), url, cacheDir);
	}

	public void onLoadFinished(final Loader<Bitmap> loader, final Bitmap bitmap) {
		if (loader.getId() == url.hashCode()) {
			if (bitmap != null) {
				image.setImageBitmap(bitmap);
			} else {
				image.setImageResource(R.drawable.ic_launcher);
			}
			if (getDisplayedChild() == 0) {
				showNext();
			}
			loader.stopLoading();
		}
	}

	@Override
	public void onLoaderReset(final Loader<Bitmap> loader) {
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public void setCacheDir(final File cacheDir) {
		this.cacheDir = cacheDir;
	}

	public void setDefaultImage(final int resId) {
		this.defaultImage = resId;
	}

}
