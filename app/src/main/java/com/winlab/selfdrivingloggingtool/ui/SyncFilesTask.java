package com.winlab.selfdrivingloggingtool.ui;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Hongyu on 2/2/17.
 */

public class SyncFilesTask extends ApiClientAsyncTask<Void, Void, Metadata> {

    public SyncFilesTask(Context context) {
        super(context);
    }

    @Override
    protected Metadata doInBackgroundConnected(Void... arg0) {

        ArrayList<File> files = new ArrayList<File>();
        File parentDir = new File(Environment.getExternalStorageDirectory(), "AutoLogger");
        files.add(parentDir);
        while (files.size() > 0) {
            ArrayList<File> next = new ArrayList<File>();
            for (File file : files) {
                if (file.isDirectory()) {
                    List<File> nested = new ArrayList<File>(Arrays.asList(file.listFiles()));
                    next.addAll(nested);
                }
                if (file.isFile()) {
                    DriveContentsResult driveContentsResult =
                            Drive.DriveApi.newDriveContents(getGoogleApiClient()).await();
                    DriveContents originalContents = driveContentsResult.getDriveContents();
                    OutputStream os = originalContents.getOutputStream();
                    try {
                        os.write("Hello world!\n".getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                    Log.i("File", file.getName());
                    byte[] buf = new byte[8192];
                    InputStream is;
                    try {
                        is = new FileInputStream(file);
                        int c = 0;
                        while ((c = is.read(buf, 0, buf.length)) > 0) {
                            os.write(buf, 0, c);
                            os.flush();
                        }
                        os.close();
                        is.close();
                    } catch (IOException e) {};

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(file.getName())
                            .setMimeType("text/plain")
                            .setStarred(true).build();
                    Drive.DriveApi.getRootFolder(getGoogleApiClient())
                            .createFile(getGoogleApiClient(), changeSet, originalContents)
                            .await();
                }
            }
            files = next;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Metadata result) {
        super.onPostExecute(result);
        if (result == null) {
            // The creation failed somehow, so show a message.
            return;
        }
        // The creation succeeded, show a message.
    }
}
