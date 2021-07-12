package com.rabunabi.friends.view.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.rabunabi.friends.R;

/**
 * Created by DUC on 3/29/2020.
 */

public abstract class BaseContainerFragment extends Fragment {
    private static final String PREFIX_TAG = "TAG_";
    private static final String TAG = BaseContainerFragment.class.getSimpleName();
    protected Fragment mFirstFragment;

    protected Map<Class<? extends Fragment>, Fragment> mFragmentMap = new HashMap<>();
    protected Class<? extends Fragment> mFragmentClass = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_container, container, false);
        mFirstFragment = onCreateFirstFragment();
        replaceChild(mFirstFragment);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    protected abstract Fragment onCreateFirstFragment();

    /**
     * add child fragment, default add back stack
     *
     * @param fragment
     */
    public void addChild(Fragment fragment) {
        addChild(fragment, true);
    }

    /**
     * @param fragment       : fragment to add into container
     * @param addToBackStack
     */
    public void addChild(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                R.anim.enter_from_left, R.anim.exit_to_right);

        // Add Index as Tag
        String tag = getTag(getFragmentCount());
        transaction.add(R.id.main_frame, fragment, tag);

        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
    }

    /**
     * replace fragment to container, not add to back stack
     *
     * @param fragment
     */
    public void replaceChild(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        // Add Index as Tag
        String tag = getTag(getFragmentCount());
        transaction.replace(R.id.main_frame, fragment, tag);

        transaction.commit();
    }

    /**
     * get total count fragment in backstack
     *
     * @return : count
     */
    public int getFragmentCount() {
        return getChildFragmentManager().getBackStackEntryCount();
    }

    /**
     * get fragment with index (tag)
     *
     * @param index
     * @return
     */
    public Fragment getFragmentAt(int index) {
        return getFragmentCount() > 0 ? getChildFragmentManager().findFragmentByTag(getTag(index)) : null;
    }

    public Fragment getCurrentFragment() {
        return getFragmentAt(getFragmentCount() - 1);
    }

    private String getTag(int index) {
        return PREFIX_TAG + index;
    }

    /**
     * pop all fragment in backStack
     */
    public void popAllBackstack() {
        getChildFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * pop fragment on top stack
     */
    public void popFragment() {
        getChildFragmentManager().popBackStack();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onDetach() {
        super.onDetach();

        // This to try fix Activity has been destroyed bug
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public Fragment getFragment(FragmentTransaction transaction,
                                Class<? extends Fragment> fragmentClass,
                                @Nullable Bundle args) {
        if (mFragmentMap.containsKey(fragmentClass)) {
            return mFragmentMap.get(fragmentClass);
        }
        try {
            Fragment fragment = fragmentClass.newInstance();
            if (args != null) {
                fragment.setArguments(args);
            }
            transaction.add(R.id.frame_container, fragment);
            mFragmentMap.put(fragmentClass, fragment);
            return fragment;
        } catch (InstantiationException | IllegalAccessException | java.lang.InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param fragmentClass
     */
    public void showFragment(Class<? extends Fragment> fragmentClass, @Nullable Bundle bundle) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                R.anim.enter_from_left, R.anim.exit_to_right);

        Fragment fragment = getFragment(transaction, fragmentClass, bundle);

        for (Fragment frg : mFragmentMap.values()) {
            transaction.hide(frg);
        }
        transaction.show(fragment);
        transaction.commit();
    }
}

