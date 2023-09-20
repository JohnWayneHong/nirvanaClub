package com.jgw.delingha.custom_module.delingha.breed.in.details;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import androidx.fragment.app.Fragment;

import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.BreedInDetailsBean;

import com.jgw.delingha.common.BaseScanActivity;
import com.jgw.delingha.databinding.ActivityCommonBreedDetailsBinding;


import java.util.ArrayList;

/**
 * @Author CJM
 * @Date 2023/6/14 09:32
 * @Description 养殖进场 详情记录 Activity
 */
public class BreedInDetailsActivity extends BaseScanActivity<BreedInDetailsViewModel, ActivityCommonBreedDetailsBinding> {

    private int lastSelectTab = -1;
    private ArrayList<String> tags;
    private ArrayList<Fragment> fragments;
    private BreedInDetailsFragment mBreedInDetailsFragment;
    private BreedInDetailsEarFragment mBreedInDetailsEarFragment;

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        setTitle("详情");

        tags = new ArrayList<>();
        tags.add("mBreedInDetailsFragment");
        tags.add("mBreedInDetailsEarFragment");

        fragments = new ArrayList<>();
        if (mBreedInDetailsFragment == null) {
            BreedInDetailsFragment fragmentByTag = (BreedInDetailsFragment) fm.findFragmentByTag(tags.get(0));
            if (fragmentByTag == null) {
                mBreedInDetailsFragment = new BreedInDetailsFragment();
            } else {
                mBreedInDetailsFragment = fragmentByTag;
            }
        }
        fragments.add(mBreedInDetailsFragment);

        if (mBreedInDetailsEarFragment == null) {
            BreedInDetailsEarFragment fragmentByTag = (BreedInDetailsEarFragment) fm.findFragmentByTag(tags.get(1));
            if (fragmentByTag == null) {
                mBreedInDetailsEarFragment = new BreedInDetailsEarFragment();
            } else {
                mBreedInDetailsEarFragment = fragmentByTag;
            }
        }
        fragments.add(mBreedInDetailsEarFragment);

        jumpToFragment(0);

    }

    @Override
    public void initLiveData() {

    }
    public void jumpToFragment(int index) {
        if (lastSelectTab != -1 && lastSelectTab != index) {
            Fragment baseFragment = fragments.get(lastSelectTab);
            fm.beginTransaction().hide(baseFragment).commitAllowingStateLoss();
        }
        String tag = tags.get(index);
        if (!fragments.get(index).isAdded() && null == fm.findFragmentByTag(tag)) {
            fm.beginTransaction().add(R.id.fl_breed_details_content, fragments.get(index), tag).addToBackStack(null).commitAllowingStateLoss();
        }
        fm.beginTransaction().show(fragments.get(index)).commitAllowingStateLoss();
        lastSelectTab = index;

        switch (index) {
            case 0:
                mBindingView.tvBreedDetailsList.setTypeface(null, Typeface.BOLD);
                mBindingView.tvBreedDetailsEar.setTypeface(null, Typeface.NORMAL);
                mBindingView.vBreedDetailsList.setVisibility(View.VISIBLE);
                mBindingView.vBreedDetailsEar.setVisibility(View.GONE);
                break;
            case 1:
                mBindingView.tvBreedDetailsEar.setTypeface(null, Typeface.BOLD);
                mBindingView.tvBreedDetailsList.setTypeface(null, Typeface.NORMAL);
                mBindingView.vBreedDetailsEar.setVisibility(View.VISIBLE);
                mBindingView.vBreedDetailsList.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.rlBreedDetailsList)
                .addView(mBindingView.rlBreedDetailsEar)
                .addOnClickListener()
                .submit();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == mBindingView.rlBreedDetailsList.getId()) {
            jumpToFragment(0);
        }else if (view.getId() == mBindingView.rlBreedDetailsEar.getId()) {
            jumpToFragment(1);
        }
    }
    @Override
    public void onBackPressed() {
        finish();
    }

    public static void start(Activity context, BreedInDetailsBean data) {
        if (isActivityNotFinished(context)) {
            MMKVUtils.saveTempData(data);
            Intent intent = new Intent(context, BreedInDetailsActivity.class);
            context.startActivity(intent);
        }
    }

}
