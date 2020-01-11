package tool.xfy9326.miui.getupdateurl.methods;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

import tool.xfy9326.miui.getupdateurl.R;
import tool.xfy9326.miui.getupdateurl.utils.UpdatePackageType;
import tool.xfy9326.miui.getupdateurl.utils.UpdateUrl;

public class ShareMethod {
    private static final String CHANGE_LINE = "\n";

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

    public static String buildShareText(Context context, UpdateUrl[] updateUrls) {
        StringBuilder output = new StringBuilder();
        for (UpdateUrl updateUrl : updateUrls) {
            if (updateUrl.getUpdatePackageType() == UpdatePackageType.COMPLETE) {
                output.append(context.getString(R.string.default_share_template_stable,
                        updateUrl.getUpdateDeviceCode(),
                        I18NMethod.getUpdatePackageType(context, updateUrl.getUpdatePackageType()),
                        updateUrl.getUpdateToVersion(),
                        updateUrl.getUpdateAndroidVersion(),
                        updateUrl.getDownloadUrl())).append(CHANGE_LINE).append(CHANGE_LINE);
            } else {
                output.append(context.getString(R.string.default_share_template_none_stable,
                        updateUrl.getUpdateDeviceCode(),
                        I18NMethod.getUpdatePackageType(context, updateUrl.getUpdatePackageType()),
                        updateUrl.getUpdateFromVersion(),
                        updateUrl.getUpdateToVersion(),
                        updateUrl.getUpdateAndroidVersion(),
                        updateUrl.getDownloadUrl())).append(CHANGE_LINE).append(CHANGE_LINE);
            }
        }
        if (output.length() > 1) {
            return output.substring(0, output.length() - 2);
        }
        return output.toString();
    }
}
