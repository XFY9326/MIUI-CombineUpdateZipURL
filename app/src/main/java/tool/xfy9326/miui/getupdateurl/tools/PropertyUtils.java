package tool.xfy9326.miui.getupdateurl.tools;

import android.annotation.SuppressLint;

import java.lang.reflect.Method;

@SuppressLint("PrivateApi")
public class PropertyUtils {
    private static final String CLASS_SYSTEM_PROPERTIES = "android.os.SystemProperties";
    private static final String METHOD_GET = "get";

    public static <T> T get(String key, T def, Class<T> castClass) {
        T value = def;
        try {
            Class<?> propertiesClazz = Class.forName(CLASS_SYSTEM_PROPERTIES);
            Method get = propertiesClazz.getDeclaredMethod(METHOD_GET, String.class, castClass);
            value = cast(get.invoke(null, key, def));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    private static <T> T cast(Object obj) {
        return (T) obj;
    }
}
