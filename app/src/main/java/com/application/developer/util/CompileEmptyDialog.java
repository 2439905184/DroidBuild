package com.application.developer.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.application.developer.R;

/**
 * ================================================
 * dialog
 */
public class CompileEmptyDialog extends Dialog {

    private Activity mActivity;
    private AlertParams mDialogAlertParams;
    private View rootView = null;

    public CompileEmptyDialog(@NonNull Context context) {
        super(context);
    }

    public CompileEmptyDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CompileEmptyDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void setParams(AlertParams params) {
        this.mDialogAlertParams = params;
    }

//    private void initView(Context context, int flag, CustomerTagBean data) {
//        View view = LayoutInflater.from(context).inflate(R.layout.main_dialog_welcome_user_ui, null);
//
//    }


    @Override
    public void show() {
        super.show();
        rootView = getWindow().getDecorView();
    }

    public View getContextView() {
        return rootView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mDialogAlertParams.mViewLayoutResId);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mDialogAlertParams.getmContext().getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.82); // 宽度设置为屏幕宽度的80%
        //lp.dimAmount=0.0f;//外围遮罩透明度0.0f-1.0f
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(mDialogAlertParams.mGravity);//内围区域底部显示
    }

    public static class Builder {
        private AlertParams mAlertParams;


        public Builder(Context context) {
            this(context, R.style.DiyDialogStyle);
        }

        public Builder(Context context, int themeRes) {
            mAlertParams = new AlertParams(context, themeRes);
        }

        public Builder setContentView(View view) {
            mAlertParams.mView = view;
            mAlertParams.mViewLayoutResId = 0;
            return this;
        }

        public Builder setAlpha(float alpha) {
            mAlertParams.alpha = alpha;
            return this;
        }

        public Builder setContentView(int layoutResId) {
            mAlertParams.mView = null;
            mAlertParams.mViewLayoutResId = layoutResId;
            return this;
        }

        public Builder setFullWidth() {
            mAlertParams.mWidth = ViewGroup.LayoutParams.MATCH_PARENT;
            return this;
        }

        public Builder setWidthAndHeight(int width, int height) {
            mAlertParams.mWidth = width;
            mAlertParams.mHeight = height;
            return this;
        }

        public Builder setOnClickListener(View.OnClickListener onJAlertDialogClickListener) {
            mAlertParams.mOnClickListener = onJAlertDialogClickListener;
            return this;
        }

        public Builder setOnOnDismissListener(OnDismissListener onDismissListener) {
            mAlertParams.mOnDismissListener = onDismissListener;
            return this;
        }

        public Builder setGravity(int gravity) {
            mAlertParams.mGravity = gravity;
            return this;
        }

        public CompileEmptyDialog build() {
            final CompileEmptyDialog dialog = new CompileEmptyDialog(mAlertParams.mContext, mAlertParams.mThemeRes);
            dialog.setParams(mAlertParams);
            return dialog;
        }
    }

    public static class AlertParams {
        private Context mContext;
        private int mThemeRes;
        private View mView;
        /**
         * 点击空白是否可以取消,默认不可以
         */
        public boolean mCancelable = false;
        private int mViewLayoutResId;
        public int mWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        public int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        public int mGravity = Gravity.CENTER;
        public float widthPercentage;
        public float alpha;
        /**
         * Dialog 取消监听
         */
        public View.OnClickListener mOnClickListener;
        public OnDismissListener mOnDismissListener;

        public AlertParams() {

        }

        public AlertParams(Context mContext, int mThemeRes) {
            this.mContext = mContext;
            this.mThemeRes = mThemeRes;
        }

        public Context getmContext() {
            return mContext;
        }

        public void setContext(Context mContext) {
            this.mContext = mContext;
        }

        public int getThemeRes() {
            return mThemeRes;
        }

        public void setThemeRes(int mThemeRes) {
            this.mThemeRes = mThemeRes;
        }

        public View getView() {
            return mView;
        }

        public void setView(View mView) {
            this.mView = mView;
        }

        public int getViewLayoutResId() {
            return mViewLayoutResId;
        }

        public void setViewLayoutResId(int mViewLayoutResId) {
            this.mViewLayoutResId = mViewLayoutResId;
        }
    }


}
