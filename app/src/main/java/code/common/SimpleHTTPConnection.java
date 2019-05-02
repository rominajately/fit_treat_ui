package code.common;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;

import code.database.AppSettings;
import code.utils.AppConstants;
import code.utils.AppUtils;


public class SimpleHTTPConnection {
    private static final String TAG = "SimpleHTTPConnection";

    protected static Activity mActivity;

    public SimpleHTTPConnection(Activity mActivity) {
        SimpleHTTPConnection.mActivity = mActivity;

    }

    /**
     * This method can be check internet connection is available or not.
     *
     * @param mActivity reference of activity.
     * @return
     */
    public static boolean isNetworkAvailable(@NonNull Context mActivity) {

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
            StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        boolean available = false;
        /** Getting the system's connectivity service */
        ConnectivityManager cm = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        /** Getting active network interface to get the network's staffMobile */
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                available = true;
                AppUtils.print("====activeNetwork" + activeNetwork.getTypeName());
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                available = true;
                AppUtils.print("====activeNetwork" + activeNetwork.getTypeName());
            }
        } else {
            // not connected to the internet
            available = false;
            AppUtils.print("====not connected to the internet");
        }
        /** Returning the staffMobile of the network */
        return available;
    }

    /**
     * This method can be check internet connection is available or not.
     *
     * @return
     */
    public static boolean isNetworkAvailable() {

        boolean available = false;
        /** Getting the system's connectivity service */
        ConnectivityManager cm = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        /** Getting active network interface to get the network's staffMobile */
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                available = true;
                AppUtils.print("====activeNetwork" + activeNetwork.getTypeName());
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                available = true;
                AppUtils.print("====activeNetwork" + activeNetwork.getTypeName());
            }
        } else {
            // not connected to the internet
            available = false;
            AppUtils.print("====not connected to the internet");
        }
        /** Returning the staffMobile of the network */
        return available;
    }

    public static String sendDataToServer(String mainUrl, StringBuilder postData, String method) {

        if (method.equalsIgnoreCase(AppConstants.methodget))
            return sendByGET(mainUrl);
        return sendByPOST(mainUrl, postData, method);
    }

    public static String sendByPOST(String mainUrl, StringBuilder postData, String method) {
        String result = AppConstants.connectionTimeOut;
        Reader mReader = null;
        try {
            URL url = new URL(mainUrl);
            byte[] postDataBytes = postData.toString().trim().getBytes("UTF-8");

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod(method);

            String baseAuthStr = AppSettings.getString(AppSettings.accessToken);
            AppUtils.print("==baseAuthStr" + baseAuthStr);
            if (!baseAuthStr.isEmpty())
                conn.addRequestProperty("Authorization", "Bearer " + baseAuthStr);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);
            int responseCode = conn.getResponseCode();
            AppUtils.print("==responseCode" + responseCode);
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                mReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                for (int c; (c = mReader.read()) >= 0; )
                    sb.append((char) c);
                result = sb.toString();
                return result;
            } else {

            }
        } catch (UnsupportedEncodingException e) {
            Log.i(TAG, e.getMessage());
            e.printStackTrace();
        } catch (UnknownHostException e) {
            Log.i(TAG, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
            e.printStackTrace();
            result = AppConstants.connectionTimeOut;
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            if (mReader != null) {
                try {
                    mReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    result = AppConstants.connectionTimeOut;
                }
            }
        }
        return result;
    }

    public static String sendByGET(String mainUrl) {
        Reader mReader = null;
        String result = AppConstants.connectionTimeOut;
        try {

            URL mUrl = new URL(mainUrl);
            HttpsURLConnection httpConnection = (HttpsURLConnection) mUrl.openConnection();
            String baseAuthStr = AppSettings.getString(AppSettings.accessToken);
            AppUtils.print("==baseAuthStr" + baseAuthStr);
            if (!baseAuthStr.isEmpty())
                httpConnection.addRequestProperty("Authorization", "Bearer " + baseAuthStr);
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Content-length", "0");
            httpConnection.setUseCaches(false);
            httpConnection.setAllowUserInteraction(false);
            httpConnection.setConnectTimeout(100000);
            httpConnection.setReadTimeout(100000);

            httpConnection.connect();

            int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                mReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                for (int c; (c = mReader.read()) >= 0; )
                    sb.append((char) c);
                result = sb.toString();
                return result;
            }
        } catch (UnsupportedEncodingException e) {
            Log.i(TAG, e.getMessage());
            e.printStackTrace();
        } catch (UnknownHostException e) {
            Log.i(TAG, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
            e.printStackTrace();
            result = AppConstants.connectionTimeOut;
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            e.printStackTrace();
        }
        return result;
    }


    public static String sendByPOST(String mainUrl, JSONObject json) {
        AppUtils.print("==--url" + mainUrl + "?" + json.toString());
        Reader mReader = null;
        String result = AppConstants.connectionTimeOut;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            HttpParams httpParams = new BasicHttpParams();
            HttpClient client = new DefaultHttpClient(httpParams);

            HttpPost request = new HttpPost(mainUrl);
            request.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF8")));
            request.setHeader("json", json.toString());
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            if (entity != null) {
                int responseCode = response.getStatusLine().getStatusCode();
                AppUtils.print("==responseCode" + responseCode);
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    InputStream is = entity.getContent();
                    mReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    for (int c; (c = mReader.read()) >= 0; )
                        sb.append((char) c);
                    if (stringBuilder.length() > 0 && !LastChar(stringBuilder.toString()).equalsIgnoreCase("}")) {
                        AppUtils.print("==missing bracket");
                        stringBuilder.append("}");
                    }
                    result = sb.toString();
                    return result;
                } else {
                    AppUtils.printE("HTTP Response: ", response.getStatusLine().getReasonPhrase());
                }
            }
        } catch (UnsupportedEncodingException e) {
            Log.i(TAG, e.getMessage());
            e.printStackTrace();
        } catch (UnknownHostException e) {
            Log.i(TAG, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
            e.printStackTrace();
            result = AppConstants.connectionTimeOut;
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            if (mReader != null) {
                try {
                    mReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    result = AppConstants.connectionTimeOut;
                }
            }
        }
        return result;
    }

    public static String LastChar(String string) {
        return string.substring(string.length() - 1);
    }
}
