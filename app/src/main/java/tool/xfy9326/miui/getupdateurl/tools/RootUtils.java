package tool.xfy9326.miui.getupdateurl.tools;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class RootUtils {
    private static final String SU_COMMAND = "su";
    private static final String EXIT_COMMAND = "exit";
    private static final String LS_L_COMMAND = "ls -l ";
    private static final String FIND_COMMAND = "find ";
    private static final String CAT_COMMAND = "cat ";
    private static final String ENTER_COMMAND = "\n";

    private static final String SU_BIN_PATH = "/system/bin/su";
    private static final String SU_XBIN_PATH = "/system/xbin/su";

    private static final char EXECUTABLE_FLAG_S = 's';
    private static final char EXECUTABLE_FLAG_X = 'x';

    public static boolean isFileExist(String path) {
        try {
            return executeCommandWithRoot(FIND_COMMAND + path).length() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @NonNull
    public static String readFile(String path) throws IOException, InterruptedException {
        return executeCommandWithRoot(CAT_COMMAND + path);
    }

    @NonNull
    private static String executeCommandWithRoot(String command) throws IOException, InterruptedException {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(SU_COMMAND);
            final InputStream inputStream = process.getInputStream();
            final StringBuffer buffer = new StringBuffer();

            Thread outputThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line).append("\n");
                    }
                    if (buffer.length() > 0) {
                        buffer.deleteCharAt(buffer.length() - 1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            outputThread.start();

            try (OutputStream outputStream = process.getOutputStream()) {
                outputStream.write((command + ENTER_COMMAND).getBytes());
                outputStream.flush();
            }

            process.waitFor();
            outputThread.join();

            return buffer.toString();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    public static boolean isRootAvailable() {
        if (!new File(SU_BIN_PATH).exists() && isExecutable(SU_BIN_PATH) || new File(SU_XBIN_PATH).exists() && isExecutable(SU_XBIN_PATH)) {
            Process process = null;
            try {
                process = Runtime.getRuntime().exec(SU_COMMAND);
                try (OutputStream outputStream = process.getOutputStream()) {
                    outputStream.write((EXIT_COMMAND + ENTER_COMMAND).getBytes());
                    outputStream.flush();
                }
                return process.waitFor() == 0;
            } catch (Exception e) {
                return false;
            } finally {
                if (process != null) {
                    process.destroy();
                }
            }
        }
        return true;
    }

    private static boolean isExecutable(String filePath) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(LS_L_COMMAND + filePath);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String str = reader.readLine();
                if (str != null && str.length() >= 4) {
                    char flag = str.charAt(3);
                    if (flag == EXECUTABLE_FLAG_S || flag == EXECUTABLE_FLAG_X)
                        return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }

}
