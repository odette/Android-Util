
package jp.ddo.trismegistos.androidutil.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 * AsyncTaskLoaderの拡張クラス。
 * 
 * @author y_sugasawa
 * @since 2013/01/28
 * @param <T>
 */
public abstract class AbstractAsyncTaskLoader<T> extends AsyncTaskLoader<T> {

    private T data;
    private T result;

    public AbstractAsyncTaskLoader(Context context) {
        super(context);
    }

    @Override
    public T loadInBackground() {
        data = load();
        return data;
    }

    /**
     * バックグラウンド処理。
     * 
     * @return
     */
    abstract protected T load();

    /**
     * {@inheritDoc}
     */
    @Override
    public void deliverResult(T data) {
        if (isReset()) {
            if (result != null) {
                result = null;
            }
            return;
        }

        result = data;

        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStartLoading() {
        if (result != null) {
            deliverResult(result);
        }
        if (takeContentChanged() || result == null) {
            forceLoad();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        cancelLoad();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        data = null;
    }
}
