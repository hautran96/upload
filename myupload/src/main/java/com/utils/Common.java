package com.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class Common {
    /**
     * Convert String from input stream
     */
    public static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    private static String encrypt(String _key, String value) {
        try {
            DESKeySpec keySpec = new DESKeySpec(_key.getBytes("UTF8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            byte[] clearText = value.getBytes("UTF8");
            // Cipher is not thread safe
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            return Base64.encodeToString(cipher.doFinal(clearText), Base64.DEFAULT);

        } catch (InvalidKeyException | UnsupportedEncodingException | InvalidKeySpecException | NoSuchAlgorithmException | BadPaddingException
                | NoSuchPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return value;
    }

    private static String decrypt(String _key, String value) {
        try {
            DESKeySpec keySpec = new DESKeySpec(_key.getBytes("UTF8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            byte[] encryptedPwdBytes = Base64.decode(value, Base64.DEFAULT);
            // cipher is not thread safe
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedValueBytes = (cipher.doFinal(encryptedPwdBytes));

            return new String(decryptedValueBytes);

        } catch (InvalidKeyException | UnsupportedEncodingException | InvalidKeySpecException | NoSuchAlgorithmException | BadPaddingException
                | NoSuchPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static void removeSharedPreference(String preferenceName, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE).edit();
        editor.clear().apply();
    }


    public static boolean isJSONValid(String text) {
        try {
            new JSONObject(text);
        } catch (JSONException ex) {
            try {
                new JSONArray(text);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static void showDialog(Context context, String title, String message, String labelButton, DialogInterface.OnClickListener listener) {
        if(context != null) {
            if(listener == null) {
                listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                };
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message);
            builder.setTitle(title);
            builder.setNegativeButton(labelButton, listener);
            builder.setCancelable(false);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public static AlertDialog buildAlertDialog(Context context, View view, boolean isHandleDialog,
                                               String title,
                                               String labelNegativeButton, String labelPositiveButton) {
        AlertDialog alertDialog;
        if(context != null && isHandleDialog) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            if(view.getParent()!=null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            builder.setView(view);
            builder.setTitle(title);
            builder.setNegativeButton(labelNegativeButton, null);
            builder.setCancelable(false);
            builder.setPositiveButton(labelPositiveButton, null);
            alertDialog = builder.create();
            return alertDialog;
        }
        return null;
    }
    public static void showDialogFull(Context context, boolean isHandleDialog, String title, String message, String labelNegativeButton, String labelPositiveButton, DialogInterface.OnClickListener listener) {
        if(context != null && isHandleDialog) {
            if(listener == null) {
                listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                };
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message);
            builder.setTitle(title);
            builder.setNegativeButton(labelNegativeButton, listener);
            builder.setCancelable(false);
            builder.setPositiveButton(labelPositiveButton, listener);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }



    //Implement hide soft keyboard when touch outside
    public static void hideSoftInput(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.
                INPUT_METHOD_SERVICE);
        if(context instanceof Activity) {
            View focusView = ((Activity) context).getCurrentFocus();
            if (focusView != null) {
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
        }
    }

    public static <C> ArrayList<C> asList(SparseArray<C> sparseArray) {
        if (sparseArray == null) return null;
        ArrayList<C> arrayList = new ArrayList<>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++) {
            arrayList.add(sparseArray.valueAt(i));
        }
        return arrayList;
    }

    public static <C> SparseArray<C> asSparseArray(ArrayList<C> list) {
        if (list == null) return null;
        SparseArray<C> sparseArray = new SparseArray<>();
        for(int i = 0; i < list.size(); i++) {
            sparseArray.put(i, list.get(i));
        }
        return sparseArray;
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
