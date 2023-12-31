package com.egpayawal.module.network.base.request;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class ProgressRequestBody extends RequestBody {
    private File mFile;
    private UploadCallbacks mListener;

    private static final int DEFAULT_BUFFER_SIZE = 2048;

    public interface UploadCallbacks {
        void onProgressUpdate(int percentage);
    }

    public ProgressRequestBody(final File file, final UploadCallbacks listener) {
        mFile = file;
        mListener = listener;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("multipart/form-data");
    }

    @Override
    public long contentLength() throws IOException {
        return mFile.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long fileLength = mFile.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(mFile);
        double uploaded = 0;

        try {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            while ((read = in.read(buffer)) != -1) {

                // update progress on UI thread
                ProgressUpdater progressUpdater = new ProgressUpdater(uploaded, fileLength);
                handler.post(progressUpdater);
                Log.d("Progress", " " + progressUpdater.toString());

                uploaded += read;
                sink.write(buffer, 0, read);
            }
        } finally {
            in.close();
        }
    }

    private class ProgressUpdater implements Runnable {
        private double mUploaded;
        private double mTotal;

        public ProgressUpdater(double uploaded, double total) {
            mUploaded = uploaded;
            mTotal = total;
        }

        @Override
        public String toString() {
            return "File progress = " + mUploaded + " Total = " + mTotal;
        }

        @Override
        public void run() {
            int total = (int) Math.ceil((100 * mUploaded) / mTotal);
            mListener.onProgressUpdate(total);
        }
    }
}
