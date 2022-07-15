package com.example.coinage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.coinage.fragments.AddTransactionFragment;
import com.example.coinage.fragments.TransactionListFragment;
import com.example.coinage.fragments.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// fragment container and bottom navigation activity
public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    public final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                    default:
                        fragment = new TransactionListFragment();
                        break;
                    case R.id.action_add:
                        fragment = new AddTransactionFragment();
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        break;
                }
                fragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out)
                    .replace(R.id.frameLayout, fragment).commit();
                Log.i(TAG, "bottom navigation bar created");
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

}