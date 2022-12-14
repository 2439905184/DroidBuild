package com.application.developer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.android.sdklib.build.ApkBuilder;
import com.android.sdklib.build.ApkCreationException;
import com.android.sdklib.build.SealedApkException;
import com.application.developer.SettingActivity;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import kellinwood.security.zipsigner.ZipSigner;
import org.eclipse.jdt.internal.compiler.batch.Main;
import android.util.Log;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View;
import java.util.List;
import android.widget.ListView;
import java.util.LinkedList;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import com.android.dx.dex.code.PositionList;
import android.content.DialogInterface;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.KeyEvent;
import com.tendcloud.tenddata.TCAgent;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.net.Uri;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.Manifest;

public class MainActivity extends Activity
{
    public static final String TAG = "MainActivity.Compile";
    private List<AppList> mData = null;
    private Context mContext;
    private AppAdapter mAdapter = null;
    private ListView list_animal;
    Dialog dialog, dialog1, dialog2;
    private String gxdz;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        setup_aapt();
        init_gui();
        init_talking_data();
    }

    //??????Menu?????? menu_main.xml
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    //??????Menu??????????????????
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.cation:
                //dialog??????
                AlertDialog.Builder build = new AlertDialog.Builder(this);
                build.setTitle("????????????");
                LayoutInflater lay = this.getLayoutInflater();
                View v = lay.inflate(R.layout.new_project,null);
                build.setView(v);
                final TextView name = (TextView) v.findViewById(R.id.newprojectname);
                final TextView pack = (TextView) v.findViewById(R.id.newprojectpackage);
                build.setPositiveButton("??????", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface p1, int p2)
                    {
                        if(name.getText().toString().equals(""))
                        {
                            Toast.makeText(MainActivity.this,"?????????????????????",Toast.LENGTH_SHORT).show();
                        }
                        else if(pack.getText().toString().equals(""))
                        {
                            Toast.makeText(MainActivity.this,"??????????????????",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            try
                            {
                                //???assets???????????????zip??????
                                String path = "/storage/emulated/0/Application/Project/"+name.getText().toString();
                                ZipUtils.UnZipAssetsFolder(MainActivity.this,"MySoft.sof",path);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            try
                            {
                                //????????????AndroidManifest.xml?????????strings.xml?????????
                                String manifest = FileUtils.getString("/storage/emulated/0/Application/Project/"+name.getText().toString()+"/","AndroidManifest.xml");
                                String strings = FileUtils.getString("/storage/emulated/0/Application/Project/"+name.getText().toString()+"/res/values/","strings.xml");
                                String main = FileUtils.getString("/storage/emulated/0/Application/Project/"+name.getText().toString()+"/src/","Main.soft");
                                String manifest1 = manifest.replace("$APP_PACKAGE$",pack.getText().toString());
                                String strings1 = strings.replace("$APP_NAME$",name.getText().toString());
                                String main1 = main.replace("$APP_PACKAGE$",pack.getText().toString());
                                FileUtils.modifyFile("/storage/emulated/0/Application/Project/"+name.getText().toString()+"/AndroidManifest.xml",manifest1,false);
                                FileUtils.modifyFile("/storage/emulated/0/Application/Project/"+name.getText().toString()+"/res/values/strings.xml",strings1,false);
                                FileUtils.modifyFile("/storage/emulated/0/Application/Project/"+name.getText().toString()+"/src/Main.soft",main1,false);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            try
                            {
                                //??????project????????????????????????
                                mContext = MainActivity.this;
                                list_animal = (ListView) findViewById(R.id.applistview);
                                list_animal.setEmptyView(findViewById(R.id.appnoproject));
                                mData = new LinkedList<AppList>();
                                List<String> lists = FileUtils.readFolders(new File("/storage/emulated/0/Application/Project"));
                                for (String s : lists)
                                {
                                    if (FileUtils.isProjectPackage(new File(s)))
                                    {
                                        String temp1 = "/AndroidManifest.xml";
                                        String temp2 = "/res/values/strings.xml";
                                        String temp3 = "/res/drawable/ic_launcher.png";
                                        String apppackage = FileUtils.readFileContent(new File(s + temp1));
                                        apppackage = FileUtils.getSubString(apppackage, "package=\"", "\"");
                                        String appname = FileUtils.readFileContent(new File(s + temp2));
                                        appname = FileUtils. getSubString(appname, "name=\"app_name\">", "</string>");
                                        temp3 = s+temp3;
                                        mData.add(new AppList(appname, apppackage, temp3, s));
                                    }
                                }
                                mAdapter = new AppAdapter((LinkedList<AppList>) mData, mContext);
                                list_animal.setAdapter(mAdapter);
                                Toast.makeText(MainActivity.this,"????????????",Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                build.setNegativeButton("??????", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface p1, int p2)
                    {
                        dialog.dismiss();
                    }
                });
                dialog = build.create();
                dialog.show();
                return true;
            case R.id.sort:
                Intent sort = new Intent();
                sort.setClass(this,AlreadyActivity.class);
                startActivity(sort);
                return true;
            case R.id.setting:
                Intent setting = new Intent();
                setting.setClass(this,SettingActivity.class);
                startActivity(setting);
                return true;
            case R.id.exit:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //onRestart()??????????????????
    @Override
    protected void onRestart()
    {
        super.onRestart();
        try
        {
            //??????project????????????????????????
            mContext = MainActivity.this;
            list_animal = (ListView) findViewById(R.id.applistview);
            list_animal.setEmptyView(findViewById(R.id.appnoproject));
            mData = new LinkedList<AppList>();
            List<String> lists = FileUtils.readFolders(new File("/storage/emulated/0/Application/Project"));
            for (String s : lists)
            {
                if (FileUtils.isProjectPackage(new File(s)))
                {
                    String temp1 = "/AndroidManifest.xml";
                    String temp2 = "/res/values/strings.xml";
                    String temp3 = "/res/drawable/ic_launcher.png";
                    String apppackage = FileUtils.readFileContent(new File(s + temp1));
                    apppackage = FileUtils.getSubString(apppackage, "package=\"", "\"");
                    String appname = FileUtils.readFileContent(new File(s + temp2));
                    appname = FileUtils. getSubString(appname, "name=\"app_name\">", "</string>");
                    temp3 = s+temp3;
                    mData.add(new AppList(appname, apppackage, temp3, s));
                }
            }
            mAdapter = new AppAdapter((LinkedList<AppList>) mData, mContext);
            list_animal.setAdapter(mAdapter);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //?????????????????????
    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder Builder = new AlertDialog.Builder(this);
        Builder.setTitle("?????????????????????");
        Builder.setNeutralButton("??????", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                finish();
            }
        });
        Builder.setNegativeButton("??????", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {

            }
        });
        Builder.show();
    }
    //???????????????
    public void requestPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 100);
        }
    }

    public void setup_aapt() {
        //aapt????????????
        File file = new File("/data/data/com.application.developer/files/aapt");
        File files = new File("/data/data/com.application.developer/files/android.jar");
        //??????aapt?????????????????????????????????????????????aapt???????????????
        if (!file.exists() && !files.exists()) {
            try {
                InputStream input = getResources().getAssets().open("aapt");
                FileOutputStream out = this.openFileOutput("aapt", Context.MODE_PRIVATE);
                byte[] b = new byte[input.available()];
                InputStream inputs = getResources().getAssets().open("android.jar");
                FileOutputStream outs = this.openFileOutput("android.jar", Context.MODE_PRIVATE);
                byte[] bs = new byte[inputs.available()];
                inputs.read(bs);
                outs.write(bs);
                inputs.close();
                outs.close();
                input.read(b);
                out.write(b);
                input.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //???????????????
            file.setExecutable(true);
            files.setExecutable(true);
        }
    }

    public void init_gui()
    {
        //Dialog??????
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setTitle("??????");
        build.setMessage("????????????Application???\n\nApplication??????????????????????????????IDE???????????????Android????????????????????????Android????????????????????????????????????????????????????????????????????????????????????????????????????????????\n\nApplication??????Soft&XML???Android SDK????????????????????????Java&XML???Android SDK????????????????????????????????????Application?????????Soft???Java?????????????????????\n\nApplication??????????????????????????????Soft????????????????????????????????????????????????????????????Soft???????????????Java???????????????????????????Java?????????????????????????????????????????????????????????????????????\n\nApplication??????????????????????????????Soft???????????????????????????Soft???????????????Soft?????????????????????\n\nApplication?????????????????????1.0??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????Application??????????????????????????????????????????????????????????????????????????????\n\n??????????????????\n??????QQ???422584084\n???????????????wwwanghua@outlook.com\nApplication??????QQ????????????737444923");
        build.setCancelable(false);
        build.setPositiveButton("??????", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface p1, int p2) {
                try {
                    //??????project????????????????????????
                    mContext = MainActivity.this;
                    list_animal = (ListView) findViewById(R.id.applistview);
                    list_animal.setEmptyView(findViewById(R.id.appnoproject));
                    mData = new LinkedList<AppList>();
                    List<String> lists = FileUtils.readFolders(new File("/storage/emulated/0/Application/Project"));
                    for (String s : lists) {
                        if (FileUtils.isProjectPackage(new File(s))) {
                            String temp1 = "/AndroidManifest.xml";
                            String temp2 = "/res/values/strings.xml";
                            String temp3 = "/res/drawable/ic_launcher.png";
                            String apppackage = FileUtils.readFileContent(new File(s + temp1));
                            apppackage = FileUtils.getSubString(apppackage, "package=\"", "\"");
                            String appname = FileUtils.readFileContent(new File(s + temp2));
                            appname = FileUtils.getSubString(appname, "name=\"app_name\">", "</string>");
                            temp3 = s + temp3;
                            mData.add(new AppList(appname, apppackage, temp3, s));
                        }
                    }
                    mAdapter = new AppAdapter((LinkedList<AppList>) mData, mContext);
                    list_animal.setAdapter(mAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        dialog = build.create();
        dialog.show();
        //Toast.makeText(this,"Welcome to Application!",Toast.LENGTH_SHORT).show();
        try {
            //??????project????????????????????????
            mContext = MainActivity.this;
            list_animal = (ListView) findViewById(R.id.applistview);
            list_animal.setEmptyView(findViewById(R.id.appnoproject));
            mData = new LinkedList<AppList>();
            List<String> lists = FileUtils.readFolders(new File("/storage/emulated/0/Application/Project"));
            for (String s : lists) {
                if (FileUtils.isProjectPackage(new File(s))) {
                    String temp1 = "/AndroidManifest.xml";
                    String temp2 = "/res/values/strings.xml";
                    String temp3 = "/res/drawable/ic_launcher.png";
                    String apppackage = FileUtils.readFileContent(new File(s + temp1));
                    apppackage = FileUtils.getSubString(apppackage, "package=\"", "\"");
                    String appname = FileUtils.readFileContent(new File(s + temp2));
                    appname = FileUtils.getSubString(appname, "name=\"app_name\">", "</string>");
                    temp3 = s + temp3;
                    mData.add(new AppList(appname, apppackage, temp3, s));
                }
            }
            mAdapter = new AppAdapter((LinkedList<AppList>) mData, mContext);
            list_animal.setAdapter(mAdapter);
        } catch (Exception e) {e.printStackTrace();}

        //??????application??????
        mContext = MainActivity.this;
        list_animal =(ListView) findViewById(R.id.applistview);
        mData =new LinkedList<AppList>();
        mData.add(new AppList("MyApp","com.application.my","R.drawable.ic_launcher",null));
        mData.add(new AppList("MyApp1","com.application.my1","R.drawable.ic_launcher",null));
        mData.add(new AppList("MyApp2","com.application.my2","R.drawable.ic_launcher",null));
        mData.add(new AppList("MyApp3","com.application.my3","R.drawable.ic_launcher",null));
        mData.add(new AppList("MyApp4","com.application.my4","R.drawable.ic_launcher",null));
        mAdapter =new AppAdapter((LinkedList<AppList>) mData,mContext);
        list_animal.setAdapter(mAdapter);
        //ListView????????????
        list_animal.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick (AdapterView < ? > p1, View p2,int p3, long p4)
            {
                AppList appname = mData.get(p3);
                Intent intent = new Intent(MainActivity.this, FilesActivity.class);
                intent.putExtra("AppName", appname.getappName());
                intent.putExtra("Path", appname.getappPath());
                startActivity(intent);
            }
    });
        //ListView????????????
        list_animal.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick (AdapterView < ? > p1, View p2,int p3, long p4)
            {
                AppList datails = mData.get(p3);
                Intent intent = new Intent(MainActivity.this, DatailsActivity.class);
                intent.putExtra("Icon", datails.getappIcon());
                intent.putExtra("Name", datails.getappName());
                intent.putExtra("Package", datails.getappPackage());
                intent.putExtra("Path", datails.getappPath());
                startActivity(intent);
                return true;
            }
    });
}
//2022/8/10 ??????????????????????????????????????????????????????
public void init_talking_data() {
    //?????????TalkingData
    /*TCAgent.LOG_ON = true;
    TCAgent.init(this, "881EAF8519DF4165B2C110FF54E11C20", "Android");
    TCAgent.setReportUncaughtExceptions(true);*/
    //????????????????????????????????????
    //PackageManager ver = getPackageManager();
    //???????????????????????????
    /*Button compile = (Button) findViewById(R.id.compilerun);
    compile.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View p1) {
            if (aapt())
            {
                if (ecj())
                {
                    if (dex())
                    {
                        if (sdklib())
                        {
                            if (zipSigner())
                            {
                                Toast.makeText(MainActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(MainActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "zipSigner??????");
                        } else
                            Toast.makeText(MainActivity.this, "??????apk??????", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "sdklib??????");
                    } else
                        Toast.makeText(MainActivity.this, "dex????????????", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "dex??????");
                } else
                    Toast.makeText(MainActivity.this, "Java????????????", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "ecj??????");
            } else
                Toast.makeText(MainActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "aapt??????");
        }
    });*/
    //???????????????????????? ???bug???????????????????????????
        /*try
        {
            PackageInfo sion = ver.getPackageInfo(getPackageName(),0);
            String version = sion.versionName;
            String versions = GetHttps.getHtml("http://app.huayi-w.cn/application/config.txt");
            String bbh = FileUtils.getSubString(versions, "<version>", "</version>");
            String qzgx = FileUtils.getSubString(versions, "<forceupdate>", "</forceupdate>");
            String qzgg = FileUtils.getSubString(versions, "<display>", "</display>");
            String ggnr = FileUtils.getSubString(versions, "<bulletintext>", "</bulletintext>");
            String gxnr = FileUtils.getSubString(versions, "<updatetext>", "</updatetext>");
            gxdz = FileUtils.getSubString(versions, "<updateurl>", "</updateurl>");
            String gxnr1 = gxnr.replace("???","\n");
            String ggnr1 = ggnr.replace("???","\n");
            if (Double.parseDouble(version) < Double.parseDouble(bbh))
            {
                if (qzgx.equals("true"))
                {
                    AlertDialog.Builder build1 = new AlertDialog.Builder(MainActivity.this);
                    build1.setTitle("???????????????");
                    build1.setMessage(gxnr1);
                    build1.setCancelable(false);
                    build1.setPositiveButton("????????????", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface p1, int p2)
                            {
                                finish();
                                Intent intent_d= new Intent(); 
                                intent_d.setAction("android.intent.action.VIEW"); 
                                Uri content_url = Uri.parse(gxdz); 
                                intent_d.setData(content_url); startActivity(intent_d);
                            }
                        });
                    dialog1 = build1.create();
                    dialog1.show();
                }
                else
                {
                    AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
                    build.setTitle("???????????????");
                    build.setMessage(gxnr1);
                    build.setPositiveButton("????????????", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface p1, int p2)
                            {
                                Intent intent_d= new Intent(); 
                                intent_d.setAction("android.intent.action.VIEW"); 
                                Uri content_url = Uri.parse(gxdz); 
                                intent_d.setData(content_url); startActivity(intent_d);
                            }
                        });
                    build.setNegativeButton("??????", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface p1, int p2)
                            {
                                dialog.dismiss();
                            }
                        });
                    dialog = build.create();
                    dialog.show();
                }
            }
            else
            {
                if (qzgg.equals("true"))
                {
                AlertDialog.Builder build1 = new AlertDialog.Builder(MainActivity.this);
                build1.setTitle("??????");
                build1.setMessage(ggnr1);
                build1.setCancelable(false);
                build1.setPositiveButton("??????", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface p1, int p2)
                        {
                            dialog2.dismiss();
                        }
                    });
                dialog2 = build1.create();
                dialog2.show();
                }
            }
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }*/
}
    //??????aapt????????????
    public boolean aapt()
    {
        String[] args = {
                //aapt????????????
                        "data/data/com.application.developer/files/aapt",
                        //??????aapt????????????
                        "package","-v","-f","-m",
                        //res???????????????
                        "-S","/storage/emulated/0/AppProjects/Test/app/src/main/res/",
                        //gen???????????????
                        "-J","/storage/emulated/0/AppProjects/Test/app/build/gen/",
                        //assets???????????????????????????????????????????????????
                        "-A","/storage/emulated/0/AppProjects/Test/app/src/main/assets/",
                        //AndroidManifest.xml????????????
                        "-M","/storage/emulated/0/AppProjects/Test/app/src/main/AndroidManifest.xml",
                        //android.jar????????????
                        "-I","/storage/emulated/0/.aide/android.jar",
                        //??????.ap_????????????
                        "-F","/storage/emulated/0/AppProjects/Test/app/build/bin/resources.ap_"
                };
        try
        {
            Process process = Runtime.getRuntime().exec(args);
            int code = process.waitFor();
            //??????????????????????????????
            InputStream input=process.getInputStream();
            //??????????????????
            InputStream error=process.getErrorStream();
            //??????????????????
            if (code!=0)
                return false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //??????ecj??????java
    public boolean ecj()
    {
        //????????????
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        //????????????
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        Main main = new Main(new PrintWriter(baos1), new PrintWriter(baos2), false, null, null);
        //???????????????????????????????????????
        //Main main=new Main(new PrintWriter(System.out),new PrintWriter(System.err),false,null,null);
        String[] args =
                {
                        "-verbose",
                        //?????????jar??????????????????
                        "-extdirs", "/storage/emulated/0/AppProjects/Test/app/libs/",
                        //android.jar????????????
                        "-bootclasspath", "/storage/emulated/0/.aide/android.jar",
                        //java??????????????????
                        "-classpath", "/storage/emulated/0/AppProjects/Test/app/src/main/java/" + ":" +
                        //r.java??????????????????
                        "/storage/emulated/0/AppProjects/Test/app/build/gen/" + ":" +
                        //?????????jar????????????????????????????????????????????????jar????????????????????????????????????????????????
                        "/storage/emulated/0/AppProjects/Test/app/libs/",
                        "-1.6",
                        "-target", "1.6",
                        "-proc:none",
                        //class??????????????????
                        "-d", "/storage/emulated/0/AppProjects/Test/app/build/bin/classes/",
                        //?????????????????????java??????
                        "/storage/emulated/0/AppProjects/Test/app/src/main/java/com/huayi/test/MainActivity.java"
                };
        //???????????????????????????
        boolean b = main.compile(args);
        //??????????????????????????????
        //???????????????????????????
        String s1 = baos1.toString();
        //???????????????????????????
        String s2 = baos2.toString();
        return b;
    }
    
    //??????dx??????dex??????
    public boolean dex()
    {
        String[] args =
        {
            "--verbose",
            //?????????
            "--num-threads="+Runtime.getRuntime().availableProcessors(),
            //classes.dex??????????????????
            "--output="+"/storage/emulated/0/AppProjects/Test/app/build/bin/classes.dex",
            //class??????????????????
            "/storage/emulated/0/AppProjects/Test/app/build/bin/classes/",
            //????????????????????????jar?????????????????????
            "/storage/emulated/0/AppProjects/Test/app/libs/"
        };
        com.android.dx.command.dexer.Main.Arguments arguments = new com.android.dx.command.dexer.Main.Arguments();
        arguments.parse(args);
        try
        {
            int code = com.android.dx.command.dexer.Main.run(arguments);
            if (code!=0)
                return false;
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    //??????sdklib????????????????????????apk
    public boolean sdklib()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            ApkBuilder builder = new ApkBuilder(
                //????????????apk????????????
                new File("/storage/emulated/0/AppProjects/Test/app/build/bin/app.ap_"),
                //aapt?????????????????????
                new File("/storage/emulated/0/AppProjects/Test/app/build/bin/resources.ap_"),
                //dx???????????????
                new File("/storage/emulated/0/AppProjects/Test/app/build/bin/classes.dex"),
                null,
                new PrintStream(baos)
            );
            builder.sealApk();
            //??????????????????????????????
            String s = baos.toString();
        }
        catch (ApkCreationException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (SealedApkException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //??????zipsigner??????apk
    public boolean zipSigner()
    {
        try
        {
            ZipSigner zipSigner = new ZipSigner();
            zipSigner.setKeymode("testkey");
            zipSigner.signZip(
                //????????????apk??????
                "/storage/emulated/0/AppProjects/Test/app/build/bin/app.ap_",
                //????????????apk??????
                "/storage/emulated/0/AppProjects/Test/app/build/bin/app.apk"
            );
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (GeneralSecurityException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
