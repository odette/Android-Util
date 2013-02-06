package jp.ddo.trismegistos.androidutil.ui.helper;

import java.io.File;

import jp.ddo.trismegistos.androidutil.file.FileUtil;
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

	/** タグ。 */
	private static final String TAG = ImageCache.class.getSimpleName();

	/** デフォルトキャッシュサイズ 50MB */
	private static final int DEFAULT_CACHE_SIZE = 50 * 1024 * 1024;

	/** キャッシュオブジェクト。 */
	private LruCache<String, Bitmap> cache;

	/** ファイルキャッシュディレクトリ。 */
	private File cacheDir;

	/**
	 * コンストラクタ。<br>
	 * キャッシュサイズを50MBとしてキャッシュを作成する。
	 * 
	 * @param cacheDir ファイルキャッシュディレクトリ
	 */
	public ImageCache(final File cacheDir) {
		this.cacheDir = cacheDir;
		cache = new Cache(DEFAULT_CACHE_SIZE);
		createCacheDir();
	}

	/**
	 * コンストラクタ。<br>
	 * 指定したキャッシュサイズでキャッシュを作成する。
	 * 
	 * @param cacheDir ファイルキャッシュディレクトリ
	 * @param cacheSize キャッシュサイズ(Byte)
	 */
	public ImageCache(final File cacheDir, int cacheSize) {
		this.cacheDir = cacheDir;
		cache = new Cache(cacheSize);
		createCacheDir();
	}

	/**
	 * キャッシュディレクトリを作成する。<br>
	 * 作成に失敗した場合は、ファイルキャッシュなしとして処理を続行するようにしている。
	 */
	private void createCacheDir() {
		if (cacheDir != null) {
			if (FileUtil.mkdir(cacheDir) == false) {
				Log.w(TAG, "CREATE CacheDir is Failed. CacheDir is " + cacheDir.getAbsolutePath());
				cacheDir = null;
			}
		}
	}

	/**
	 * Bitmapをメモリキャッシュする。
	 * 
	 * @param url 画像のURL
	 * @param bitmap 画像のBitmap
	 */
	public void saveBitmap(final String url, final Bitmap bitmap) {
		cache.put(url, bitmap);
	}

	/**
	 * キャッシュから画像のBitmapを取得する。<br>
	 * メモリキャッシュ→ファイルキャッシュの順に取得する。
	 * 
	 * @param url 元画像ファイルのURL
	 * @return 画像のBitmap
	 */
	public Bitmap getImage(final String url) {

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

	/**
	 * メモリキャッシュを削除する。
	 */
	public void memoryCacheClear() {
		cache.evictAll();
	}

	/**
	 * ファイルキャッシュを削除する。
	 */
	public void deleteAll() {
		if (cacheDir != null && cacheDir.isDirectory() == false) {
			return;
		}

		final File[] files = cacheDir.listFiles();
		for (final File file : files) {
			if (file.isFile()) {
				if (!file.delete()) {
					Log.v(TAG, "file delete false");
				}
			}
		}
	}

	/**
	 * ファイルキャッシュディレクトリのサイズを取得する。
	 * 
	 * @return ファイルキャッシュディレクトリのサイズ
	 */
	public long dirSize() {
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

	/**
	 * ファイル名を取得する。
	 * 
	 * @param url 画像のURL
	 * @return 画像ファイル名。
	 */
	public static String getFileName(final String url) {
		return String.valueOf(url.hashCode());
	}

	/**
	 * ファイルキャッシュが可能かどうかを返す。
	 * 
	 * @return ファイルキャッシュが可能ならばtrue, 不可能ならばfalse
	 */
	public boolean isFileCache() {
		return cacheDir != null;
	}

	/**
	 * キャッシュディレクトリを取得する。
	 * 
	 * @return キャッシュディレクトリ
	 */
	public File getCacheDir() {
		return cacheDir;
	}

	/**
	 * SupportLibraryのLruCacheのバグを修正したLruCacheクラス。
	 * 
	 * @author y_sugasawa
	 * @since 2013/02/06
	 */
	class Cache extends LruCache<String, Bitmap> {

		/**
		 * コンストラクタ。
		 * 
		 * @param maxSize 最大キャッシュサイズ
		 */
		public Cache(int maxSize) {
			super(maxSize);
		}

		@Override
		protected int sizeOf(final String key, final Bitmap value) {
			return value.getRowBytes() * value.getHeight();
		}

	}
}
