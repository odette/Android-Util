package jp.ddo.trismegistos.androidutil.ui.helper;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * 画像ファイルのキャッシュを管理するクラス。
 * 
 * @author y_sugasawa
 * @since 2013/01/28
 */
public class ImageCache {

	private static final String TAG = ImageCache.class.getSimpleName();

	private static final int DEFAULT_CACHE_SIZE = 50 * 1024 * 1024;

	/**
	 * プライベートコンストラクタ。
	 */
	private ImageCache() {
	}

	private static LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(DEFAULT_CACHE_SIZE) {
		@Override
		protected int sizeOf(final String key, final Bitmap value) {
			return value.getRowBytes() * value.getHeight();
		}
	};

	public static String getFileName(final String url) {
		int hash = url.hashCode();
		return String.valueOf(hash);
	}

	public static void saveBitmap(final File cacheDir, final String url, final Bitmap bitmap) {
		cache.put(url, bitmap);
	}

	public static Bitmap getImage(final File cacheDir, final String url) {

		final Bitmap bm = cache.get(url);
		if (bm != null) {
			return bm;
		}

		if (cacheDir == null) {
			return null;
		}

		final String fileName = getFileName(url);
		final File localFile = new File(cacheDir, fileName);
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeFile(localFile.getPath());
		} catch (final Exception e) {
			Log.e(TAG, e.getMessage());
		} catch (final OutOfMemoryError e) {
			Log.e(TAG, e.getMessage());
			cache.evictAll();
		}
		return bitmap;
	}

	public static void memoryCacheClear() {
		cache.evictAll();
	}

	public static void deleteAll(final File cacheDir) {
		if (!cacheDir.isDirectory()) {
			return;
		}
		final File[] files = cacheDir.listFiles();
		for (final File file : files) {
			if (file.isFile()) {
				if (!file.delete()) {
					Log.v("file", "file delete false");
				}
			}
		}
	}

	public static long dirSize(final File cacheDir) {
		long size = 0L;
		if (cacheDir == null) {
			return size;
		}
		if (cacheDir.isDirectory()) {
			for (final File file : cacheDir.listFiles()) {
				size += file.length();
			}
		} else {
			size = cacheDir.length();
		}
		return size;
	}

	public static void setCacheFileSize(int size) {
		cache = new LruCache<String, Bitmap>(size == 0 ? DEFAULT_CACHE_SIZE : size) {
			@Override
			protected int sizeOf(final String key, final Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};
	}
}
