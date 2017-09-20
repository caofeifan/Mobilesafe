package com.cff.mobilesafe.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 80755 on 2017/2/22.
 */

public class StreamUtil {
    public static String readFromStream(InputStream in) throws IOException {
        ByteArrayOutputStream out  = new ByteArrayOutputStream();
        int len = 0;
        byte[] buffer = new byte[1024];
        while ((len = in.read(buffer)) != -1){
            out.write(buffer,0,len);
        }
        String result = out.toString();
        if (in != null){
            in.close();
        }
        if (out != null){
            out.close();
        }
        return result;
    }
}
