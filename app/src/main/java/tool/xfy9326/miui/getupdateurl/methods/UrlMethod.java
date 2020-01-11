package tool.xfy9326.miui.getupdateurl.methods;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import tool.xfy9326.miui.getupdateurl.Constants;
import tool.xfy9326.miui.getupdateurl.tools.PrefStringReader;
import tool.xfy9326.miui.getupdateurl.tools.RootUtils;
import tool.xfy9326.miui.getupdateurl.utils.UpdateUrl;

public class UrlMethod {
    private static final String[] PREF_XML_KEYS = {"current_rom_file_name", "last_download_file"};
    private static final String[] SERVER_XML_KEYS = {"rom_url"};
    private static final String[] VERSION_JSON_XML_KEYS = {"current_version", "new_version"};
    private static final String VERSION_JSON_KEY_LATEST_ROM = "LatestRom";
    private static final String VERSION_JSON_KEY_FILE_NAME = "filename";

    private static final String MIUI_DOWNLOAD_TEMP_PREFIX = ".cfg";

    public static Set<UpdateUrl> getUrlFromDownloadFile() {
        HashSet<UpdateUrl> updateUrls = new HashSet<>();
        File file = new File(Constants.DOWNLOAD_ROM_DIR);
        if (file.exists() && file.isDirectory()) {
            File[] childFiles = file.listFiles((dir, name) -> name.startsWith(Constants.MIUI));
            if (childFiles != null) {
                for (File childFile : childFiles) {
                    String fileName = childFile.getName();
                    if (fileName.endsWith(MIUI_DOWNLOAD_TEMP_PREFIX)) {
                        fileName = fileName.substring(0, fileName.lastIndexOf(MIUI_DOWNLOAD_TEMP_PREFIX));
                    }
                    updateUrls.add(new UpdateUrl(UpdateUrl.HandlerType.FILE_NAME, fileName));
                }
            }
        }
        return updateUrls;
    }

    public static Set<UpdateUrl> getUrlFromSystemFile() {
        HashSet<UpdateUrl> updateUrls = new HashSet<>();
        if (RootUtils.isRootAvailable()) {
            try {
                if (RootUtils.isFileExist(Constants.UPDATE_CONF_PREF_XML_PATH)) {
                    String content = RootUtils.readFile(Constants.UPDATE_CONF_PREF_XML_PATH);
                    updateUrls.addAll(getUrlFromPrefXml(content));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (RootUtils.isFileExist(Constants.UPDATE_CONF_SERVER_XML_PATH)) {
                    String content = RootUtils.readFile(Constants.UPDATE_CONF_SERVER_XML_PATH);
                    updateUrls.addAll(getUrlFromServerXml(content));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (RootUtils.isFileExist(Constants.UPDATE_CONF_VERSION_JSON_XML_PATH)) {
                    String content = RootUtils.readFile(Constants.UPDATE_CONF_VERSION_JSON_XML_PATH);
                    updateUrls.addAll(getUrlFromVersionJsonXml(content));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return updateUrls;
    }

    private static Set<UpdateUrl> getUrlFromPrefXml(String content) throws ParserConfigurationException, IOException, SAXException {
        HashSet<UpdateUrl> updateUrls = new HashSet<>(PREF_XML_KEYS.length);
        PrefStringReader reader = new PrefStringReader(content);
        for (String prefXmlKey : PREF_XML_KEYS) {
            String value = reader.getString(prefXmlKey);
            if (value != null) {
                updateUrls.add(new UpdateUrl(UpdateUrl.HandlerType.FILE_NAME, value));
            }
        }
        return updateUrls;
    }

    private static Set<UpdateUrl> getUrlFromServerXml(String content) throws ParserConfigurationException, IOException, SAXException {
        HashSet<UpdateUrl> updateUrls = new HashSet<>(SERVER_XML_KEYS.length);
        PrefStringReader reader = new PrefStringReader(content);
        for (String serverXmlKey : SERVER_XML_KEYS) {
            String value = reader.getString(serverXmlKey);
            if (value != null) {
                updateUrls.add(new UpdateUrl(UpdateUrl.HandlerType.URL, value));
            }
        }
        return updateUrls;
    }

    private static Set<UpdateUrl> getUrlFromVersionJsonXml(String content) throws ParserConfigurationException, IOException, SAXException, JSONException {
        HashSet<UpdateUrl> updateUrls = new HashSet<>(VERSION_JSON_XML_KEYS.length);
        PrefStringReader reader = new PrefStringReader(content);
        for (String versionJsonXmlKey : VERSION_JSON_XML_KEYS) {
            String value = reader.getString(versionJsonXmlKey);
            if (value != null) {
                String fileName = new JSONObject(value).getJSONObject(VERSION_JSON_KEY_LATEST_ROM).getString(VERSION_JSON_KEY_FILE_NAME);
                updateUrls.add(new UpdateUrl(UpdateUrl.HandlerType.FILE_NAME, fileName));
            }
        }
        return updateUrls;
    }
}
