package wangdaye.com.geometricweather.main.adapters;

import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wangdaye.com.geometricweather.R;
import wangdaye.com.geometricweather.common.basic.models.options.unit.CloudCoverUnit;
import wangdaye.com.geometricweather.common.basic.models.options.unit.RelativeHumidityUnit;
import wangdaye.com.geometricweather.common.basic.models.options.unit.SpeedUnit;
import wangdaye.com.geometricweather.common.basic.models.weather.Weather;
import wangdaye.com.geometricweather.main.utils.MainThemeManager;
import wangdaye.com.geometricweather.settings.SettingsManager;

/**
 * Details adapter.
 * */

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.ViewHolder> {

    private final List<Index> mIndexList;
    private final MainThemeManager mThemeManager;

    private static class Index {
        @DrawableRes int iconId;
        String title;
        String content;
        String talkBack;

        Index(@DrawableRes int iconId, String title, String content) {
            this.iconId = iconId;
            this.title = title;
            this.content = content;
            this.talkBack = title + ", " + content;
        }

        Index(@DrawableRes int iconId, String title, String content, String talkBack) {
            this.iconId = iconId;
            this.title = title;
            this.content = content;
            this.talkBack = talkBack;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatImageView mIcon;
        private final TextView mTitle;
        private final TextView mContent;

        ViewHolder(View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.item_details_icon);
            mTitle = itemView.findViewById(R.id.item_details_title);
            mContent = itemView.findViewById(R.id.item_details_content);
        }

        void onBindView(Index index) {
            Context context = itemView.getContext();

            itemView.setContentDescription(index.talkBack);

            mIcon.setImageResource(index.iconId);
            ImageViewCompat.setImageTintList(
                    mIcon,
                    ColorStateList.valueOf(mThemeManager.getTextContentColor(context))
            );

            mTitle.setText(index.title);
            mTitle.setTextColor(mThemeManager.getTextContentColor(context));

            mContent.setText(index.content);
            mContent.setTextColor(mThemeManager.getTextSubtitleColor(context));
        }
    }

    public DetailsAdapter(Context context, @NonNull Weather weather, MainThemeManager themeManager) {
        mIndexList = new ArrayList<>();
        SettingsManager settings = SettingsManager.getInstance(context);

        SpeedUnit speedUnit = settings.getSpeedUnit();
        String windTitle = context.getString(R.string.live)
                + " : "
                + weather.getCurrent().getWind().getWindDescription(context, speedUnit);
        String windContent = context.getString(R.string.daytime)
                + " : "
                + weather.getDailyForecast().get(0).day().getWind().getWindDescription(context, speedUnit)
                + "\n"
                + context.getString(R.string.nighttime)
                + " : "
                + weather.getDailyForecast().get(0).night().getWind().getWindDescription(context, speedUnit);
        mIndexList.add(
                new Index(
                        R.drawable.ic_wind,
                        windTitle,
                        windContent,
                        context.getString(R.string.wind)
                                + ", " + windTitle
                                + ", " + windContent.replace("\n", ", ")
                )
        );

        if (weather.getCurrent().getRelativeHumidity() != null) {
            mIndexList.add(
                    new Index(
                            R.drawable.ic_water_percent,
                            context.getString(R.string.humidity),
                            RelativeHumidityUnit.PERCENT.getRelativeHumidityText(
                                    weather.getCurrent().getRelativeHumidity()
                            )
                    )
            );
        }

        if (weather.getCurrent().getUV().isValid()) {
            mIndexList.add(
                    new Index(
                            R.drawable.ic_uv,
                            context.getString(R.string.uv_index),
                            weather.getCurrent().getUV().getUVDescription()
                    )
            );
        }

        if (weather.getCurrent().getPressure() != null) {
            mIndexList.add(
                    new Index(
                            R.drawable.ic_gauge,
                            context.getString(R.string.pressure),
                            settings.getPressureUnit().getPressureText(context, weather.getCurrent().getPressure()),
                            context.getString(R.string.pressure)
                                    + ", " + settings.getPressureUnit().getPressureVoice(context, weather.getCurrent().getPressure())
                    )
            );
        }

        if (weather.getCurrent().getVisibility() != null) {
            mIndexList.add(
                    new Index(
                            R.drawable.ic_eye,
                            context.getString(R.string.visibility),
                            settings.getDistanceUnit().getDistanceText(context, weather.getCurrent().getVisibility()),
                            context.getString(R.string.visibility)
                                    + ", " + settings.getDistanceUnit().getDistanceVoice(context, weather.getCurrent().getVisibility())
                    )
            );
        }

        if (weather.getCurrent().getDewPoint() != null) {
            mIndexList.add(
                    new Index(
                            R.drawable.ic_water,
                            context.getString(R.string.dew_point),
                            settings.getTemperatureUnit().getTemperatureText(
                                    context,
                                    weather.getCurrent().getDewPoint()
                            )
                    )
            );
        }

        if (weather.getCurrent().getCloudCover() != null) {
            mIndexList.add(
                    new Index(
                            R.drawable.ic_cloud,
                            context.getString(R.string.cloud_cover),
                            CloudCoverUnit.PERCENT.getCloudCoverText(
                                    weather.getCurrent().getCloudCover()
                            )
                    )
            );
        }

        if (weather.getCurrent().getCeiling() != null) {
            mIndexList.add(
                    new Index(
                            R.drawable.ic_top,
                            context.getString(R.string.ceiling),
                            settings.getDistanceUnit().getDistanceText(
                                    context,
                                    weather.getCurrent().getCeiling()
                            ),
                            context.getString(R.string.ceiling) + ", " + settings.getDistanceUnit().getDistanceVoice(
                                    context, weather.getCurrent().getCeiling())
                    )
            );
        }

        mThemeManager = themeManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBindView(mIndexList.get(position));
    }

    @Override
    public int getItemCount() {
        return mIndexList.size();
    }
}