package jp.ddo.trismegistos.androidutil.file;

import java.io.File;

import android.os.Environment;
import android.util.Log;

/**
 * ファイル操作のUtilクラス。
 * 
 * @author y_sugasawa
 * @since 2013/01/29
 */
public class FileUtil {

	/** タグ。 */
	private static final String TAG = FileUtil.class.getSimpleName();

	/**
	 * プライベートコンストラクタ。
	 */
	private FileUtil() {
	}

	/**
	 * SDカードのパスを取得する。
	 * 
	 * @return SDカードのパス
	 */
	public static String getSdCardPath() {
		final File file = Environment.getExternalStorageDirectory();
		return file.getPath();
	}

	/**
	 * 指定したディレクトリを作成する。
	 * 
	 * @param dir ディレクトリのパス
	 * @return ディレクトリが既に存在している、またディレクトリ作成に成功した場合にtrue。それ以外はfalse。
	 */
	public static boolean mkdir(final File dir) {
		if (dir.isDirectory() == false) {
			if (dir.isFile()) {
				return false;
			}
			return dir.mkdirs();
		}
		return true;
	}

	/**
	 * 指定したファイルorディレクトリを削除する。
	 * 
	 * @param file
	 * @return 全て削除成功ならばtrue、それ以外はfalse
	 */
	public static boolean deleteAll(final File file) {
		boolean isSuccess = true;
		if (file.isFile()) {
			if (file.delete() == false) {
				Log.e(TAG, "DELETE file is Failed. path is " + file.getAbsolutePath());
				isSuccess = false;
			}
		} else if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				if (deleteAll(f) == false) {
					isSuccess = false;
				}
			}
			if (file.delete() == false) {
				Log.e(TAG, "DELETE file is Failed. path is " + file.getAbsolutePath());
				isSuccess = false;
			}
		}
		return isSuccess;
	}
}
