package tool.xfy9326.miui.getupdateurl.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import tool.xfy9326.miui.getupdateurl.BuildConfig;
import tool.xfy9326.miui.getupdateurl.Constants;
import tool.xfy9326.miui.getupdateurl.R;
import tool.xfy9326.miui.getupdateurl.methods.BaseMethod;
import tool.xfy9326.miui.getupdateurl.methods.I18NMethod;
import tool.xfy9326.miui.getupdateurl.methods.ShareMethod;
import tool.xfy9326.miui.getupdateurl.methods.UrlMethod;
import tool.xfy9326.miui.getupdateurl.tools.RootUtils;
import tool.xfy9326.miui.getupdateurl.utils.UpdatePackageType;
import tool.xfy9326.miui.getupdateurl.utils.UpdateUrl;

public class MainActivity extends Activity {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        viewInit();
        checkInit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        for (int i = 0; i < menu.size(); i++) {
            if (menu.getItem(i).getItemId() == R.id.menu_current_version) {
                menu.getItem(i).setTitle(getString(R.string.current_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
                break;
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_about) {
            showAboutDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == BaseMethod.STORAGE_PERMISSION_REQUEST_CODE) {
            boolean grantPermissionSuccess = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    String permissionLabel;
                    try {
                        permissionLabel = getPackageManager().getPermissionInfo(permissions[i], 0).loadLabel(getPackageManager()).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        permissionLabel = permissions[i];
                    }
                    Toast.makeText(this, getString(R.string.permission_grant_failed, permissionLabel), Toast.LENGTH_SHORT).show();
                    grantPermissionSuccess = false;
                    break;
                }
            }
            if (grantPermissionSuccess) {
                Toast.makeText(this, R.string.permission_grant_success, Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void viewInit() {
        final CheckBox rootModeCheckBox = findViewById(R.id.cb_rootMode);
        rootModeCheckBox.setChecked(sharedPreferences.getBoolean(Constants.PREFERENCE_ROOT_MODE, Constants.DEFAULT_PREFERENCE_ROOT_MODE));
        rootModeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                RootUtils.isRootAvailable();
            }
            sharedPreferences.edit().putBoolean(Constants.PREFERENCE_ROOT_MODE, isChecked).apply();
        });

        findViewById(R.id.btn_useAttention).setOnClickListener(v -> showUseAttention());
        findViewById(R.id.btn_getUpdateUrl).setOnClickListener(v -> {
            if (BaseMethod.hasNoStoragePermission(MainActivity.this)) {
                Toast.makeText(MainActivity.this, R.string.no_permission_granted, Toast.LENGTH_SHORT).show();
                BaseMethod.requestStoragePermission(MainActivity.this);
            } else {
                new Thread(() -> {
                    HashSet<UpdateUrl> updateUrls = new HashSet<>();
                    if (rootModeCheckBox.isChecked()) {
                        if (RootUtils.isRootAvailable()) {
                            updateUrls.addAll(UrlMethod.getUrlFromSystemFile());
                        } else {
                            Looper.loop();
                            Toast.makeText(MainActivity.this, R.string.no_root_available, Toast.LENGTH_SHORT).show();
                            Looper.prepare();
                            return;
                        }
                    }
                    updateUrls.addAll(UrlMethod.getUrlFromDownloadFile());
                    if (updateUrls.size() == 0) {
                        Looper.loop();
                        Toast.makeText(MainActivity.this, R.string.no_update_package_found, Toast.LENGTH_SHORT).show();
                        Looper.prepare();
                    } else {
                        runOnUiThread(() -> showPackageShare(updateUrls));
                    }
                }).start();
                Toast.makeText(MainActivity.this, R.string.loading, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkInit() {
        if (!BaseMethod.isMIUI() && !sharedPreferences.getBoolean(Constants.PREFERENCE_NONE_MIUI_FORCE_USE, Constants.DEFAULT_PREFERENCE_NONE_MIUI_FORCE_USE)) {
            showMiuiSystemAttention();
        }
        if (BaseMethod.hasNoStoragePermission(this)) {
            BaseMethod.requestStoragePermission(this);
        }
    }

    private void showPackageShare(final Set<UpdateUrl> updateUrls) {
        final HashSet<UpdateUrl> selectUrl = new HashSet<>(updateUrls);
        final UpdateUrl[] updateUrlList = updateUrls.toArray(new UpdateUrl[0]);

        final String[] itemList = new String[updateUrlList.length];
        final boolean[] selectList = new boolean[updateUrlList.length];
        Arrays.fill(selectList, true);

        for (int i = 0; i < updateUrlList.length; i++) {
            if (updateUrlList[i].getUpdatePackageType() == UpdatePackageType.COMPLETE) {
                itemList[i] = getString(R.string.package_info_stable, I18NMethod.getUpdatePackageType(MainActivity.this, updateUrlList[i].getUpdatePackageType()), updateUrlList[i].getUpdateToVersion(), updateUrlList[i].getUpdateAndroidVersion());
            } else {
                itemList[i] = getString(R.string.package_info_none_stable, I18NMethod.getUpdatePackageType(MainActivity.this, updateUrlList[i].getUpdatePackageType()), updateUrlList[i].getUpdateFromVersion(), updateUrlList[i].getUpdateToVersion(), updateUrlList[i].getUpdateAndroidVersion());
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.package_share_title);
        builder.setMultiChoiceItems(itemList, selectList, (d, which, set) -> {
            if (set) {
                selectUrl.add(updateUrlList[which]);
            } else {
                selectUrl.remove(updateUrlList[which]);
            }
        });
        builder.setPositiveButton(R.string.share, (dialog, which) -> {
            String text = ShareMethod.buildShareText(MainActivity.this, selectUrl.toArray(new UpdateUrl[0]));
            ShareMethod.shareText(MainActivity.this, text);
        });
        builder.setNeutralButton(R.string.copy_to_paste_board, (dialog, which) -> {
            String text = ShareMethod.buildShareText(MainActivity.this, selectUrl.toArray(new UpdateUrl[0]));
            ShareMethod.copyToPasteBoard(MainActivity.this, text);
            Toast.makeText(MainActivity.this, R.string.copy_to_paste_board_success, Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.about);
        builder.setMessage(R.string.about_msg);
        builder.setPositiveButton(android.R.string.yes, null);
        builder.show();
    }

    private void showUseAttention() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.use_attention_title);
        builder.setMessage(R.string.use_attention_msg);
        builder.setPositiveButton(android.R.string.yes, null);
        builder.show();
    }

    private void showMiuiSystemAttention() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.attention);
        builder.setCancelable(false);
        builder.setMessage(R.string.not_miui_system_attention);
        builder.setPositiveButton(android.R.string.yes, (dialog, which) -> sharedPreferences.edit().putBoolean(Constants.PREFERENCE_NONE_MIUI_FORCE_USE, true).apply());
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> finish());
        builder.show();
    }
}
