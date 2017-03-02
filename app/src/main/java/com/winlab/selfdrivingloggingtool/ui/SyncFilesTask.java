package com.winlab.selfdrivingloggingtool.ui;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.drive.query.SortOrder;
import com.google.android.gms.drive.query.SortableField;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by Hongyu on 2/2/17.
 */

public class SyncFilesTask extends ApiClientAsyncTask<Void, Void, Metadata> {

    public SyncFilesTask(Context context) {
        super(context);
    }

    @Override
    protected Metadata doInBackgroundConnected(Void... arg0) {
        Log.i("Google Drive Sync","Starting");
        DriveId folderID = getFolderbyName("AutoLogger",null);
        DriveFolder loggerfolder = folderID.asDriveFolder();
        Log.i("Google Drive Sync","Got Google Drive folder");
        // get all the local files and upload
        String folderpath = Environment.getExternalStorageDirectory().toString()+"/AutoLogger/";
        Log.i("Google Drive Sync","Path: "+folderpath);
        File parentDir = new File(folderpath);
        if (!parentDir.isDirectory()) {
            Log.i("Google Drive Sync","Local AutoLogger Folder does not exist");
            return null;
        }
        File[] dirs = parentDir.listFiles();
        ArrayList<File> files = new ArrayList<File>(Arrays.asList(parentDir.listFiles()));
        for (File file : files) {
//            if (file.isDirectory()) {
//                DriveFolder curFolder = getFolderbyName(file.getName(), loggerfolder).asDriveFolder();
//                List<File> nested = new ArrayList<File>(Arrays.asList(file.listFiles()));
//                for (File logfile : nested) {
//                    uploadFile(logfile, curFolder);
//                }
//            }
            if (file.isFile()) {
                uploadFile(file, loggerfolder);
            }
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

    protected DriveId getFolderbyName(String foldername, DriveFolder parentfolder) {
        DriveId folderid = null;
        if (parentfolder == null) {
            parentfolder = Drive.DriveApi.getRootFolder(getGoogleApiClient());
        }
        // check whether there is an existing folder and get it's drive id
        SortOrder sortOrder = new SortOrder.Builder()
                .addSortAscending(SortableField.TITLE)
                .addSortDescending(SortableField.MODIFIED_DATE).build();
        Query folderquery = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, foldername))
                .addFilter(Filters.eq(SearchableField.TRASHED, false))
                .setSortOrder(sortOrder)
                .build();
        DriveApi.MetadataBufferResult qresult = Drive.DriveApi.query(getGoogleApiClient(), folderquery).await();
        Iterator<Metadata> iterator = qresult.getMetadataBuffer().iterator();
        while (iterator.hasNext()){
            Metadata metadata = iterator.next();
            if (metadata.isFolder() && !metadata.isTrashed()){
                folderid = metadata.getDriveId();
                Log.i("CreateFolder","Already have " + foldername + " Folder");
                break;
            }
        }
        // if not, create a new one and gets its drive id
        if (folderid == null) {
            Log.i("CreateFolder","Creating " + foldername + " Folder");
            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                    .setTitle(foldername).build();
            DriveFolder.DriveFolderResult createfolderResult =  parentfolder.createFolder(
                    getGoogleApiClient(), changeSet).await();
            folderid = createfolderResult.getDriveFolder().getDriveId();
        }
        qresult.getMetadataBuffer().release();
        return folderid;
    }

    protected void uploadFile(File file, DriveFolder folder) {
        Query folderquery = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, file.getName()))
                .build();
        DriveApi.MetadataBufferResult qresult = Drive.DriveApi.query(getGoogleApiClient(), folderquery).await();
        Iterator<Metadata> iterator = qresult.getMetadataBuffer().iterator();
        while (iterator.hasNext()){
            Metadata metadata = iterator.next();
            if (!metadata.isFolder() && !metadata.isTrashed()){
                qresult.getMetadataBuffer().release();
                return;
            }
        }
        qresult.getMetadataBuffer().release();
        DriveContentsResult driveContentsResult =
                Drive.DriveApi.newDriveContents(getGoogleApiClient()).await();
        DriveContents originalContents = driveContentsResult.getDriveContents();
        OutputStream os = originalContents.getOutputStream();
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
                .setStarred(true).build();
        folder.createFile(getGoogleApiClient(), changeSet, originalContents)
                .await();
    }
}
