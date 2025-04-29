package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    TodayFragment todayFragment;
    private DrawerLayout Layout;
    private NavigationView view;
    private final Map<Integer, Runnable> Actions = new HashMap<>();
    SharedPreference sharedPreference;
    String UserName;
    String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreference = new SharedPreference(this);

        email = getIntent().getStringExtra("userEmail");
        UserName = getIntent().getStringExtra("userName");

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView moonIcon = toolbar.findViewById(R.id.moon_icon);
        moonIcon.setOnClickListener(v -> toggleNightMode());

        Layout = findViewById(R.id.layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, Layout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        Layout.addDrawerListener(toggle);
        toggle.syncState();

        view = findViewById(R.id.nav_view);

        View headerView = view.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.userName);
        TextView userEmailTextView = headerView.findViewById(R.id.email);
        userNameTextView.setText(UserName);
        userEmailTextView.setText(email);

        todayFragment = new TodayFragment();
        loadFragment(todayFragment);

        initializeActions();
        view.setNavigationItemSelectedListener(this::whenItemSelected);
    }

    private void toggleNightMode() {
        boolean currentNightMode = sharedPreference.readDarkMode("Mode",false);
        if (currentNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            sharedPreference.writeDarkMode("Mode", false);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            sharedPreference.writeDarkMode("Mode", true);
        }
    }

    private void initializeActions() {
        Actions.put(R.id.nav_today, () -> loadFragment(todayFragment));
        Actions.put(R.id.nav_new_task, this::goToNewActivity);
        Actions.put(R.id.nav_all, this::goToAllTasksActivity);
        Actions.put(R.id.nav_search, this::goToSearchTasksActivity);
        Actions.put(R.id.nav_completed, this::goToCompletedTasksActivity);
        Actions.put(R.id.nav_get_tasks, this::goToGetTasks);
        Actions.put(R.id.nav_profile, this::getMyProfile);
        Actions.put(R.id.nav_logout, () -> {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        });
    }

    private void getMyProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        String email = sharedPreference.getEmail();
        intent.putExtra("email", sharedPreference.getEmail());
        startActivity(intent);
    }

    private boolean whenItemSelected(@NonNull MenuItem item) {
        Runnable action = Actions.get(item.getItemId());
        if (action != null) {
            action.run();
            Layout.closeDrawers();
            return true;
        }
        return false;
    }

    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
    }

    private void goToGetTasks() {
        Intent intent = new Intent(this, GetTasks.class);
        String email = sharedPreference.getEmail();
        intent.putExtra("email", sharedPreference.getEmail());
        startActivity(intent);
    }

    private void goToCompletedTasksActivity() {
        Intent intent = new Intent(this, CompletedTasksActivity.class);
        String email = sharedPreference.getEmail();
        intent.putExtra("email", sharedPreference.getEmail());
        startActivity(intent);
    }

    private void goToAllTasksActivity() {
        Intent intent = new Intent(this, AllTasksActivity.class);
        String email = sharedPreference.getEmail();
        intent.putExtra("email", sharedPreference.getEmail());
        startActivity(intent);
    }

    private void goToSearchTasksActivity() {
        Intent intent = new Intent(this, SearchTasksActivity.class);
        startActivity(intent);
    }

    private void goToNewActivity() {
        Intent intent = new Intent(this, NewTaskActivity.class);
        String email = sharedPreference.getEmail();
        intent.putExtra("email", sharedPreference.getEmail());
        startActivity(intent);
    }
}
