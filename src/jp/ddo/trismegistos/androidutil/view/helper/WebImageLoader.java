
package jp.ddo.trismegistos.androidutil.view.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import jp.ddo.trismegistos.androidutil.file.FileUtil;
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

    /** タグ。 */
    private static final String TAG = WebImageLoader.class.getSimpleName();

    /** キャッシュ管理オブジェクト。 */
    private ImageCache imageCache;

    /** 画像URL。 */
    private String url;

    /** ローカルファイルディレクトリ。 */
    private File localDir;

    /**
     * コンストラクタ。<br>
     * imageCacheがnullの場合はキャッシュを行わない。
     * 
     * @param context コンテキスト
     * @param url 画像URL
     * @param imageCache キャッシュ管理オブジェクト
     */
    public WebImageLoader(final Context context, final String url, final ImageCache imageCache) {
        super(context);
        this.url = url;
        this.imageCache = imageCache;
    }

    /**
     * コンストラクタ。<br>
     * 指定したファイルパスに画像を保存する。
     * 
     * @param context コンテキスト
     * @param url 画像URL
     * @param localDir ローカルディレクトリ
     */
    public WebImageLoader(final Context context, final String url, final File localDir) {
        super(context);
        this.url = url;
        this.localDir = localDir;
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
            if (localDir != null) {
                final byte[] buf = new byte[1024];
                int len = 0;
                FileUtil.mkdir(localDir);
                final File localFile = new File(localDir.getAbsolutePath()
                        + FileUtil.FILE_SEPARATOR + getFileName(url));
                fos = new FileOutputStream(localFile);
                while ((len = in.read(buf)) > -1) {
                    fos.write(buf, 0, len);
                }
                fos.flush();
                bitmap = BitmapFactory.decodeFile(localFile.getPath());
            } else if (imageCache != null) {
                if (imageCache.isFileCache()) {
                    final byte[] buf = new byte[1024];
                    int len = 0;
                    final File localFile = new File(imageCache.getCacheDir(),
                            ImageCache.getFileName(url));
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
                imageCache.saveBitmap(url, bitmap);
            } else {
                bitmap = BitmapFactory.decodeStream(in);
            }
            return bitmap;
        } catch (final Exception e) {
            Log.e(TAG, "" + e.getMessage());
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

    /**
     * URLから画像ファイル名を取得する。<br>
     * 拡張子を含まない場合は"jpg"を付けて返却する。
     * 
     * @param url 画像URL
     * @return 画像ファイル名
     */
    private String getFileName(final String url) {
        final StringBuilder sb = new StringBuilder(url.substring(url.lastIndexOf("/") + 1));
        if (sb.indexOf(".") == -1) {
            sb.append(".jpg");
        }
        return sb.toString();
    }
}
