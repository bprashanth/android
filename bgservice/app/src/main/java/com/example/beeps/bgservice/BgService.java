package com.example.beeps.bgservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class BgService extends Service {

  private static final String TAG = BgService.class.getSimpleName();

  // periodic timer to fire off bg processing
  private Timer timer;
  private String lastResult;
  private final Object lastResultLock = new Object();

  // The IBinder interface for this is generated from the AIDL on first build
  private BgServiceApi.Stub apiEndpoint = new BgServiceApi.Stub() {
    public String getLastResult() throws RemoteException {
      synchronized (lastResultLock) {
        return lastResult;
      }
    }
  };

  @Override
  public IBinder onBind(Intent intent) {
    Log.e(TAG, "onBind with intent " + intent.toString());
    if ((BgService.class.getName()).equals(intent.getAction())) {
      Log.d(TAG, "Bound by intent " + intent);
      return apiEndpoint;
    }
    return null;
  }

  private TimerTask updateTask = new TimerTask() {
    @Override
    public void run() {
      Log.e(TAG, "Timer task doing work");
      try {
        synchronized (lastResultLock) {
          Date d = new Date();
          lastResult = d.toString();
        }
      } catch (Exception e) {
        Log.e(TAG, "Failed to retrieve results", e);
      }
    }
  };

  public BgService() {
    Log.e(TAG, "BgService constructor");
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Log.e(TAG, "onCreate");

    // kickoff a period heartbeat timer
    timer = new Timer("TweetCollectorTimer");
    timer.schedule(updateTask, 1000L, 2*1000L);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.e(TAG, "onDestroy");
    timer.cancel();
    timer = null;
  }
}
