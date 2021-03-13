package edu.neu.promotion;

import android.content.Context;

import com.davidsoft.utils.JsonNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

public final class CacheManager {


    private static File buildCacheFile(Context context, String name) {
        return new File(context.getCacheDir().getAbsoluteFile() + File.separator + name);
    }

    public static void setJsonCache(Context context, String name, JsonNode content) {
        File cacheFile = buildCacheFile(context, name);
        FileOutputStream out;
        try {
            out = new FileOutputStream(cacheFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        try {
            out.write(content.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JsonNode getJsonCache(Context context, String name) {
        File cacheFile = buildCacheFile(context, name);
        int fileLength = (int) cacheFile.length();
        if (fileLength == 0) {
            cacheFile.delete();
            return null;
        }
        FileInputStream in;
        try {
            in = new FileInputStream(cacheFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            cacheFile.delete();
            return null;
        }
        byte[] fileData = new byte[fileLength];
        try {
            in.read(fileData);
        } catch (IOException e) {
            e.printStackTrace();
            fileData = null;
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fileData == null) {
            cacheFile.delete();
            return null;
        }
        cacheFile.setLastModified(System.currentTimeMillis());
        try {
            return JsonNode.parseJson(new String(fileData, StandardCharsets.UTF_8));
        } catch (ParseException e) {
            e.printStackTrace();
            cacheFile.delete();
            return null;
        }
    }

    public static void setValueCache(Context context, String name, String content) {
        File cacheFile = buildCacheFile(context, name);
        FileOutputStream out;
        try {
            out = new FileOutputStream(cacheFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        try {
            out.write(content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getValueCache(Context context, String name) {
        File cacheFile = buildCacheFile(context, name);
        int fileLength = (int) cacheFile.length();
        if (fileLength == 0) {
            cacheFile.delete();
            return null;
        }
        FileInputStream in;
        try {
            in = new FileInputStream(cacheFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            cacheFile.delete();
            return null;
        }
        byte[] fileData = new byte[fileLength];
        try {
            in.read(fileData);
        } catch (IOException e) {
            e.printStackTrace();
            fileData = null;
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fileData == null) {
            cacheFile.delete();
            return null;
        }
        cacheFile.setLastModified(System.currentTimeMillis());
        return new String(fileData, StandardCharsets.UTF_8);
    }
}