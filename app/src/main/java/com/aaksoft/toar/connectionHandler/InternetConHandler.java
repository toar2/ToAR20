package com.aaksoft.toar.connectionHandler;

/*
    Created By Aasharib
    on
    23 September, 2018
 */

import android.os.AsyncTask;

import java.net.InetAddress;

/*
    This class handles internet conectivity issue during application runtime
 */

public class InternetConHandler extends AsyncTask<Object, Void, Boolean>{

    public InternetConHandler.AsyncResponse delegate = null;

    public interface AsyncResponse {
        void internetAvailable();
        void internetNotAvailable();
    }

    public InternetConHandler(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected Boolean doInBackground(Object... objects) {
        return isInternetAvailable();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(result) {
            delegate.internetAvailable();
        }
        else {
            delegate.internetNotAvailable();
        }
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //Here google b/c it has highest availablity
            return !ipAddr.equals("");
        }
        catch (Exception e) {
            return false;
        }
    }
}
