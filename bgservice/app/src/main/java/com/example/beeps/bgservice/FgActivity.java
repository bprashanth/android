package com.example.beeps.bgservice;

import android.app.Activity;
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
import android.widget.TextView;
import org.w3c.dom.Text;

public class FgActivity extends Activity {

  private String TAG = "FgActivity";
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
            Log.e(TAG, "Date " + lastResult);
            textView.setText(lastResult);
          } catch (Exception e) {
            Log.e(TAG, "Failed to retrieve last result ");
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
    setContentView(R.layout.activity_fg);

    handler = new Handler();
    textView = (TextView) findViewById(R.id.text_view);

    String serviceName = BgService.class.getName();
    String packageName = this.getPackageName();

    Log.d(TAG, "onCreate, invoking " +  serviceName + " in package " + packageName);

    Intent serviceIntent = new Intent(serviceName);
    serviceIntent.setPackage(packageName);
    startService(serviceIntent);
    bindService(serviceIntent, serviceConnection, 0);
    Log.d(TAG, "Activity created and bound to BgService");
  }

}
