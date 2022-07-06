package com.library.ekycnetset.base;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.application.bubble.local.PrefUtils;
import com.library.ekycnetset.network.ApiClient;
import com.application.linkodes.network.ApiService;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Objects;
import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Retrofit;

//by : Deepak Kumar
//at : Netset Software
//in : Java

public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    private FragmentTransaction fragmentTransaction;
    private Handler mHandler;

    private Dialog mDialog;
    public Fragment currentFragment;
    private T mViewDataBinding;

    private PrefUtils mPref;

    public ApiService service;
    private CompositeDisposable disposable = new CompositeDisposable();
    public Retrofit retrofitClient;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();

        performDataBinding();
        mPref = new PrefUtils();
    }

    private void performDataBinding() {
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId());
    }

    public void showLoading() {
        hideLoading();
        mDialog = Common.Companion.setLoadingDialog(this);
    }

    public void hideLoading() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.cancel();
        }
    }

    public T getViewDataBinding() {
        return mViewDataBinding;
    }

    /**
     * @return layout resource id
     */
    public abstract
    @LayoutRes
    int getLayoutId();


    public void displayIt(final Fragment mFragment, final String tag, final boolean isBack) {
        mHandler.post(() -> {
            currentFragment = mFragment;
            fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction();

            if (isBack) {
                fragmentTransaction.addToBackStack(tag);
            }

            fragmentTransaction
                    .replace(setContainerLayout(), currentFragment, tag)
                    .commitAllowingStateLoss();
        });

    }

    public Fragment setArguments(final Fragment mFragment, Bundle mBundle) {
        if (mBundle != null) {
            mFragment.setArguments(mBundle);
        }
        return mFragment;
    }


    public abstract int setContainerLayout();


    @Override
    public void onBackStackChanged() {
        FragmentManager localFragmentManager = getSupportFragmentManager();
        int i = localFragmentManager.getBackStackEntryCount();
        if (i == 1 || i == 0) {
            finish();
        } else {
            mHandler.postDelayed(localFragmentManager::popBackStack, 100);
        }
    }

    @Override
    public void onBackPressed() {
        onBackStackChanged();
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
            if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
                int[] scrcoords = new int[2];
                view.getLocationOnScreen(scrcoords);
                float x = ev.getRawX() + view.getLeft() - scrcoords[0];
                float y = ev.getRawY() + view.getTop() - scrcoords[1];
                if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                    ((InputMethodManager) Objects.requireNonNull(this.getSystemService(Context.INPUT_METHOD_SERVICE))).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
            }

        return super.dispatchTouchEvent(ev);
    }

    public CompositeDisposable getDisposable() {
        return disposable;
    }

    public ApiService getApiService() {
        return service;
    }

    public PrefUtils getKycPref() {
        return mPref;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    public void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    class EmojiExcludeFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String specialChars = "@`/*!#$%^&*()\"{}_[]|\\?/<>,.:-'';€¤•§£¥...π¢¶®©₩￦";
            for (int i = start; i < end; i++) {
                int type = Character.getType(source.charAt(i));
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL || type == Character.MATH_SYMBOL || specialChars.contains("" + source) || Character.isWhitespace(0)) {
                    return "";
                }
            }
            return null;
        }
    }

    // Google Drive

    public File getFileFromUri(final Context context, final Uri uri) throws Exception {

//        if (isGoogleDrive(uri)) // check if file selected from google drive
//        {
            return saveFileIntoExternalStorageByUri(context, uri);
//        }else
//            // do your other calculation for the other files and return that file
//            return null;
    }


    private boolean isGoogleDrive(Uri uri)
    {
        return "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    }

    private File saveFileIntoExternalStorageByUri(Context context, Uri uri) throws

            Exception {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        int originalSize = inputStream.available();

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        String fileName = getFileName(context, uri);
        File file = makeEmptyFileIntoExternalStorageWithTitle(fileName);
        bis = new BufferedInputStream(inputStream);
        bos = new BufferedOutputStream(new FileOutputStream(
                file, false));

        byte[] buf = new byte[originalSize];
        bis.read(buf);
        do {
            bos.write(buf);
        } while (bis.read(buf) != -1);

        bos.flush();
        bos.close();
        bis.close();

        return file;

    }

    private String getFileName(Context context, Uri uri)
    {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    private File makeEmptyFileIntoExternalStorageWithTitle(String title) {
        String root =  Environment.getExternalStorageDirectory().getAbsolutePath();
        return new File(root, title);
    }

}

