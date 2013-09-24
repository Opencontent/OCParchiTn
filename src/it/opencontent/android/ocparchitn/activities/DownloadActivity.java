package it.opencontent.android.ocparchitn.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import it.opencontent.android.ocparchitn.Constants;
import it.opencontent.android.ocparchitn.utils.FileNameCreator;

public class DownloadActivity extends Activity {
    private final static String TAG  = DownloadActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        try{
            URL url = new URL(Constants.SOAP_ENDPOINT_NO_SLASH+intent.getExtras().get(Constants.EXTRAKEY_DOWNLOAD_URL).toString());
            new DownloadFilesTask().execute(url);
        } catch (Exception e){
            Log.e(TAG,e.getMessage());
            e.printStackTrace();
        }

    }

    private void salvaFile(InputStream in, String  filename){
        FileOutputStream outputStream;
        try {
            String path = FileNameCreator.getManualsTempPath(filename);
            File file = new File(path);
            outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1)
            {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            getIntent().putExtra(Constants.EXTRAKEY_DOWNLOAD_MANUALE_ACTUAL_PATH,path);
            setResult(RESULT_OK,getIntent());
        } catch (Exception e) {
            setResult(RESULT_CANCELED,null);
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
        } finally{
            finish();
        }
    }
    private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground(URL... urls) {
            int count = urls.length;
            long totalSize = 0;
            for (int i = 0; i < count; i++) {
                Downloader d = new Downloader();
                totalSize += d.downloadFile(urls[i]);
//                publishProgress((int) ((i / (float) count) * 100));
                // Escape early if cancel() is called
                if (isCancelled()) break;
            }
            return totalSize;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {
        }
    }

    private class Downloader{
        public int downloadFile(URL url){
            HttpURLConnection urlConnection = null;
            try {
                Uri uri= Uri.parse(url.toString());
                String filename = uri.getQueryParameter(Constants.PDFNAME_QUERY_PARAMETER);
                String path = FileNameCreator.getManualsTempPath(filename);
                File file = new File(path);
                if(file.exists()){

                    getIntent().putExtra(Constants.EXTRAKEY_DOWNLOAD_MANUALE_ACTUAL_PATH,path);
                    setResult(RESULT_OK,getIntent());
                    finish();
                } else {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    salvaFile(in,filename);
                }
            } catch (Exception e){
                Log.e(TAG, e.getMessage());
            } finally{
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
            }
            return 1;
        }
    }
}
