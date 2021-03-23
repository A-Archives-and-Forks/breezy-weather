package wangdaye.com.geometricweather.common.basic.models.options;

import android.content.Context;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;

import wangdaye.com.geometricweather.R;
import wangdaye.com.geometricweather.common.basic.models.options._utils.Utils;

public enum NotificationTextColor {

    DARK("dark", R.color.colorTextDark, R.color.colorTextDark2nd),
    GREY("grey", R.color.colorTextGrey, R.color.colorTextGrey2nd),
    LIGHT("light", R.color.colorTextLight, R.color.colorTextLight2nd);

    private final String colorId;
    @ColorRes private final int mainTextColorResId;
    @ColorRes private final int subTextColorResId;

    NotificationTextColor(String colorId, int mainTextColorResId, int subTextColorResId) {
        this.colorId = colorId;
        this.mainTextColorResId = mainTextColorResId;
        this.subTextColorResId = subTextColorResId;
    }

    @Nullable
    public String getNotificationTextColorName(Context context) {
        return Utils.getNameByValue(
                context.getResources(),
                colorId,
                R.array.notification_text_colors,
                R.array.notification_text_color_values
        );
    }

    @ColorRes
    public int getMainTextColorResId() {
        return mainTextColorResId;
    }

    @ColorRes
    public int getSubTextColorResId() {
        return subTextColorResId;
    }

    public static NotificationTextColor getInstance(String value) {
        switch (value) {
            case "light":
                return LIGHT;

            case "grey":
                return GREY;

            default:
                return DARK;

        }
    }
}
