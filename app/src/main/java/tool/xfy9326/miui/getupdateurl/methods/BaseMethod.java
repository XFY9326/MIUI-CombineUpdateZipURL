package tool.xfy9326.miui.getupdateurl.methods;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import tool.xfy9326.miui.getupdateurl.Constants;

public class BaseMethod {
    public static final int STORAGE_PERMISSION_REQUEST_CODE = 816;

    public static boolean isMIUI() {
        return Build.MANUFACTURER.equals(Constants.XIAOMI) && Build.HOST.contains(Constants.MIUI);
    }

    public static boolean hasNoStoragePermission(@NonNull Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    public static void requestStoragePermission(@NonNull Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
    }
}
