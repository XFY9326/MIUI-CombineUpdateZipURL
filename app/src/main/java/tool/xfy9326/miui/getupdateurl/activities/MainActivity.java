package tool.xfy9326.miui.getupdateurl.activities;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import tool.xfy9326.miui.getupdateurl.tools.PropertyUtils;
import tool.xfy9326.miui.getupdateurl.tools.RootUtils;
import tool.xfy9326.miui.getupdateurl.utils.UpdatePackageType;
import tool.xfy9326.miui.getupdateurl.utils.UpdateUrl;

public class MainActivity extends AppCompatActivity {
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
        TextView currentSystemVersion = findViewById(R.id.tv_currentSystemVersion);
        currentSystemVersion.setText(getString(R.string.current_system_version, Build.MODEL, Build.DEVICE.toLowerCase(), Build.VERSION.RELEASE, Build.VERSION.SDK_INT, PropertyUtils.get(Constants.MIUI_VERSION_NAME, Build.UNKNOWN, String.class), Build.VERSION.INCREMENTAL));

        bindCheckBoxWithPreference(R.id.cb_rootMode, Constants.PREFERENCE_ROOT_MODE, Constants.DEFAULT_PREFERENCE_ROOT_MODE, (buttonView, isChecked) -> {
            if (isChecked) {
                RootUtils.isRootAvailable();
            }
        });

        final EditText shareString = findViewById(R.id.et_shareString);
        shareString.setText(sharedPreferences.getString(Constants.PREFERENCE_SHARE_FORMAT_STRING, getString(R.string.default_share_template)));

        findViewById(R.id.btn_restore).setOnClickListener(v -> shareString.setText(getString(R.string.default_share_template)));
        findViewById(R.id.btn_save).setOnClickListener(v -> {
            sharedPreferences.edit().putString(Constants.PREFERENCE_SHARE_FORMAT_STRING, shareString.getText().toString().trim()).apply();
            Toast.makeText(MainActivity.this, R.string.save_success, Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.btn_formatGuide).setOnClickListener(v -> showFormatString());

        final Spinner updateServerSpinner = findViewById(R.id.sp_updateServer);
        String[] updateServerNames = new String[Constants.UPDATE_SERVER.length];
        for (int i = 0; i < Constants.UPDATE_SERVER.length; i++) {
            updateServerNames[i] = Constants.UPDATE_SERVER[i].replace(Constants.HTTPS, Constants.EMPTY).replace(Constants.HTTP, Constants.EMPTY);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, updateServerNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        updateServerSpinner.setAdapter(adapter);
        updateServerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sharedPreferences.edit().putInt(Constants.PREFERENCE_UPDATE_SERVER, position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        updateServerSpinner.setSelection(sharedPreferences.getInt(Constants.PREFERENCE_UPDATE_SERVER, Constants.DEFAULT_PREFERENCE_UPDATE_SERVER));

        findViewById(R.id.btn_useAttention).setOnClickListener(v -> showUseAttention());
        findViewById(R.id.btn_getUpdateUrl).setOnClickListener(v -> {
            if (BaseMethod.hasNoStoragePermission(MainActivity.this)) {
                Toast.makeText(MainActivity.this, R.string.no_permission_granted, Toast.LENGTH_SHORT).show();
                BaseMethod.requestStoragePermission(MainActivity.this);
            } else {
                new Thread(() -> {
                    HashSet<UpdateUrl> updateUrls = new HashSet<>();
                    if (((CheckBox) findViewById(R.id.cb_rootMode)).isChecked()) {
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
                        runOnUiThread(() -> showPackageShare(Constants.UPDATE_SERVER[updateServerSpinner.getSelectedItemPosition()], shareString.getText().toString().trim(), updateUrls));
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

    private void showPackageShare(final String updateServer, final String formatString, final Set<UpdateUrl> updateUrls) {
        final HashSet<UpdateUrl> selectUrl = new HashSet<>(updateUrls);
        final UpdateUrl[] updateUrlList = updateUrls.toArray(new UpdateUrl[0]);

        final String[] itemList = new String[updateUrlList.length];
        final boolean[] selectList = new boolean[updateUrlList.length];
        Arrays.fill(selectList, true);

        for (int i = 0; i < updateUrlList.length; i++) {
            updateUrlList[i].setUpdateServer(updateServer);
            if (updateUrlList[i].getUpdatePackageType() == UpdatePackageType.COMPLETE) {
                itemList[i] = getString(R.string.package_info_stable, I18NMethod.getUpdatePackageType(MainActivity.this, updateUrlList[i].getUpdatePackageType()), updateUrlList[i].getUpdateToVersion());
            } else {
                itemList[i] = getString(R.string.package_info_none_stable, I18NMethod.getUpdatePackageType(MainActivity.this, updateUrlList[i].getUpdatePackageType()), updateUrlList[i].getUpdateFromVersion(), updateUrlList[i].getUpdateToVersion());
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
            String text = ShareMethod.buildShareText(MainActivity.this, selectUrl.toArray(new UpdateUrl[0]), formatString);
            ShareMethod.shareText(MainActivity.this, text);
        });
        builder.setNeutralButton(R.string.copy_to_paste_board, (dialog, which) -> {
            String text = ShareMethod.buildShareText(MainActivity.this, selectUrl.toArray(new UpdateUrl[0]), formatString);
            ShareMethod.copyToPasteBoard(MainActivity.this, text);
            Toast.makeText(MainActivity.this, R.string.copy_to_paste_board_success, Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }

    @SuppressWarnings("SameParameterValue")
    private void bindCheckBoxWithPreference(@IdRes int resId, final String prefKey, final boolean defaultValue, @Nullable CompoundButton.OnCheckedChangeListener listener) {
        CheckBox checkBox = findViewById(resId);
        checkBox.setChecked(sharedPreferences.getBoolean(prefKey, defaultValue));
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onCheckedChanged(buttonView, isChecked);
            }
            sharedPreferences.edit().putBoolean(prefKey, isChecked).apply();
        });
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.about);
        builder.setMessage(R.string.about_msg);
        builder.setPositiveButton(android.R.string.yes, null);
        builder.show();
    }

    private void showFormatString() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.format_guide);
        builder.setMessage(R.string.format_guide_msg);
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
