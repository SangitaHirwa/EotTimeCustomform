package com.eot_app.utility.language_support;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import com.eot_app.services.ApiClient;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.EotApp;
import com.hypertrack.hyperlog.HyperLog;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import androidx.annotation.NonNull;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class LanguageController implements Language_pi {

    private static final LanguageController ourInstance = new LanguageController();
    private final String filename = "en.json"; // name get from server
    private JSONObject backendMsgsModel;
    private JSONObject mobileMsgsModel;
    private JSONObject staticLabelModel = new JSONObject();

    private LanguageController() {
        initializeJsonObject2();
    }

    public static LanguageController getInstance() {
        return ourInstance;
    }

    @Override
    public boolean isFileStoreInLocalStorage() {

        return false;
    }

    @Override
    public void downloadFile(final String file_url, final Callable<Boolean> function) {
        if (AppUtility.isInternetConnected()) {
            //  AppUtility.progressBarShow(EotApp.getAppinstance());
            ApiClient.getservices().downloadFile(file_url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Response<ResponseBody>>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull Response<ResponseBody> responseBodyResponse) {
                            try {
                                String file_Name = file_url.substring(file_url.lastIndexOf('.') + -2);
                                Language_Preference.getSharedprefInstance().setLanguageFilename(file_Name.substring(0, 2));
                                Language_Preference.getSharedprefInstance().setisUserChangeLang(true);
                                assert responseBodyResponse.body() != null;
                                String allData = responseBodyResponse.body().string();
                                JSONObject alljosn = new JSONObject(allData);
                                Language_Preference.getSharedprefInstance().setBackendMsgsModel(alljosn.getJSONObject("backendMsgsModel").toString());
                                Language_Preference.getSharedprefInstance().setMobileMsgsModel(alljosn.getJSONObject("mobileMsgsModel").toString());
                                initializeJsonObject2();
                                function.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                                AppUtility.progressBarDissMiss();
                            }
                        }
                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.e("", "");
                            AppUtility.progressBarDissMiss();
                        }

                        @Override
                        public void onComplete() {
                            Log.e("", "");
                        }
                    });
        }
    }

    @Override
    public void checkFileInLocal() {
        File file = EotApp.getAppinstance().getBaseContext().getFileStreamPath(filename);
        if (file.exists()) {
            Log.e("TAG :", "Asset available");
            initializeJsonObject(file);
        } else {
//            copy file from asset and save to local storage.
            getFileFromAsset(file);
//            initialize json object from the local
            checkFileInLocal();
        }


    }

    private void initializeJsonObject(File file) {
        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
            int size = is.available();
            byte[] data = new byte[size];
            is.read(data);
            is.close();
            String json = new String(data);
            backendMsgsModel = new JSONObject(json).getJSONObject("backendMsgsModel");
            mobileMsgsModel = new JSONObject(json).getJSONObject("mobileMsgsModel");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getFileFromAsset(File file) {
        try {
            InputStream is = EotApp.getAppinstance().getAssets().open("en.json");
            FileOutputStream os = new FileOutputStream(file);
            copyFile(is, os);
            is.close();
            is = null;
            os.flush();
            os.close();
            os = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    private StringBuffer getOldContentOfFile(File file) {
        StringBuffer text = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
//                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return text;
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(filename + File.separator + "en.pdf");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d("TAG", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    private void writeAssetFileToDisk() {
        AssetManager assetManager = EotApp.getAppinstance().getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);

                String outDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/X/Y/Z/";

                File outFile = new File(outDir, filename);

                out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[in.available()];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    @Override
    public String getServerMsgByKey(String key) {
        try {
            if (backendMsgsModel.getString(key) != null) {
                return backendMsgsModel.getString(key);
            } else return key;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return key;
    }

    @Override
    public String getMobileMsgByKey(String key) {
        try {
            if(Language_Preference.getSharedprefInstance().getStaticMsgsModel()!=null&&
                    !Language_Preference.getSharedprefInstance().getStaticMsgsModel().isEmpty()){
                staticLabelModel = new JSONObject(Language_Preference.getSharedprefInstance().getStaticMsgsModel());
            }
            if(Language_Preference.getSharedprefInstance().getStaticMsgsModel()!=null&&
                    Language_Preference.getSharedprefInstance().getStaticMsgsModel().isEmpty()){
                staticLabelModel = new JSONObject();
            }
        } catch (JSONException e) {
                e.printStackTrace();
        }
        try {
            if (staticLabelModel != null && staticLabelModel.getString(key) != null)
                return staticLabelModel.getString(key);
        } catch (Exception ex) {
            HyperLog.i("", "getMobileMsgByKey(M) Exception" + key);
            ex.printStackTrace();
        }
        try {
            if (mobileMsgsModel.getString(key) != null) {
                return mobileMsgsModel.getString(key);
            } else return key;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return key;
    }

    @Override
    public void initializeJsonObject2() {
        Log.e("TAG :", "is here");
        if (Language_Preference.getSharedprefInstance().getBackendMsgsModel() == null) {
            try {
                InputStream is = EotApp.getAppinstance().getAssets().open(filename);
                int size = is.available();
                byte[] data = new byte[size];
                is.read(data);
                is.close();
                String json = new String(data);
                JSONObject jsonObject = new JSONObject(json);
                Language_Preference.getSharedprefInstance().setBackendMsgsModel(jsonObject.getJSONObject("backendMsgsModel").toString());
                Language_Preference.getSharedprefInstance().setMobileMsgsModel(jsonObject.getJSONObject("mobileMsgsModel").toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        try {
            backendMsgsModel = new JSONObject(Language_Preference.getSharedprefInstance().getBackendMsgsModel());
            mobileMsgsModel = new JSONObject(Language_Preference.getSharedprefInstance().getMobileMsgsModel());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearlanguageData() {
        Language_Preference.getSharedprefInstance().clearPreference();
    }

}
