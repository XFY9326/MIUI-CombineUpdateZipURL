package tool.xfy9326.miui.getupdateurl;

import android.os.Environment;

import java.io.File;

public class Constants {
    public static final String XIAOMI = "Xiaomi";
    public static final String MIUI = "miui";

    public static final String UPDATE_SERVER = "https://hugeota.d.miui.com";

    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final String URL_SPILT = "/";
    public static final String FILE_TYPE_SPILT = ".";

    public static final String COMPLETE_PACKAGE_SPLIT = "_";
    public static final String OTA_OR_ROOT_PACKAGE_SPLIT = "-";
    public static final String ROOT_PACKAGE_TYPE = "root";
    public static final String OTA_OR_ROOT_PACKAGE_PREFIX = "blockota";

    public static final String PREFERENCE_ROOT_MODE = "ROOT_MODE";
    public static final String PREFERENCE_NONE_MIUI_FORCE_USE = "NONE_MIUI_FORCE_USE";

    public static final boolean DEFAULT_PREFERENCE_ROOT_MODE = false;
    public static final boolean DEFAULT_PREFERENCE_NONE_MIUI_FORCE_USE = false;

    public static final String DOWNLOAD_ROM_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "downloaded_rom";

    private static final String ANDROID_UPDATE_PACKAGE_NAME = "com.android.updater";
    private static final String UPDATE_CONF_DIR = Environment.getDataDirectory().getAbsolutePath() + File.separator + "data" + File.separator + ANDROID_UPDATE_PACKAGE_NAME + File.separator + "shared_prefs";
    private static final String UPDATE_CONF_SERVER_XML = "com.android.updater.server.xml";
    public static final String UPDATE_CONF_SERVER_XML_PATH = UPDATE_CONF_DIR + File.separator + UPDATE_CONF_SERVER_XML;
    private static final String UPDATE_CONF_PREF_XML = "com.android.updater.UPDATER_PREF.xml";
    public static final String UPDATE_CONF_PREF_XML_PATH = UPDATE_CONF_DIR + File.separator + UPDATE_CONF_PREF_XML;
    private static final String UPDATE_CONF_VERSION_JSON_XML = "version_json.xml";
    public static final String UPDATE_CONF_VERSION_JSON_XML_PATH = UPDATE_CONF_DIR + File.separator + UPDATE_CONF_VERSION_JSON_XML;
}
