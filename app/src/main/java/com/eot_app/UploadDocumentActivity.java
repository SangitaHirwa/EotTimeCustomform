package com.eot_app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.eot_app.eoteditor.Utils;
import com.eot_app.nav_menu.audit.audit_list.documents.ActivityEditImageDialog;
import com.eot_app.nav_menu.jobs.job_detail.chat.img_crop_pkg.ImageCropFragment;
import com.eot_app.nav_menu.jobs.job_detail.documents.PathUtils;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.language_support.LanguageController;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hypertrack.hyperlog.HyperLog;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UploadDocumentActivity extends AppCompatActivity implements ImageCropFragment.MyDialogInterface {
    private static final int PHOTO_EDIT_CODE = 147;
    private final static int CAPTURE_IMAGE_GALLARY = 222;
    private final int CAMERA_CODE = 100;
    private final int ATTACHFILE_CODE = 102;
    String path = "";
    private String captureImagePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void selectFile(boolean hideAttachment) {
        if (!Utils.isOnline(this)) {
            AppUtility.alertDialog(this, LanguageController.getInstance().getMobileMsgByKey(AppConstant.dialog_error_title), LanguageController.getInstance().getMobileMsgByKey(AppConstant.feature_not_available), LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", () -> null);
        } else {
            final BottomSheetDialog dialog = new BottomSheetDialog(UploadDocumentActivity.this);
            dialog.setContentView(R.layout.bottom_image_chooser);
            TextView camera = dialog.findViewById(R.id.camera);
            assert camera != null;
            camera.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.camera));
            TextView gallery = dialog.findViewById(R.id.gallery);
            assert gallery != null;
            gallery.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.gallery));
            LinearLayout driveLayout = dialog.findViewById(R.id.driveLayout);
            TextView drive_document = dialog.findViewById(R.id.drive_document);
            assert drive_document != null;
            drive_document.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.document));
            if (hideAttachment) {
                assert driveLayout != null;
                driveLayout.setVisibility(View.GONE);
            }
            camera.setOnClickListener(view -> {
                HyperLog.i("UploadDocumentActivity", "onClick(M) started");
                if (AppUtility.askCameraTakePicture(UploadDocumentActivity.this)) {
                    getPictureFromCamera();
                } else {
                    askTedPermission(0,AppConstant.cameraPermissions);
                }
                HyperLog.i("UploadDocumentActivity", "onClick(M) Stop");
                dialog.dismiss();
            });
            gallery.setOnClickListener(view -> {
                if (AppUtility.askGalaryTakeImagePermiSsion(UploadDocumentActivity.this)) {
                    getImageFromGallery();
                } else {
                    askTedPermission(1,AppConstant.galleryPermissions);
                }
                dialog.dismiss();
            });
            drive_document.setOnClickListener(view -> {
                if (AppUtility.askGalaryTakeImagePermiSsion(UploadDocumentActivity.this)) {
                    getDocumentsFromGallery();//only for drive documents
                } else {
                    askTedPermission(2,AppConstant.galleryPermissions);
                }
                dialog.dismiss();
            });
            dialog.show();
        }
    }


    public void selectFileforcustomform(boolean hideAttachment) {
            final BottomSheetDialog dialog = new BottomSheetDialog(UploadDocumentActivity.this);
            dialog.setContentView(R.layout.bottom_image_chooser);
            TextView camera = dialog.findViewById(R.id.camera);
            assert camera != null;
            camera.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.camera));
            TextView gallery = dialog.findViewById(R.id.gallery);
            assert gallery != null;
            gallery.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.gallery));
            LinearLayout driveLayout = dialog.findViewById(R.id.driveLayout);
            TextView drive_document = dialog.findViewById(R.id.drive_document);
            assert drive_document != null;
            drive_document.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.document));
            if (hideAttachment) {
                assert driveLayout != null;
                driveLayout.setVisibility(View.GONE);
            }
            camera.setOnClickListener(view -> {
                HyperLog.i("UploadDocumentActivity", "onClick(M) started");
                if (AppUtility.askCameraTakePicture(UploadDocumentActivity.this)) {
                    getPictureFromCamera();
                } else {
                    askTedPermission(0,AppConstant.cameraPermissions);
                }
                HyperLog.i("UploadDocumentActivity", "onClick(M) Stop");
                dialog.dismiss();
            });
            gallery.setOnClickListener(view -> {
                if (AppUtility.askGalaryTakeImagePermiSsion(UploadDocumentActivity.this)) {
                    getImageFromGallery();
                } else {
                    askTedPermission(1,AppConstant.galleryPermissions);
                }
                dialog.dismiss();
            });
            drive_document.setOnClickListener(view -> {
                if (AppUtility.askGalaryTakeImagePermiSsion(UploadDocumentActivity.this)) {
                    getDocumentsFromGallery();//only for drive documents
                } else {
                    askTedPermission(2,AppConstant.galleryPermissions);
                }
                dialog.dismiss();
            });
            dialog.show();
    }

    private void askTedPermission(int type,String[] permissions) {
        TedPermission.with(EotApp.getAppinstance())
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (type == 0)
                            getPictureFromCamera();
                        else if (type == 1)
                            getImageFromGallery();
                        else if (type == 2)
                            getDocumentsFromGallery();
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                    }
                })
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [SettingActivity] > [Permission]")
                .setPermissions(permissions)
                .check();
    }


    public void selectFileOnlyPdfFile() {
        final BottomSheetDialog dialog = new BottomSheetDialog(UploadDocumentActivity.this);
        dialog.setContentView(R.layout.bottom_image_chooser);
        LinearLayout camera_layout = dialog.findViewById(R.id.camera_layout);
        LinearLayout gallary_layout = dialog.findViewById(R.id.gallary_layout);
        TextView drive_document = dialog.findViewById(R.id.drive_document);
        assert drive_document != null;
        drive_document.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.document));


        assert camera_layout != null;
        camera_layout.setVisibility(View.GONE);
        assert gallary_layout != null;
        gallary_layout.setVisibility(View.GONE);

        drive_document.setOnClickListener(view -> {
            if (AppUtility.askGalaryTakeImagePermiSsion(UploadDocumentActivity.this)) {
                getDocumentsFromGalleryNotImage();//only for drive documents
            }
            dialog.dismiss();
        });
        dialog.show();

    }

    public void getDocumentsFromGalleryNotImage() {
        if (!AppUtility.askGalaryTakeImagePermiSsion(UploadDocumentActivity.this))
            return;
        //allow upload file extension
        String[] mimeTypes = {
                //   "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",//.doc & .docx
                "application/pdf"//pdf
        };

        /* *only for document uploading */
        Intent documentIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        documentIntent.addCategory(Intent.CATEGORY_OPENABLE);
        documentIntent.setType("*/*");
        documentIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        documentIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(documentIntent, ATTACHFILE_CODE);
    }


    public void getPictureFromCamera() {
        HyperLog.i("UploadDocumentActivity", "getPictureFromCamera(M) Start");
        if (!AppUtility.askCameraTakePicture(UploadDocumentActivity.this)) {
            HyperLog.i("UploadDocumentActivity", "getPictureFromCamera(M) Intent NOT Call.......");
            return;
        }

        HyperLog.i("UploadDocumentActivity", "getPictureFromCamera(M) Intent Call!!!!!!");

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Eot Directory");

        if (!path.exists()) {
            path.mkdir();
        }

        File imageFile = null;
        try {
            imageFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", imageFile);

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;

            this.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        startActivityForResult(takePictureIntent, CAMERA_CODE); // IMAGE_CAPTURE = 0


        //someActivityResultLauncher.launch(new Intent(this, UploadDocumentActivity.class));

        HyperLog.i("UploadDocumentActivity", "getPictureFromCamera(M) Stop");

    }


    public void getImageFromGallery() {
        if (!AppUtility.askGalaryTakeImagePermiSsion(UploadDocumentActivity.this))
            return;
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, CAPTURE_IMAGE_GALLARY);
    }

    public void getDocumentsFromGallery() {
        if (!AppUtility.askGalaryTakeImagePermiSsion(UploadDocumentActivity.this))
            return;
        //allow upload file extension
        String[] mimeTypes = {"image/jpeg", "image/jpg", "image/png",
                "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",//.doc & .docx
                "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",//.xls & .xlsx
                "application/pdf",//pdf
                "text/csv", "text/plain"//csv
        };

        /*only for document uploading */
        Intent documentIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        documentIntent.addCategory(Intent.CATEGORY_OPENABLE);
        documentIntent.setType("*/*");
        documentIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        documentIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(documentIntent, ATTACHFILE_CODE);
    }

    private File createImageFile() throws IOException {
        HyperLog.i("UploadDocumentActivity", "createImageFile Strat(M)");

        Calendar calendar = Calendar.getInstance();
        long imageFileName = calendar.getTime().getTime();

        // File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);// return path
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Eot Directory");

        File directoryPath = new File(storageDir.getPath());
        File image = File.createTempFile(
                String.valueOf(imageFileName),  /* prefix */
                ".jpg",         /* suffix */
                directoryPath   /* directory */
        );
        captureImagePath = image.getAbsolutePath();
        HyperLog.i("UploadDocumentActivity", "captureImagePath " + captureImagePath);

        HyperLog.i("UploadDocumentActivity", "createImageFile Stop(M)");

        return new File(image.getPath());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case CAMERA_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        File file = AppUtility.scaleToActualAspectRatio(captureImagePath, 1024f, 1024f);
                        if (file != null) {
                            imageEditing(Uri.fromFile(new File(captureImagePath)));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    return;
                }
                break;
            case CAPTURE_IMAGE_GALLARY:
            case ATTACHFILE_CODE:
                if (resultCode == RESULT_OK) {
                    assert data != null;
                    Uri galreyImguriUri = data.getData();

//                    Log.v("Name:::;",name);
                    String gallery_image_Path = PathUtils.getRealPath(UploadDocumentActivity.this, galreyImguriUri);
                    Log.v("gallery_image_Path:::;", gallery_image_Path);

                    String img_extension = gallery_image_Path.substring(gallery_image_Path.lastIndexOf("."));
                    /*('jpg','png','jpeg','pdf','doc','docx','xlsx','csv','xls'); supporting extensions*/
                    if (img_extension.equals(".jpg") || img_extension.equals(".png") || img_extension.equals(".jpeg")) {
                        imageEditing(data.getData());
                    } else {
                        String filename = gallery_image_Path.substring(gallery_image_Path.lastIndexOf("/") + 1);
                        onDocumentSelected(gallery_image_Path, filename, false);
                    }
                } else {
                    return;
                }
                break;
            case PHOTO_EDIT_CODE:
                Log.v("Image File Name ", "Activity Result :::::");
                if (data != null && data.hasExtra("path")) {
                    String path = data.getStringExtra("path");
                    String name = data.getStringExtra("name");
                    onDocumentSelected(path, name, true);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /*****upload image edit highlighting feature for image********/
    private void imageEditing(Uri uri) {
        Log.v("Image File Name ", "Activity Result :::::");

        Intent intent = new Intent(UploadDocumentActivity.this, ActivityEditImageDialog.class);
        intent.putExtra("uri", uri);
        intent.putExtra("allowOffline", true);
        startActivityForResult(intent, PHOTO_EDIT_CODE);
    }

    @Override
    public void onClickContinuarEvent(Uri permisoRequerido) {
        path = PathUtils.getRealPath(this, permisoRequerido);
        if (!path.isEmpty()) {
            File file = new File(path);
            if (file != null && file.exists()) {
                onDocumentSelected(path, "", true);
            }
        }
    }

    public void onDocumentSelected(String path, String name, boolean isImage) {
        Log.v("Image File Name Upload", ":::::" + name);
    }
}
