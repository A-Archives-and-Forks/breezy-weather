package wangdaye.com.geometricweather.common.ui.widgets.insets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import wangdaye.com.geometricweather.R;
import wangdaye.com.geometricweather.common.utils.DisplayUtils;

public class FitSystemBarRecyclerView extends RecyclerView {

    private Rect mWindowInsets = new Rect(0, 0, 0, 0);
    private boolean mAdaptiveWidthEnabled = true;
    private int mFitSide;

    public static final int SIDE_NONE = 0;
    public static final int SIDE_TOP = 1;
    public static final int SIDE_BOTTOM = 2;
    public static final int SIDE_BOTH = 3;

    public FitSystemBarRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public FitSystemBarRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FitSystemBarRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.FitSystemBarRecyclerView, defStyleAttr, 0);
        mFitSide = a.getInt(R.styleable.FitSystemBarRecyclerView_rv_side, SIDE_BOTH);
        a.recycle();

        ViewCompat.setOnApplyWindowInsetsListener(this, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public void setOnApplyWindowInsetsListener(OnApplyWindowInsetsListener listener) {
        super.setOnApplyWindowInsetsListener((v, insets) -> {
            if (listener != null) {
                return listener.onApplyWindowInsets(v, insets);
            }

            Rect waterfull = Utils.getWaterfullInsets(insets);
            fitSystemWindows(
                    new Rect(
                            insets.getSystemWindowInsetLeft() + waterfull.left,
                            insets.getSystemWindowInsetTop() + waterfull.top,
                            insets.getSystemWindowInsetRight() + waterfull.right,
                            insets.getSystemWindowInsetBottom() + waterfull.bottom
                    )
            );
            return insets;
        });
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        mWindowInsets = insets;
        requestLayout();
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int viewWidth = getMeasuredWidth();
        int adaptiveWidth = DisplayUtils.getTabletListAdaptiveWidth(getContext(), viewWidth);
        int paddingHorizontal = mAdaptiveWidthEnabled ? ((viewWidth - adaptiveWidth) / 2) : 0;
        setPadding(
                paddingHorizontal,
                (mFitSide == SIDE_TOP || mFitSide == SIDE_BOTH) ? mWindowInsets.top : 0,
                paddingHorizontal,
                (mFitSide == SIDE_BOTTOM || mFitSide == SIDE_BOTH) ? mWindowInsets.bottom : 0
        );
    }

    public Rect getWindowInsets() {
        return mWindowInsets;
    }

    public void setAdaptiveWidthEnabled(boolean enabled) {
        mAdaptiveWidthEnabled = enabled;
        requestLayout();
    }

    public void setFitSide(int side) {
        mFitSide = side;
        requestLayout();
    }
}
