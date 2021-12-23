package com.roshendilan.eventbussample;

import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {
    TextView updateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateTextView = findViewById(R.id.updateTextView);

        //Trigger the service
        scheduleJob(this);
    }

    //SmartJobService provides the implementation for the job
    //Job scheduler will run only once
    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, SmartJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(30 * 1000); // Wait at least 30s
        builder.setOverrideDeadline(60 * 1000); // Maximum delay 60s

        // get the jobScheduler instance from current context
        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    // Prepare the subscriber and run on the main thread
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateTextViewEvent event) {
        updateTextView.setText(event.getText());
        Toast.makeText(this, event.getText(), Toast.LENGTH_SHORT).show();
    };

    //Register subscriber
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    //Unregister subscriber
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}