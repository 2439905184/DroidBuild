package com.application.developer;

import java.io.File;
import java.nio.channels.FileChannel;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import com.android.sdklib.build.ApkBuilder;
import java.io.PrintStream;
import com.android.sdklib.build.ApkCreationException;
import com.android.sdklib.build.SealedApkException;
import com.application.developer.util.CompileInformationManager;
import com.application.developer.util.LogUtil;

import kellinwood.security.zipsigner.ZipSigner;
import java.security.GeneralSecurityException;
import org.eclipse.jdt.internal.compiler.batch.Main;
import android.util.Log;

public class CompileUtils
{
    
    private static final String REGEX_IMPORT = "import\\s*<(.*?)>";
    private static final String REGEX_PACKAGE = "package\\s*<(.*?)>";
    
    public static void copyFile(File source, File dest) throws IOException
    {
        FileChannel inputChannel = new FileInputStream(source).getChannel();
        if (dest!=null){
            if (!dest.exists()){
                dest.getParentFile().mkdirs();
                dest.createNewFile();
            }
        }
        FileChannel outputChannel = new FileOutputStream(dest).getChannel();
        outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        inputChannel.close();
        outputChannel.close();
	}
    
    public static List<String> getFiles(String path)
    {
        return getFiles("", path);
    }

    public static List<String> getFilesJava(String path)
    {
        return getFiles("java", path);
    }

    public static List<String> getFilesSoft(String path)
    {
        return getFiles("soft", path);
    }

    private static List<String> getFiles(String extension, String path)
    {
        List<String> list = new ArrayList<String>();
        for (String str : getFilesAllName(path))
        {
            if (!extension.isEmpty())
            {
                String temp = str.substring(str.lastIndexOf(".") + 1, str.length());
                if (extension.equals(temp))
                {
                    list.add(str);
                }
            }
            else
            {
                list.add(str);
            }
        }
        return list;
    }
    
    public static List<String> getFilesAllName(String path)
    {
        File file = new File(path);
        File[] files = file.listFiles();
        if (files == null)
        {
            return null;
        }
        List<String> list = new ArrayList<>();
        for (int i = 0; i < files.length; i++)
        {
            list.add(files[i].getAbsolutePath());
        }
        return list;
    }

    public static String readFile(String filepath)
    {
        StringBuffer string = new StringBuffer();
        BufferedReader br = null;
        try
        {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(filepath));
            br = new BufferedReader(isr);    
            String line = null;       
            while ((line = br.readLine()) != null)
            {
                string.append(line);
                string.append("\n");
            }   
            br.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return string.toString();
    }

    public static void writeFile(String str, String savePath) throws Exception
    {
        BufferedWriter bw = new BufferedWriter(new FileWriter(savePath));
        bw.write(str);
        bw.close();
	}
    
    public static String getOnCreate(String content)
    {
        String startStr = "void onCreate()";
        String endStr = "end onCreate();";
        int start = content.indexOf(startStr) + startStr.length();
        int end = content.lastIndexOf(endStr);
        return (end != -1) ? content.substring(start, end) : "";
        //return content.substring(content.indexOf(startStr) + startStr.length(), content.lastIndexOf(endStr));
    }

    public static String getOther(String content)
    {
        String startStr = "void onTher()";
        String endStr = "end onTher();";
        int start = content.indexOf(startStr) + startStr.length();
        int end = content.lastIndexOf(endStr);
        return (end != -1) ? content.substring(start, end) : "";
        //return content.substring(content.indexOf(startStr) + startStr.length(), content.lastIndexOf(endStr));
    }

    public static String getClass(String content)
    {
        String startStr = "class <";
        String endStr = ">";
        int start = content.indexOf(startStr) + startStr.length();
        int end = content.lastIndexOf(endStr);
        return (end != -1) ? content.substring(start, end) : "";
        //return content.substring(content.indexOf(startStr) + startStr.length(), content.lastIndexOf(endStr));
	}
    
    public static String getImport(String content)
    {
        StringBuilder str = new StringBuilder();
        if (content.isEmpty())
        {
            return str.toString();
        }
        Matcher matcher = Pattern.compile(REGEX_IMPORT).matcher(content);
        int matcher_start = 0;
        while (matcher.find(matcher_start))
        {
            str.append("import ");
            str.append(matcher.group(1));
            str.append(";");
            str.append("\n");
            matcher_start = matcher.end();
        }
        return str.toString();
    }

    public static String getPackage(String content)
    {
        StringBuilder str = new StringBuilder();
        if (content.isEmpty())
        {
            return str.toString();
        }
        Matcher matcher = Pattern.compile(REGEX_PACKAGE).matcher(content);
        if (matcher.find())
        {
            str.append("package ");
            str.append(matcher.group(1));
            str.append(";");
        }
        return str.toString();
    }
    
    //??????aapt????????????
    public static boolean aapt(String res,String gen,String assets,String androidmanifest,String android_jar,String ap_)
    {
        String[] args = 
        {
            //aapt????????????
            "data/data/com.application.developer/files/aapt",
            //??????aapt????????????
            "package","-v","-f","-m",
            //res???????????????
            "-S",res,
            //gen???????????????
            "-J",gen,
            //assets???????????????????????????????????????????????????
            "-A",assets,
            //AndroidManifest.xml????????????
            "-M",androidmanifest,
            //android.jar????????????
            "-I",android_jar,
            //??????.ap_????????????
            "-F",ap_
        };
        try
        {
            Process process = Runtime.getRuntime().exec(args);
            int code = process.waitFor();
            /*
             //??????????????????????????????
             InputStream input=process.getInputStream(); 
             //??????????????????
             InputStream input=process.getErrorStream(); 
             //??????????????????
             */
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
    public static boolean ecj(String libs,String android_jar,String java,String r_java,String classs,String mainactivity)
    {
        //????????????
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        //????????????
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        Main main = new Main(new PrintWriter(baos1),new PrintWriter(baos2),false,null,null);
        //???????????????????????????????????????
        //Main main=new Main(new PrintWriter(System.out),new PrintWriter(System.err),false,null,null);
        String[] args =
        {
            "-verbose",
            //?????????jar??????????????????
            "-extdirs",libs,
            //android.jar????????????
            "-bootclasspath",android_jar,
            //java??????????????????
            "-classpath",java+":"+
            //r.java??????????????????
            r_java+":"+
            //?????????jar????????????????????????????????????????????????jar????????????????????????????????????????????????
            libs,
            "-1.6",
            "-target","1.6",
            "-proc:none",
            //class??????????????????
            "-d",classs,
            //?????????????????????java??????
            mainactivity
        };
        String cmdStr = "";
        for (String item:args){
            cmdStr+=" "+item;
        }
        //???????????????????????????
        boolean b = main.compile(args);
        //??????????????????????????????
        //???????????????????????????
        String s1 = baos1.toString();
        //???????????????????????????
        String s2 = baos2.toString();
        LogUtil.e("CompileUtils---error--s2"+s2);
        //??????????????????????????????????????????
        CompileInformationManager.getInstance().setCompileErrorMsg(s2);
        LogUtil.e("CompileUtils---ecj:: cmdStr:"+cmdStr+" s1:"+s1+" s2:"+s2);
        return b;
    }

    //??????dx??????dex??????
    public static boolean dex(String dex,String classs,String libs)
    {
        String[] args =
        {
            "--verbose",
            //?????????
            "--num-threads="+Runtime.getRuntime().availableProcessors(),
            //classes.dex??????????????????
            "--output="+dex,
            //class??????????????????
            classs,
            //????????????????????????jar?????????????????????
            libs
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
    public static boolean sdklib(String ap_,String resources,String dex)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            ApkBuilder builder = new ApkBuilder(
                //????????????apk????????????
                new File(ap_),
                //aapt?????????????????????
                new File(resources),
                //dx???????????????
                new File(dex),
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
    public static boolean zipSigner(String key,String ap_,String apk)
    {
        try
        {
            ZipSigner zipSigner = new ZipSigner();
            zipSigner.setKeymode(key);
            zipSigner.signZip(
                //????????????apk??????
                ap_,
                //????????????apk??????
                apk
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
