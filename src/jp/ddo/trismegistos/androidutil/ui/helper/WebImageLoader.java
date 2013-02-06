package jp.ddo.trismegistos.androidutil.ui.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import jp.ddo.trismegistos.androidutil.loader.AbstractAsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Web上の画像をロードするLoaderクラス。
 * 
 * @author y_sugasawa
 * @since 2013/02/05
 */
public class WebImageLoader extends AbstractAsyncTaskLoader<Bitmap> {

	private static final String TAG = WebImageLoader.class.getSimpleName();

	private String url;
	private File cacheDir;

	/**
	 * コンストラクタ。
	 * 
	 * @param context
	 * @param url
	 * @param cacheDir
	 */
	public WebImageLoader(final Context context, final String url, final File cacheDir) {
		super(context);
		this.url = url;
		this.cacheDir = cacheDir;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Bitmap load() {

		HttpURLConnection con = null;
		InputStream in = null;
		FileOutputStream fos = null;

		try {
			URL imageUrl = new URL(url);
			con = (HttpURLConnection) imageUrl.openConnection();
			con.setUseCaches(true);
			con.setRequestMethod("GET");
			con.setReadTimeout(500000);
			con.setConnectTimeout(50000);
			con.connect();
			in = con.getInputStream();

			Bitmap bitmap = null;
			if (cacheDir != null) {
				if (cacheDir.exists() == false) {
					cacheDir.mkdir();
				}
				final byte[] buf = new byte[1024];
				int len = 0;
				final File localFile = new File(cacheDir, ImageCache.getFileName(url));
				fos = new FileOutputStream(localFile);
				while ((len = in.read(buf)) > -1) {
					fos.write(buf, 0, len);
				}
				fos.flush();
				bitmap = BitmapFactory.decodeFile(localFile.getPath());
			}
			if (bitmap == null) {
				bitmap = BitmapFactory.decodeStream(in);
			}
			ImageCache.saveBitmap(cacheDir, url, bitmap);
			return bitmap;
		} catch (final Exception e) {
			Log.e(TAG, e.getMessage());
		} finally {
			if (con != null) {
				try {
					con.disconnect();
				} catch (final Exception e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (final Exception e) {
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (final Exception e) {
				}
			}
		}

		return null;
	}

}
