package wangdaye.com.geometricweather.common.ui.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import wangdaye.com.geometricweather.R;
import wangdaye.com.geometricweather.common.basic.GeoActivity;
import wangdaye.com.geometricweather.common.basic.models.Location;
import wangdaye.com.geometricweather.common.ui.decotarions.ListDecoration;
import wangdaye.com.geometricweather.common.ui.widgets.insets.both.FitSystemBarRecyclerView;
import wangdaye.com.geometricweather.db.DatabaseHelper;
import wangdaye.com.geometricweather.common.ui.adapters.DailyPollenAdapter;

public class AllergenActivity extends GeoActivity {

    private Location mLocation;
    public static final String KEY_ALLERGEN_ACTIVITY_LOCATION_FORMATTED_ID = "ALLERGEN_ACTIVITY_LOCATION_FORMATTED_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergen);
        initData();
        initWidget();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // do nothing.
    }

    private void initData() {
        String formattedId = getIntent().getStringExtra(KEY_ALLERGEN_ACTIVITY_LOCATION_FORMATTED_ID);
        if (!TextUtils.isEmpty(formattedId)) {
            mLocation = DatabaseHelper.getInstance(this).readLocation(formattedId);
        }
        if (mLocation == null) {
            mLocation = DatabaseHelper.getInstance(this).readLocationList().get(0);
        }
        mLocation.setWeather(DatabaseHelper.getInstance(this).readWeather(mLocation));
    }

    private void initWidget() {
        Toolbar toolbar = findViewById(R.id.activity_allergen_toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        if (mLocation.getWeather() != null) {
            FitSystemBarRecyclerView recyclerView = findViewById(R.id.activity_allergen_recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(new ListDecoration(this, ContextCompat.getColor(this, R.color.colorLine)));
            recyclerView.setAdapter(new DailyPollenAdapter(mLocation.getWeather()));
        } else {
            finish();
        }
    }
}
