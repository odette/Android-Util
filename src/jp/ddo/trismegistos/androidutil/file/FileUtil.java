package jp.ddo.trismegistos.androidutil.file;

import java.io.File;

import android.os.Environment;

/**
 * @author y_sugasawa
 * @since 2013/01/29
 */
public class FileUtil {

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
}
