package tool.xfy9326.miui.getupdateurl.methods;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import tool.xfy9326.miui.getupdateurl.R;
import tool.xfy9326.miui.getupdateurl.utils.UpdatePackageType;
import tool.xfy9326.miui.getupdateurl.utils.UpdateUrl;

public class ShareMethod {
    private static final String CHANGE_LINE = "\n";

    private static final String DEVICE_CODE = "{deviceCode}";
    private static final String UPDATE_PACKAGE_TYPE = "{packageType}";
    private static final String UPDATE_VERSION = "{updateVersion}";
    private static final String ANDROID_VERSION = "{androidVersion}";
    private static final String PACKAGE_MD5 = "{packageMD5}";
    private static final String DOWNLOAD_URL = "{downloadUrl}";

    public static void copyToPasteBoard(Context context, String text) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(ClipData.newPlainText(context.getString(R.string.app_name), text));
        }
    }

    public static void shareText(Activity activity, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, text);
        activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.app_name)));
    }

    @NonNull
    private static String formatContentWithData(@NonNull Context context, @NonNull String formatString, @NonNull UpdateUrl updateUrl) {
        String content = formatString.replace(DEVICE_CODE, updateUrl.getUpdateDeviceCode())
                .replace(UPDATE_PACKAGE_TYPE, I18NMethod.getUpdatePackageType(context, updateUrl.getUpdatePackageType()))
                .replace(ANDROID_VERSION, updateUrl.getUpdateAndroidVersion())
                .replace(PACKAGE_MD5, updateUrl.getUpdatePackageMD5())
                .replace(DOWNLOAD_URL, updateUrl.getDownloadUrl());
        if (updateUrl.getUpdatePackageType() != UpdatePackageType.COMPLETE) {
            return content.replace(UPDATE_VERSION, context.getString(R.string.version_describe, updateUrl.getUpdateFromVersion(), updateUrl.getUpdateToVersion()));
        } else {
            return content.replace(UPDATE_VERSION, updateUrl.getUpdateToVersion());
        }
    }

    public static String buildShareText(Context context, UpdateUrl[] updateUrls, String shareFormat) {
        StringBuilder output = new StringBuilder();
        for (UpdateUrl updateUrl : updateUrls) {
            output.append(formatContentWithData(context, shareFormat, updateUrl)).append(CHANGE_LINE).append(CHANGE_LINE);
        }
        if (output.length() > 1) {
            output.delete(output.length() - 2, output.length());
        }
        return output.toString();
    }
}
