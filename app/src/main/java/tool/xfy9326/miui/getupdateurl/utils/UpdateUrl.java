package tool.xfy9326.miui.getupdateurl.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tool.xfy9326.miui.getupdateurl.Constants;

@SuppressWarnings({"WeakerAccess", "unused"})
public class UpdateUrl {
    private String downloadUrl;
    private String updateFileName;

    private UpdatePackageType updatePackageType;

    private String updateFromVersion;
    private String updateToVersion;
    private String updateDeviceCode;
    private String updateAndroidVersion;
    private String updatePackageMD5;

    public UpdateUrl(@NonNull HandlerType type, @NonNull String content) {
        switch (type) {
            case URL:
                downloadUrl = content;
                updateFileName = getFileNameFromUrl(downloadUrl);
                parseUpdateFileName();
                break;
            case FILE_NAME:
                updateFileName = content;
                parseUpdateFileName();
                downloadUrl = getUrlFileName(updateToVersion, updateFileName);
                break;
        }
    }

    private static String getFileNameFromUrl(String downloadUrl) {
        if (downloadUrl.startsWith(Constants.HTTP)) {
            downloadUrl = downloadUrl.substring(Constants.HTTP.length());
        } else if (downloadUrl.startsWith(Constants.HTTPS)) {
            downloadUrl = downloadUrl.substring(Constants.HTTPS.length());
        }
        String[] urlPart = downloadUrl.split(Constants.URL_SPILT);
        return urlPart[urlPart.length - 1];
    }

    private static String getUrlFileName(String updateToVersion, String updateFileName) {
        return Constants.UPDATE_SERVER + Constants.URL_SPILT + updateToVersion + Constants.URL_SPILT + updateFileName;
    }

    private void parseUpdateFileName() {
        String filePureName = updateFileName.substring(0, updateFileName.lastIndexOf(Constants.FILE_TYPE_SPILT));
        if (filePureName.contains(Constants.COMPLETE_PACKAGE_SPLIT)) {
            updatePackageType = UpdatePackageType.COMPLETE;
            String[] fileNamePart = filePureName.split(Constants.COMPLETE_PACKAGE_SPLIT);
            if (fileNamePart.length == 5 && Constants.MIUI.equals(fileNamePart[0])) {
                updateDeviceCode = fileNamePart[1].toLowerCase();
                updateToVersion = fileNamePart[2];
                updatePackageMD5 = fileNamePart[3];
                updateAndroidVersion = fileNamePart[4];
            } else {
                throw new RuntimeException(updateFileName + " isn't miui update package!");
            }
        } else if (filePureName.contains(Constants.OTA_OR_ROOT_PACKAGE_SPLIT)) {
            String[] fileNamePart = filePureName.split(Constants.OTA_OR_ROOT_PACKAGE_SPLIT);
            if (fileNamePart.length == 7 && Constants.MIUI.equals(fileNamePart[0]) && Constants.OTA_OR_ROOT_PACKAGE_PREFIX.equals(fileNamePart[1])) {
                updateDeviceCode = fileNamePart[2].toLowerCase();
                if (fileNamePart[3].endsWith(Constants.ROOT_PACKAGE_TYPE)) {
                    updatePackageType = UpdatePackageType.ROOT;
                    updateFromVersion = fileNamePart[3].substring(0, fileNamePart[3].indexOf(Constants.ROOT_PACKAGE_TYPE) - 1);
                    updateToVersion = fileNamePart[4].substring(0, fileNamePart[4].indexOf(Constants.ROOT_PACKAGE_TYPE) - 1);
                } else {
                    updatePackageType = UpdatePackageType.OTA;
                    updateFromVersion = fileNamePart[3];
                    updateToVersion = fileNamePart[4];
                }
                updatePackageMD5 = fileNamePart[5];
                updateAndroidVersion = fileNamePart[6];
            } else {
                throw new RuntimeException(updateFileName + " isn't miui update package!");
            }
        } else {
            throw new RuntimeException("File name " + updateFileName + " split error!");
        }
    }

    @NonNull
    public String getDownloadUrl() {
        return downloadUrl;
    }

    @NonNull
    public String getUpdateFileName() {
        return updateFileName;
    }

    @NonNull
    public UpdatePackageType getUpdatePackageType() {
        return updatePackageType;
    }

    @NonNull
    public String getUpdateFromVersion() {
        if (updatePackageType == UpdatePackageType.COMPLETE) {
            throw new RuntimeException(UpdatePackageType.COMPLETE + " has no update from version!");
        } else {
            return updateFromVersion;
        }
    }

    @NonNull
    public String getUpdateToVersion() {
        return updateToVersion;
    }

    @NonNull
    public String getUpdateDeviceCode() {
        return updateDeviceCode;
    }

    @NonNull
    public String getUpdateAndroidVersion() {
        return updateAndroidVersion;
    }

    @NonNull
    public String getUpdatePackageMD5() {
        return updatePackageMD5;
    }

    @NonNull
    @Override
    public String toString() {
        return "UpdateUrl{" +
                "downloadUrl='" + downloadUrl + '\'' +
                ", updateFileName='" + updateFileName + '\'' +
                ", updatePackageType=" + updatePackageType +
                ", updateFromVersion='" + updateFromVersion + '\'' +
                ", updateToVersion='" + updateToVersion + '\'' +
                ", updateDeviceCode='" + updateDeviceCode + '\'' +
                ", updateAndroidVersion='" + updateAndroidVersion + '\'' +
                ", updatePackageMD5='" + updatePackageMD5 + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return updateFileName.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof UpdateUrl) {
            return updateFileName.equalsIgnoreCase(((UpdateUrl) obj).getUpdateFileName());
        }
        return false;
    }


    public enum HandlerType {
        URL,
        FILE_NAME
    }
}
