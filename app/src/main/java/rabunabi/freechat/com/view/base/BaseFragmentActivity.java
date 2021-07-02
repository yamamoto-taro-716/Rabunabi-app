package rabunabi.freechat.com.view.base;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.HashMap;
import java.util.Map;

import rabunabi.freechat.com.R;
import rabunabi.freechat.com.view.home.ui.messsage.MessageFragment;

public abstract class BaseFragmentActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {
    protected int mCurrentPosition = 0;
    protected Map<Class<? extends Fragment>, Fragment> mFragmentMap = new HashMap<>();
    protected Class<? extends Fragment> mFragmentClass = null;
    protected Bundle bundleArgs;

    /**
     * @param fragmentClass
     */
    protected void showFragment(Class<? extends Fragment> fragmentClass, int position, @Nullable Bundle bundle) {
        if (position == 0 && bundle != null) {// profile
            bundle.putString("tab","tabProfile");
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (mCurrentPosition < position) {
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                    R.anim.enter_from_left, R.anim.exit_to_right);
        } else {
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right,
                    R.anim.enter_from_right, R.anim.exit_to_left);
        }

        Fragment fragment = getFragment(transaction, fragmentClass, bundle);
        if (fragment instanceof MessageFragment) {// profile
            MessageFragment mf = (MessageFragment) fragment;
            mf.onResume();
        }
        for (Fragment frg : mFragmentMap.values()) {
            transaction.hide(frg);
        }
        transaction.show(fragment);
        transaction.commit();
        mCurrentPosition = position;
    }

    protected abstract void initFragments();

    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected Fragment getFragment(FragmentTransaction transaction,
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
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    protected void commitFragmentTransaction(FragmentTransaction fragmentTransaction) {
        if (!isFinishing()) {
            fragmentTransaction.commit();
        }
    }

}
