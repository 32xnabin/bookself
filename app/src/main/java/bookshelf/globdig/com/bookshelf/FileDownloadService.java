package bookshelf.globdig.com.bookshelf;

/**
 * Created by Sandip on 02/12/2016.
 */
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Vaibhav.Jani on 6/4/15.
 */
public class FileDownloadService extends IntentService {

    private static int STATUS_OK = 100;

    private static int STATUS_FAILED = 200;

    private static final String DOWNLOADER_RECEIVER = "downloader_receiver";

    private static final String DOWNLOAD_DETAILS = "download_details";

    private static final String DOWNLOAD_STARTED = "download_started";

    private static final String DOWNLOAD_FAILED = "download_failed";

    private static final String DOWNLOAD_COMPLETED = "download_completed";

    private static final String DOWNLOAD_PROGRESS = "download_progress";

    public FileDownloadService() {

        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle bundle = intent.getExtras();

        if (bundle == null
                || !bundle.containsKey(DOWNLOADER_RECEIVER)
                || !bundle.containsKey(DOWNLOAD_DETAILS)) {

            return;
        }

        ResultReceiver resultReceiver = bundle.getParcelable(DOWNLOADER_RECEIVER);

        /*FileDownloader fileDownloader;

        if (resultReceiver instanceof FileDownloader) {

            fileDownloader = (FileDownloader) resultReceiver;

        } else {

            return;
        }*/

        //FileDownloader fileDownloader = (FileDownloader) resultReceiver;

        DownloadDetails downloadDetails = bundle.getParcelable(DOWNLOAD_DETAILS);

        try {

            URL url = new URL(downloadDetails.getServerFilePath());

            URLConnection urlConnection = url.openConnection();

            urlConnection.connect();

            int lengthOfFile = urlConnection.getContentLength();

            Log.d("FileDownloaderService", "Length of file: " + lengthOfFile);
            downloadStarted(resultReceiver);

            InputStream input = new BufferedInputStream(url.openStream());

            String localPath = downloadDetails.getLocalFilePath();

            if(downloadDetails.isRequiresUnzip()) {

                localPath = URLProvider.getLocalZipLocation(this);
            }

            OutputStream output = new FileOutputStream(localPath);

            byte data[] = new byte[1024];

            long total = 0;

            int count;

            while ((count = input.read(data)) != -1) {

                total += count;

                int progress = (int) ((total * 100) / lengthOfFile);

                sendProgress(progress, resultReceiver);

                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();

            if (downloadDetails.isRequiresUnzip()) {

                unzip(localPath);
            }

            downloadCompleted(resultReceiver);

        } catch (Exception e) {

            e.printStackTrace();

            downloadFailed(resultReceiver);
        }

    }

    public void sendProgress(int progress, ResultReceiver receiver) {

        Bundle progressBundle = new Bundle();
        progressBundle.putInt(FileDownloadService.DOWNLOAD_PROGRESS, progress);
        receiver.send(STATUS_OK, progressBundle);
    }

    public void downloadStarted(ResultReceiver resultReceiver) {

        Bundle progressBundle = new Bundle();
        progressBundle.putBoolean(FileDownloadService.DOWNLOAD_STARTED, true);
        resultReceiver.send(STATUS_OK, progressBundle);
    }

    public void downloadCompleted(ResultReceiver resultReceiver) {

        Bundle progressBundle = new Bundle();
        progressBundle.putBoolean(FileDownloadService.DOWNLOAD_COMPLETED, true);
        resultReceiver.send(STATUS_OK, progressBundle);
    }

    public void downloadFailed(ResultReceiver resultReceiver) {

        Bundle progressBundle = new Bundle();
        progressBundle.putBoolean(FileDownloadService.DOWNLOAD_FAILED, true);
        resultReceiver.send(STATUS_FAILED, progressBundle);
    }

    private void unzip(String zipFilePath) throws Exception {

        String destinationPath = URLProvider.getDataDir(this);

        File archive = new File(zipFilePath);

        try {

            ZipFile zipfile = new ZipFile(archive);

            for (Enumeration e = zipfile.entries(); e.hasMoreElements(); ) {

                ZipEntry entry = (ZipEntry) e.nextElement();

                unzipEntry(zipfile, entry, destinationPath);
            }

        } catch (Exception e) {

            Log.e("Unzip zip", "Unzip exception", e);
        }
    }

    private void unzipEntry(ZipFile zipfile, ZipEntry entry, String outputDir) throws IOException {

        if (entry.isDirectory()) {
            createDir(new File(outputDir, entry.getName()));
            return;
        }

        File outputFile = new File(outputDir, entry.getName());
        if (!outputFile.getParentFile().exists()) {
            createDir(outputFile.getParentFile());
        }

        Log.v("ZIP E", "Extracting: " + entry);

        InputStream zin = zipfile.getInputStream(entry);
        BufferedInputStream inputStream = new BufferedInputStream(zin);
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

        try {

            //IOUtils.copy(inputStream, outputStream);

            try {

                for (int c = inputStream.read(); c != -1; c = inputStream.read()) {
                    outputStream.write(c);
                }

            } finally {

                outputStream.close();
            }

        } finally {
            outputStream.close();
            inputStream.close();
        }
    }

    private void createDir(File dir) {

        if (dir.exists()) {
            return;
        }

        Log.v("ZIP E", "Creating dir " + dir.getName());

        if (!dir.mkdirs()) {

            throw new RuntimeException("Can not create dir " + dir);
        }
    }

    @SuppressLint("ParcelCreator")
    public static class FileDownloader extends ResultReceiver {

        private DownloadDetails downloadDetails;

        private OnDownloadStatusListener onDownloadStatusListener;

        public static FileDownloader getInstance(DownloadDetails downloadDetails, OnDownloadStatusListener downloadStatusListener) {

            Handler handler = new Handler();

            FileDownloader fileDownloader = new FileDownloader(handler);

            fileDownloader.downloadDetails = downloadDetails;

            fileDownloader.onDownloadStatusListener = downloadStatusListener;

            return fileDownloader;
        }

        public void download(Context context) {

            if (Util.isNetworkAvailable(context, (context instanceof Activity))) {

                Intent intent = new Intent(context, FileDownloadService.class);
                intent.putExtra(FileDownloadService.DOWNLOADER_RECEIVER, this);
                intent.putExtra(FileDownloadService.DOWNLOAD_DETAILS, downloadDetails);
                context.startService(intent);
            }
        }

        private FileDownloader(Handler handler) {

            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            super.onReceiveResult(resultCode, resultData);

            if (onDownloadStatusListener == null) {

                return;
            }

            if (resultCode == FileDownloadService.STATUS_OK) {

                if (resultData.containsKey(FileDownloadService.DOWNLOAD_STARTED)
                        && resultData.getBoolean(FileDownloadService.DOWNLOAD_STARTED)) {

                    onDownloadStatusListener.onDownloadStarted();

                } else if (resultData.containsKey(FileDownloadService.DOWNLOAD_COMPLETED)
                        && resultData.getBoolean(FileDownloadService.DOWNLOAD_COMPLETED)) {

                    onDownloadStatusListener.onDownloadCompleted();

                } else if (resultData.containsKey(FileDownloadService.DOWNLOAD_PROGRESS)) {

                    int progress = resultData.getInt(FileDownloadService.DOWNLOAD_PROGRESS);
                    onDownloadStatusListener.onDownloadProgress(progress);

                }

            } else if (resultCode == FileDownloadService.STATUS_FAILED) {

                onDownloadStatusListener.onDownloadFailed();
            }
        }

        public DownloadDetails getDownloadDetails() {

            return downloadDetails;
        }

        public void setDownloadDetails(DownloadDetails downloadDetails) {

            this.downloadDetails = downloadDetails;
        }

        public OnDownloadStatusListener getOnDownloadStatusListener() {

            return onDownloadStatusListener;
        }

        public void setOnDownloadStatusListener(OnDownloadStatusListener onDownloadStatusListener) {

            this.onDownloadStatusListener = onDownloadStatusListener;
        }

    }

    public static interface OnDownloadStatusListener {

        void onDownloadStarted();

        void onDownloadCompleted();

        void onDownloadFailed();

        void onDownloadProgress(int progress);

    }

    public static class DownloadDetails implements Parcelable {

        private boolean requiresUnzip;

        private String serverFilePath;

        private String localFilePath;

        public DownloadDetails(String serverFilePath, String localPath, boolean requiresUnzip) {

            this.serverFilePath = serverFilePath;

            this.localFilePath = localPath;

            this.requiresUnzip = requiresUnzip;
        }

        protected DownloadDetails(Parcel in) {

            requiresUnzip = in.readByte() != 0x00;
            serverFilePath = in.readString();
            localFilePath = in.readString();
        }

        @Override
        public int describeContents() {

            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

            dest.writeByte((byte) (requiresUnzip ? 0x01 : 0x00));
            dest.writeString(serverFilePath);
            dest.writeString(localFilePath);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<DownloadDetails> CREATOR = new Parcelable.Creator<DownloadDetails>() {

            @Override
            public DownloadDetails createFromParcel(Parcel in) {

                return new DownloadDetails(in);
            }

            @Override
            public DownloadDetails[] newArray(int size) {

                return new DownloadDetails[size];
            }
        };

        public boolean isRequiresUnzip() {

            return requiresUnzip;
        }

        public void setRequiresUnzip(boolean requiresUnzip) {

            this.requiresUnzip = requiresUnzip;
        }

        public String getServerFilePath() {

            return serverFilePath;
        }

        public void setServerFilePath(String serverFilePath) {

            this.serverFilePath = serverFilePath;
        }

        public String getLocalFilePath() {

            return localFilePath;
        }

        public void setLocalFilePath(String localFilePath) {

            this.localFilePath = localFilePath;
        }

        public static Creator<DownloadDetails> getCreator() {

            return CREATOR;
        }
    }

    private static class URLProvider {

        private static final String EXTERNAL_FOLDER_NAME = "YourFolderNAME";

        public static String getDataDir(Context context) throws Exception {

            String path = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).applicationInfo.dataDir;

            path += "/" + EXTERNAL_FOLDER_NAME;

            File file = new File(path);

            if(!file.exists()) {

                file.mkdirs();
            }

            return path;
        }

        public static String getLocalZipLocation(Context context) throws Exception {

            String path = getDataDir(context) + "YOUR_ZIP_NAME";

        /*File file = new File(path);

        if(!file.exists()) {

            file.mkdirs();
        }*/

            return path;
        }
    }

    private static class Util {

        public static boolean isNetworkAvailable(final Context context, boolean canShowErrorDialogOnFail) {

            boolean isNetworkAvailable = false;

            if (context != null) {

                ConnectivityManager connectivityManager
                        = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

                isNetworkAvailable = activeNetworkInfo != null && activeNetworkInfo.isConnected();

                if (!isNetworkAvailable && canShowErrorDialogOnFail) {

                    Log.v("TAG", "context : " + context.toString());

                    if (context instanceof Activity) {

                        ((Activity) context).runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                /*Util.displayAlert((Activity) context,
                                        context.getString(R.string.app_name),
                                        context.getString(R.string.alert_internet),
                                        false);*/
                            }
                        });

                    }
                }
            }

            return isNetworkAvailable;
        }
    }
}
