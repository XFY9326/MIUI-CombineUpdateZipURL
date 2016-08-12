package miuitool.xfy9326.getupdateurl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.File;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class MainActivity extends Activity 
{
    private boolean environmentscan;
    private String[] filename;
    private String[] urlarr;
    private boolean[] selectitem;
    private int version_select;
    private boolean issecondtime = false;
    private boolean isfirstuse;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        savedataget();
        if (!environmentscan)
        {
            if (phonescan())
            {
                environmentscan = true;
                savedataset();
            }
            else
            {
                show(getString(R.string.no_xiaomi));
            }
        }
        licenseshow();
        buttonset();
        spinnerset(version_select);
    }

    private void licenseshow()
    {
        if (isfirstuse)
        {
            AlertDialog.Builder readme = new AlertDialog.Builder(this);
            readme.setTitle(getString(R.string.readme_title));
            readme.setMessage(getString(R.string.readme));
            readme.setCancelable(false);
            readme.setPositiveButton(getString(R.string.done), new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface d, int p)
                    {
                        isfirstuse = false;
                        savedataset();
                    }
                });
            readme.show();
        }
    }

    private void savedataget()
    {
        SharedPreferences data= getSharedPreferences("Data", Activity.MODE_PRIVATE);
        version_select = data.getInt("Spinner_Select", 0);
        isfirstuse = data.getBoolean("First_Use", true);
        environmentscan = data.getBoolean("XiaoMi_Device", false);
    }

    private void savedataset()
    {
        SharedPreferences data = getSharedPreferences("Data", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putInt("Spinner_Select", version_select);
        editor.putBoolean("First_Use", isfirstuse);
        editor.putBoolean("XiaoMi_Device", environmentscan);
        editor.commit();
    }

    private void spinnerset(int selectitem)
    {
        String[] arr = getResources().getStringArray(R.array.selects);
        Spinner spin = (Spinner) findViewById(R.id.version_selecter);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setSelection(selectitem);
        spin.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){  
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
                {  
                    version_select = arg2;
                    savedataset();
                }
                public void onNothingSelected(AdapterView<?> arg0)
                {}});
    }

    private void buttonset()
    {
        Button start = (Button) findViewById(R.id.start_button);
        start.setOnClickListener(new OnClickListener(){
                public void onClick(View v)
                {
                    if (environmentscan == true)
                    {
                        geturl();
                    }
                    else
                    {
                        show(getString(R.string.environment_error));
                    }
                }
            });
    }

    private void geturl()
    {
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/downloaded_rom");
        if (path.exists())
        {
            File[] list = path.listFiles();
            filename = new String[list.length];
            String[] ziparr = new String[list.length];
            String [] zipname = new String[list.length];
            for (int i = 0;i < list.length;i++)
            {
                filename[i] = "null";
                if (list[i].isFile())
                {
                    if (getExtensionName(list[i].getName().toString()).equalsIgnoreCase("zip"))
                    {
                        filename[i] = list[i].getName().toString();
                        String nametemp = filename[i].substring(0, 5);
                        if (nametemp.equalsIgnoreCase("miui_"))
                        {
                            String[] namedata = filename[i].split("_");
                            zipname[i] = namedata[2] + " " + getString(R.string.huge);
                            filename[i] = namedata[2] + " " + getString(R.string.huge) + getString(R.string.download) + "\n" + "http://bigota.d.miui.com/" + namedata[2] + "/" + filename[i];
                        }
                        else if (nametemp.equalsIgnoreCase("miui-"))
                        {
                            String[] namedata = filename[i].split("-");
                            zipname[i] = namedata[3] + "~" + namedata[4] + " " + getString(R.string.ota);
                            filename[i] = namedata[3] + "~" + namedata[4] + " " + getString(R.string.ota) + getString(R.string.download) + "\n" + "http://bigota.d.miui.com/" + namedata[4] + "/" + filename[i];
                        }
                        else
                        {
                            filename[i] = "null";
                            zipname[i] = "null";
                        }
                    }
                }
                if (filename[i] != "null" && zipname[i] != "null")
                {
                    ziparr[i] = filename[i];
                }
            }
            if (emptyarr(filename))
            {
                show(getString(R.string.no_file));
            }
            else
            {
                showdialog(ziparr, zipname);
            }
        }
        else
        {
            show(getString(R.string.no_path));
        }
    }

    private boolean emptyarr(String[] arr)
    {
        for (int i = 0;i < arr.length;i++)
        {
            if (arr[i] != "null")
            {
                return false;
            }
        }
        return true;
    }

    private boolean[] arrcreate(int len, boolean bool)
    {
        boolean[] arr=new boolean[len];
        for (int i = 0;i < len;i++)
        {
            arr[i] = bool;
        }
        return arr;
    }

    private String outputstr(String[] data, boolean[] state)
    {
        String str = "";
        String[] arr = getResources().getStringArray(R.array.selects);
        String version = arr[version_select];
        for (int i = 0;i < data.length;i++)
        {
            if (state[i])
            {
                str += data[i] + "\n\n";
            }
        }
        String output = "";
        if (version_select == 0)
        {
            output = getString(R.string.device) + " " + devicename() + " \n\n" + str;
        }
        else
        {
            output = getString(R.string.device) + " " + devicename() + " \n\n" + getString(R.string.zip_version) + version + "\n\n" + str;
        }
        if (output.length() < 2)
        {
            return "Url Get Error!!";
        }
        else
        {
            output = output.substring(0, output.length() - 2);
            return output;
        }
    }

    private void showdialog(String[] data, String[] datalist)
    {
        urlarr = data;
        if (!issecondtime)
        {
            selectitem = arrcreate(data.length, false);
        }
        issecondtime = true;
        AlertDialog.Builder result = new AlertDialog.Builder(this);
        result.setTitle(getString(R.string.result));
        result.setMultiChoiceItems(datalist, selectitem, new DialogInterface.OnMultiChoiceClickListener(){
                public void onClick(DialogInterface d, int which, boolean set)
                {
                    selectitem[which] = set;
                }
            });
        result.setPositiveButton(getString(R.string.copy), new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which)
                {
                    if (!haschoosed(selectitem))
                    {
                        show(getString(R.string.no_choose));
                    }
                    else
                    {
                        dialog.dismiss();
                        ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        cmb.setText(outputstr(urlarr, selectitem));
                        show(getString(R.string.copy_ok));
                    }
                }
            });
        result.setNegativeButton(getString(R.string.share), new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which)
                {
                    if (!haschoosed(selectitem))
                    {

                        show(getString(R.string.no_choose));
                    }
                    else
                    {
                        dialog.dismiss();
                        Intent intent=new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share));
                        intent.putExtra(Intent.EXTRA_TEXT, outputstr(urlarr, selectitem));
                        startActivity(Intent.createChooser(intent, getTitle()));
                    }
                }
            });
        result.setNeutralButton(getString(R.string.copy_all), new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int p)
                {
                    dialog.dismiss();
                    ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb.setText(outputstr(urlarr, arrcreate(selectitem.length, true)));
                    show(getString(R.string.copy_ok));
                }});
        result.show();
    }

    private boolean haschoosed(boolean[] hcarr)
    {
        boolean choosed = false;
        for (int i = 0;i < hcarr.length;i++)
        {
            if (hcarr[i] == true)
            {
                choosed = true;
                break;
            }
        }
        return choosed;
    }

    private String getExtensionName(String filename)
    { 
        if ((filename != null) && (filename.length() > 0))
        { 
            int dot = filename.lastIndexOf('.'); 
            if ((dot > -1) && (dot < (filename.length() - 1)))
            { 
                return filename.substring(dot + 1); 
            } 
        } 
        return filename; 
    }

    private boolean phonescan()
    {
        if (Build.BRAND.equalsIgnoreCase("Xiaomi"))
        {
            return true;
        }
        else
        {
            String miuisdkpkg="com.miui.core";
            String miuisyspkg="com.miui.system";
            String miuirompkg="com.miui.rom";
            if(isinstalled(miuisdkpkg)||isinstalled(miuisyspkg)||isinstalled(miuirompkg))
            {
                return true;
            }
            return false;
        }
    }

    private boolean isinstalled(String pkgname)
    {
        PackageInfo pkginfo;
        try
        {
            pkginfo = getPackageManager().getPackageInfo(pkgname, 0);
        }
        catch (NameNotFoundException e)
        {
            pkginfo = null;
        }
        if(pkginfo == null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    private void show(String str)
    {
        Toast.makeText(this, str.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater(); 
        inflater.inflate(R.menu.main, menu); 
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int item_id = item.getItemId(); 
        switch (item_id)
        { 
            case R.id.delete_all:
                File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/downloaded_rom");
                if (path.exists())
                {
                    File[] list = path.listFiles();
                    for (int i = 0;i < list.length;i++)
                    {
                        if (list[i].isFile() && getExtensionName(list[i].getName().toString()).equalsIgnoreCase("zip"))
                        {
                            File deletefile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/downloaded_rom/" + list[i].getName().toString());
                            deletefile.delete();
                        }
                    }
                    show(getString(R.string.delete_zips_ok));
                }
                else
                {
                    show(getString(R.string.no_path));
                }
                break;
            case R.id.update_info: 
                AlertDialog.Builder update = new AlertDialog.Builder(this);
                update.setTitle(R.string.update_info);
                update.setMessage(R.string.update);
                update.setPositiveButton(R.string.done, null);
                update.show();
                break; 
            case R.id.about: 
                AlertDialog.Builder about = new AlertDialog.Builder(this);
                about.setTitle(R.string.about);
                about.setMessage(R.string.about_info);
                about.setPositiveButton(R.string.done, null);
                about.show();
                break; 
        } 
        return super.onOptionsItemSelected(item);
    }

    private String devicename()
    {
        String type = Build.DEVICE.toString();
        String result;
        switch (type)
        {
            case "Mi one plus":
                result = "小米1/1S";
                break;
            case "aries":
                result = "小米2/2S";
                break;
            case "taurus":
                result = "小米2A";
                break;
            case "pisces":
                result = "小米3移动版";
                break;
            case "cancro":
                result = "小米3联通/电信 小米4 \n注：小米4电信4G版同型号不同包";
                break;
            case "ferrari":
                result = "小米4i";
                break;
            case "libra":
                result = "小米4c";
                break;
            case "aqua":
                result = "小米4s";
                break;
            case "gemini":
                result = "小米5";
                break;
            case "virgo":
                result = "小米Note";
                break;
            case "leo":
                result = "小米Note顶配版";
                break;
            case "hydrogen":
                result = "小米Max标配版";
                break;
            case "helium":
                result = "小米Max高配版";
                break;
            case "mocha":
                result = "小米平板";
                break;
            case "latte":
                result = "小米平板2";
                break;
            case "wt93007":
                result = "红米1移动版";
                break;
            case "wt98007":
                result = "红米1联通版";
                break;
            case "armani":
                result = "红米1s联通/电信";
                break;
            case "wt93807":
                result = "红米1s移动3G";
                break;
            case "wt96007":
                result = "红米1s移动4G";
                break;
            case "wt86047":
                result = "红米2移动版";
                break;
            case "wt88047":
                result = "红米2联通/电信";
                break;
            case "lte26007":
                result = "红米2A";
                break;
            case "wt86047":
                result = "红米2A高配版";
                break;
            case "ido":
                result = "红米3";
                break;
            case "land":
                result = "红米3S";
                break;
            case "omega":
                result = "红米Pro";
                break;
            case "lcsh92_wet_tdd":
                result = "红米Note移动3G";
                break;
            case "lcsh92_wet_jb9":
                result = "红米Note联通3G";
                break;
            case "dior":
                result = "红米Note 4G单卡版";
                break;
            case "gucci":
                result = "红米Note 4G双卡版";
                break;
            case "hermes":
                result = "红米Note2";
                break;
            case "hennessy":
                result = "红米Note3双网通版";
                break;
            case "kenzo":
                result = "红米Note3全网通版";
                break;
            default:
                result = type;
                break;
        }
        return result;
    }

}
