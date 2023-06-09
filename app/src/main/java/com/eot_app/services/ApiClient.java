package com.eot_app.services;

import com.eot_app.BuildConfig;
import com.eot_app.utility.App_preference;
import com.hypertrack.hyperlog.HyperLog;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by geet-pc on 21/5/18.
 */

public class ApiClient {
    private static Retrofit retrofit = null;

    public static Retrofit getInstance() {

        OkHttpClient client;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // Logging
        if(BuildConfig.BUILD_TYPE.equals("debug"))
        {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.level(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }

        builder.addInterceptor(new AddCookiesInterceptor()); // VERY VERY IMPORTANT
        builder.addInterceptor(new ReceivedCookiesInterceptor()); // VERY VERY IMPORTANT
        builder.connectTimeout(60, TimeUnit.SECONDS);
        builder.readTimeout(60, TimeUnit.SECONDS);
        builder.writeTimeout(60, TimeUnit.SECONDS);



        /* **call Api error**/
        try {
            TLSSocketFactory tlsSocketFactory = new TLSSocketFactory();
            if (tlsSocketFactory.getTrustManager() != null) {
                builder.sslSocketFactory(tlsSocketFactory, tlsSocketFactory.getTrustManager());
            }
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
            HyperLog.e("TAG","TLS Socket error "+e.getMessage());
        }

        client = builder.build();

        String baseUrl="";
        if(BuildConfig.BASEURL.contains("eyeontask.com")){
            baseUrl=App_preference.getSharedprefInstance().getBaseURL();
        }
        else {
            baseUrl=BuildConfig.BASEURL;
        }

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .baseUrl(baseUrl)
//                    .baseUrl(App_preference.getSharedprefInstance().getBaseURL())
                    .build();
        }
        return retrofit;
    }

    public static Service_apis getservices() {
        return getInstance().create(Service_apis.class);
    }

    /*** retrofit instance empty after get server specific uri & recreate instance for server url(UK/US/AS/AU) means craete again instance for new Uri***/
    public static void resetClientforBaseurl() {
        if (retrofit != null) {
            retrofit = null;
        }
    }

}
