package com.service;

import android.content.Context;
import android.os.AsyncTask;

import com.listener.AsyncTaskCompleteListener;

import java.io.IOException;

public class PingHostTask extends AsyncTask<Void, Void, Boolean> {
    private Context mContext;
    private boolean isNetWorkAvailable = true;
    private AsyncTaskCompleteListener<Boolean> callback;

    public PingHostTask(Context mContext, AsyncTaskCompleteListener<Boolean> cb) {
        this.mContext = mContext;
        this.callback = cb;
    }

    private static int pingHost(String host, int timeout) throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        timeout /= 1000;
        String cmd = "ping -c 1 -W " + timeout + " " + host;
        Process process = runtime.exec(cmd);
        return process.waitFor();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        if (isNetWorkAvailable) {
            int pingCount = 5;
            int pingDelay = 3000;
            String host = "www.google.com";
            if (isPoorSignal(pingCount, pingDelay, host)){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean isPoorSignal) {
        if (isNetWorkAvailable) {
            callback.onTaskComplete(isPoorSignal);
        }
    }

    private boolean isPoorSignal(int count, int delay, String host) {
        int r;
        int isFailExeCount = 0;
        boolean isPoorSignal = false;
        for (int i = 0; i < count; i++) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                return false;
            }
            try {
                r = pingHost(host, delay);
            } catch (IOException e) {
                e.printStackTrace();
                return false;

            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            //r: int value of 0 or 1 or 2 0=success, 1=fail, 2=error
            if (r != 0) {
                isFailExeCount++;
            }
        }
        if (isFailExeCount >= 3)
            isPoorSignal = true;
        return isPoorSignal;
    }
}
