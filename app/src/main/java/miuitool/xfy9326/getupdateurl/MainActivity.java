package miuitool.xfy9326.getupdateurl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

public class MainActivity extends Activity 
{
    private boolean environmentscan = false;
    private String[] filename;
    private String urlstr;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        if (phonescan())
        {
            environmentscan = true;
        }
        else
        {
            show(getString(R.string.no_xiaomi));
        }
        buttonset();
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
            String str = "";
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
                            filename[i] = namedata[2] + " " + getString(R.string.huge) + "\n" + "http://bigota.d.miui.com/" + namedata[2] + "/" + filename[i];
                        }
                        else if (nametemp.equalsIgnoreCase("miui-"))
                        {
                            String[] namedata = filename[i].split("-");
                            filename[i] = namedata[3] + "~" + namedata[4] + " " + getString(R.string.ota) + "\n" + "http://bigota.d.miui.com/" + namedata[4] + "/" + filename[i];
                        }
                        else
                        {
                            filename[i] = "null";
                        }
                    }
                }
                if (filename[i] != "null")
                {
                    str += filename[i] + "\n\n";
                }
            }
            if (emptyarr(filename))
            {
                show(getString(R.string.no_file));
            }
            else
            {
                String output = getString(R.string.device) + " " + Build.DEVICE.toString() + " \n\n" + str;
                output = output.substring(0, output.length() - 2);
                showdialog(output);
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

    private void showdialog(String data)
    {
        urlstr = data;
        AlertDialog.Builder result = new AlertDialog.Builder(this);
        result.setTitle(getString(R.string.result));
        result.setMessage(urlstr);
        result.setPositiveButton(getString(R.string.copy), new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                    ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb.setText(urlstr);
                    show(getString(R.string.copy_ok));
                }
            });
        result.setNegativeButton(getString(R.string.share), new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                    Intent intent=new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share));
                    intent.putExtra(Intent.EXTRA_TEXT, urlstr);
                    startActivity(Intent.createChooser(intent, getTitle()));
                }
            });    
        result.show();
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
            return false;
        }
    }

    private void show(String str)
    {
        Toast.makeText(this, str.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // TODO: Implement this method
        MenuInflater inflater = getMenuInflater(); 
        inflater.inflate(R.menu.main, menu); 
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO: Implement this method
        int item_id = item.getItemId(); 
        switch (item_id)
        { 
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


}
