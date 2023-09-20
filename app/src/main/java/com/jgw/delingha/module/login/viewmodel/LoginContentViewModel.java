package com.jgw.delingha.module.login.viewmodel;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.jgw.common_library.base.ui.BaseFragment;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.delingha.module.login.ui.LoginFragment;
import com.jgw.delingha.module.login.ui.SelectOrgSystemFragment;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class LoginContentViewModel extends BaseViewModel {

    private int lastPage = -1;
    private List<BaseFragment> mFragments;
    private ArrayList<String> tags;
    private LoginFragment mLoginFragment;
    private SelectOrgSystemFragment mSelectOrgSysFragment;

    public LoginContentViewModel(@NonNull Application application) {
        super(application);
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public void initFragments(FragmentManager fm) {

        tags = new ArrayList<>();
        String Login = "mLoginFragment";
        tags.add(Login);
        String SelectOrgSys = "mSelectOrgSysFragment";
        tags.add(SelectOrgSys);
        mFragments = new ArrayList<>();
        if (mLoginFragment == null) {
            LoginFragment loginFragmentByTag = (LoginFragment) fm.findFragmentByTag(tags.get(0));
            if (loginFragmentByTag == null) {
                mLoginFragment = new LoginFragment();
            } else {
                mLoginFragment = loginFragmentByTag;
            }
        }
        mFragments.add(mLoginFragment);

        if (mSelectOrgSysFragment == null) {
            SelectOrgSystemFragment selectOrgSystemFragmentByTag = (SelectOrgSystemFragment) fm.findFragmentByTag(tags.get(1));
            if (selectOrgSystemFragmentByTag == null) {
                mSelectOrgSysFragment = new SelectOrgSystemFragment();
            } else {
                mSelectOrgSysFragment = selectOrgSystemFragmentByTag;
            }
        }
        mFragments.add(mSelectOrgSysFragment);

    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public List<BaseFragment> getFragments() {
        return mFragments;
    }

    public void setTempToken(String token) {
        Bundle bundle = new Bundle();
        bundle.putString("tempToken", token);
        mSelectOrgSysFragment.setArguments(bundle);
    }

}
