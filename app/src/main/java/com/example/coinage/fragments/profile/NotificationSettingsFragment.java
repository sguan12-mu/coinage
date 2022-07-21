package com.example.coinage.fragments.profile;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.coinage.AlarmReceiver;
import com.example.coinage.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class NotificationSettingsFragment extends Fragment {
    public static final String TAG = "NotificationSettingsFragment";

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    private ConstraintLayout clAlarm;
    private SwitchMaterial notificationSwitch;

    public NotificationSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification_settings, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createNotificationChannel();
        setAlarm();

        notificationSwitch = view.findViewById(R.id.notificationSwitch);
        clAlarm = view.findViewById(R.id.clAlarm);

        notificationSwitch.setChecked(true);
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    unsetAlarm();
                    clAlarm.setVisibility(View.INVISIBLE);
                }
                else {
                    setAlarm();
                    clAlarm.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "CoinageNotificationChannel";
            String description = "Notification Channel for Coinage";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("coinage", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setAlarm() {
        alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);

        // current implementation sets a repeating alarm for every 10 seconds
        // to make the feature easier to test/showcase
        pendingIntent = PendingIntent.getBroadcast(getContext(),0,intent,PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                10000,
                pendingIntent);
        Log.i(TAG, "notification alarm set successfully");
    }

    public void unsetAlarm() {
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext().getApplicationContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getContext(),0,intent,PendingIntent.FLAG_IMMUTABLE);

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            Log.i(TAG, "notification alarm removed successfully");
        }
    }
}