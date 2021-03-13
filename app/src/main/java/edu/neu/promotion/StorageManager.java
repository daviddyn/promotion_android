package edu.neu.promotion;

import android.content.Context;

import com.davidsoft.utils.JsonNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

public final class StorageManager {

    public static final String TOKEN = "token";
    public static final String TOKEN_TYPE = "token_type";
    public static final String USER_INFO = "userInfo";
    public static final String LAST_USE_ADMIN_ROLE_GROUP_ID = "lastUseAdminRoleGroupId";

    private static File buildFile(Context context, String name) {
        return new File(context.getFilesDir().getAbsoluteFile() + File.separator + name);
    }

    public static void setJson(Context context, String name, JsonNode content) {
        File cacheFile = buildFile(context, name);
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

    public static JsonNode getJson(Context context, String name) {
        File cacheFile = buildFile(context, name);
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

    public static void setValue(Context context, String name, String content) {
        File cacheFile = buildFile(context, name);
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

    public static String getValue(Context context, String name) {
        File cacheFile = buildFile(context, name);
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

    public static void clear(Context context, String name) {
        buildFile(context, name).delete();
    }
}
