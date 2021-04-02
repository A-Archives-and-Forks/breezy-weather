package wangdaye.com.geometricweather.main.fragments;

import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;
import androidx.core.widget.ImageViewCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wangdaye.com.geometricweather.R;
import wangdaye.com.geometricweather.common.basic.GeoActivity;
import wangdaye.com.geometricweather.common.basic.GeoFragment;
import wangdaye.com.geometricweather.common.basic.models.Location;
import wangdaye.com.geometricweather.common.ui.adapters.location.LocationAdapter;
import wangdaye.com.geometricweather.common.ui.decotarions.ListDecoration;
import wangdaye.com.geometricweather.common.utils.DisplayUtils;
import wangdaye.com.geometricweather.common.utils.helpers.SnackbarHelper;
import wangdaye.com.geometricweather.databinding.FragmentManagementBinding;
import wangdaye.com.geometricweather.main.MainActivityViewModel;
import wangdaye.com.geometricweather.main.adapters.LocationAdapterAnimWrapper;
import wangdaye.com.geometricweather.main.models.SelectableLocationListResource;
import wangdaye.com.geometricweather.main.utils.MainThemeManager;
import wangdaye.com.geometricweather.main.widgets.LocationItemTouchCallback;

public class ManagementFragment extends GeoFragment
        implements LocationItemTouchCallback.OnSelectProviderActivityStartedCallback {

    private FragmentManagementBinding mBinding;
    private MainActivityViewModel mViewModel;

    private LocationAdapter mAdapter;
    private LocationAdapterAnimWrapper mAdapterAnimWrapper;
    private ItemTouchHelper mItemTouchHelper;
    private ListDecoration mItemDecoration;

    private ValueAnimator mColorAnimator;

    private @Nullable Callback mCallback;

    public static final String KEY_CONTROL_SYSTEM_BAR = "control_system_bar";

    public interface Callback {
        void onSearchBarClicked(View searchBar);
        void onSelectProviderActivityStarted();
    }

    public static ManagementFragment getInstance(boolean controlSystemBar) {
        Bundle b = new Bundle();
        b.putBoolean(KEY_CONTROL_SYSTEM_BAR, controlSystemBar);

        ManagementFragment f = new ManagementFragment();
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentManagementBinding.inflate(getLayoutInflater(), container, false);
        initModel();
        initView();

        setCallback((Callback) requireActivity());

        Bundle b = getArguments();
        if (b != null) {
            boolean controlSystemBar = b.getBoolean(KEY_CONTROL_SYSTEM_BAR, false);
            if (controlSystemBar) {
                DisplayUtils.setSystemBarStyle(requireContext(), requireActivity().getWindow(),
                        false, false, true,
                        mViewModel.getThemeManager().isLightTheme());
            }
        }

        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mItemDecoration != null) {
            mBinding.recyclerView.removeItemDecoration(mItemDecoration);
        }
    }

    @Nullable
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter && nextAnim != 0 && mAdapterAnimWrapper != null) {
            mAdapterAnimWrapper.setLastPosition(-1);
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    private void initModel() {
        mViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
    }

    private void initView() {
        mBinding.searchBar.setOnClickListener(v -> {
            if (mCallback != null) {
                mCallback.onSearchBarClicked(mBinding.searchBar);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBinding.searchBar.setTransitionName(getString(R.string.transition_activity_search_bar));
        }

        mBinding.currentLocationButton.setOnClickListener(v -> {
            mViewModel.addLocation(Location.buildLocal());
            SnackbarHelper.showSnackbar(getString(R.string.feedback_collect_succeed));
        });

        mAdapter = new LocationAdapter(
                requireActivity(),
                new ArrayList<>(),
                null,
                (v, formattedId) -> { // on click.
                    mViewModel.setLocation(formattedId);
                    getParentFragmentManager().popBackStack();
                },
                holder -> mItemTouchHelper.startDrag(holder), // on drag.
                mViewModel.getThemeManager()
        );
        mAdapterAnimWrapper = new LocationAdapterAnimWrapper(requireContext(), mAdapter);
        mAdapterAnimWrapper.setLastPosition(Integer.MAX_VALUE);
        mBinding.recyclerView.setAdapter(mAdapterAnimWrapper);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(
                requireActivity(), RecyclerView.VERTICAL, false));
        mBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy != 0) {
                    mAdapterAnimWrapper.setScrolled();
                }
            }
        });

        mItemDecoration = new ListDecoration(
                requireActivity(),
                mViewModel.getThemeManager().getLineColor(requireActivity())
        );
        mBinding.recyclerView.addItemDecoration(mItemDecoration);

        mItemTouchHelper = new ItemTouchHelper(new LocationItemTouchCallback(
                (GeoActivity) requireActivity(), mViewModel, this));
        mItemTouchHelper.attachToRecyclerView(mBinding.recyclerView);

        mViewModel.getListResource().observe(getViewLifecycleOwner(), resource -> {

            if (resource.source instanceof SelectableLocationListResource.ItemMoved) {
                SelectableLocationListResource.ItemMoved source
                        = (SelectableLocationListResource.ItemMoved) resource.source;
                mAdapter.update(source.from, source.to);
            } else {
                mAdapter.update(resource.dataList, resource.selectedId, resource.forceUpdateId);
            }

            setThemeStyle();
            setCurrentLocationButtonEnabled(resource.dataList);
        });
    }

    private void setThemeStyle() {
        MainThemeManager themeManager = mViewModel.getThemeManager();

        ImageViewCompat.setImageTintList(
                mBinding.searchIcon,
                ColorStateList.valueOf(themeManager.getTextContentColor(requireActivity()))
        );
        ImageViewCompat.setImageTintList(
                mBinding.currentLocationButton,
                ColorStateList.valueOf(themeManager.getTextContentColor(requireActivity()))
        );
        mBinding.title.setTextColor(
                ColorStateList.valueOf(themeManager.getTextSubtitleColor(requireActivity())));

        // background.
        if (mColorAnimator != null) {
            mColorAnimator.cancel();
            mColorAnimator = null;
        }

        final int oldBackgroundColor = mBinding.recyclerView.getBackground() instanceof ColorDrawable
                ? ((ColorDrawable) mBinding.recyclerView.getBackground()).getColor()
                : Color.TRANSPARENT;
        final int newBackgroundColor = themeManager.getRootColor(requireContext());

        final int oldLineColor = mItemDecoration.getColor();
        final int newLineColor = themeManager.getLineColor(requireContext());

        if (newBackgroundColor != oldBackgroundColor || newLineColor != oldLineColor) {
            final float[] progress = new float[1];
            final int[] colors = new int[2];
            mColorAnimator = ValueAnimator.ofFloat(0, 1);
            mColorAnimator.addUpdateListener(animation -> {
                progress[0] = (float) animation.getAnimatedValue();
                colors[0] = DisplayUtils.blendColor(
                        ColorUtils.setAlphaComponent(newBackgroundColor, (int) (255 * progress[0])),
                        oldBackgroundColor
                );
                colors[1] = DisplayUtils.blendColor(
                        ColorUtils.setAlphaComponent(newLineColor, (int) (255 * progress[0])),
                        oldLineColor
                );
                mBinding.searchBar.setCardBackgroundColor(colors[0]);
                mBinding.recyclerView.setBackgroundColor(colors[0]);
                mItemDecoration.setColor(colors[1]);
            });
            mColorAnimator.setDuration(500); // same as 2 * changeDuration of default item animator.
            mColorAnimator.start();
        } else {
            mBinding.searchBar.setCardBackgroundColor(newBackgroundColor);
            mBinding.recyclerView.setBackgroundColor(newBackgroundColor);
            mItemDecoration.setColor(newLineColor);
        }
    }

    private void setCurrentLocationButtonEnabled(List<Location> list) {
        boolean enabled = list.size() != 0;
        for (int i = 0; i < list.size(); i ++) {
            if (list.get(i).isCurrentPosition()) {
                enabled = false;
                break;
            }
        }
        mBinding.currentLocationButton.setEnabled(enabled);
        mBinding.currentLocationButton.setAlpha(enabled ? 1 : .5f);
    }

    public void prepareReenterTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
            mBinding.searchBar.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mBinding.searchBar.getViewTreeObserver().removeOnPreDrawListener(this);
                    startPostponedEnterTransition();
                    return true;
                }
            });
        }
    }

    // interface.

    public void setCallback(Callback l) {
        mCallback = l;
    }

    // on location list changed listener.

    @Override
    public void onSelectProviderActivityStarted() {
        if (mCallback != null) {
            mCallback.onSelectProviderActivityStarted();
        }
    }
}
