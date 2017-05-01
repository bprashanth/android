package com.example.beeps.fgclient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.example.beeps.bgservice.BgServiceApi;

public class MainActivity extends AppCompatActivity {
  String TAG = "FgClient2";
  private BgServiceApi apiClient;
  private Handler handler;
  private TextView textView;

  private ServiceConnection serviceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      Log.d(TAG, "Service connected");
      apiClient = BgServiceApi.Stub.asInterface(service);
      handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            String lastResult = apiClient.getLastResult();
            Log.e(TAG, "Hello World " + lastResult);
            textView.setText(lastResult);
          } catch (Exception e) {
            Log.e(TAG, "Failed to retrieve last result ", e);
          }
        }
      });
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      Log.d(TAG, "Service connection closed");
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    handler = new Handler();
    textView = (TextView) findViewById(R.id.text_view);

    Log.e(TAG, "contacting bgservice");
    Intent serviceIntent = new Intent("com.example.beeps.bgservice.BgService");
    serviceIntent.setPackage("com.example.beeps.bgservice");
    startService(serviceIntent);
    bindService(serviceIntent, serviceConnection, 0);
    Log.d(TAG, "Activity created and bound to BgService");
  }
}
