package com.example.expensetracker.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.expensetracker.R;
import com.example.expensetracker.databinding.FragmentPartiesAndAccountsBinding;
import com.example.expensetracker.views.adapters.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class PartiesAndAccountsFragment extends Fragment {
    FragmentPartiesAndAccountsBinding binding;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    ViewPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_parties_and_accounts, container, false);
        viewPager = binding.viewPager;
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new PartiesFragment());
        fragments.add(new AccountsFragment());
        adapter = new ViewPagerAdapter(getChildFragmentManager(), getLifecycle(), fragments);
        viewPager.setAdapter(adapter);
        tabLayout = binding.tabLayout;

        new TabLayoutMediator(
                tabLayout,
                viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position){
                            case 0:{
                                tab.setText("Parties");
                                break;
                            }
                            case 1:{
                                tab.setText("Accounts");
                            }
                        }
                    }
                }
        ).attach();



        return binding.getRoot();
    }
}