package tool.xfy9326.miui.getupdateurl.methods;

import android.content.Context;

import androidx.annotation.NonNull;

import tool.xfy9326.miui.getupdateurl.R;
import tool.xfy9326.miui.getupdateurl.utils.UpdatePackageType;

public class I18NMethod {

    @NonNull
    public static String getUpdatePackageType(@NonNull Context context, UpdatePackageType type) {
        switch (type) {
            case COMPLETE:
                return context.getString(R.string.update_package_type_complete);
            case ROOT:
                return context.getString(R.string.update_package_type_root);
            case OTA:
                return context.getString(R.string.update_package_type_ota);
            default:
                throw new RuntimeException("UpdatePackageType " + type.toString() + " has no i18n translation.");
        }
    }
}
