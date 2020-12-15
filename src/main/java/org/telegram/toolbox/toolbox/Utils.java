package org.telegram.toolbox.toolbox;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class Utils {
    private final static String TELEGRAM_FILE_URL = "https://api.telegram.org/file/bot%s/%s";

    public static String loadFile(final String token, final String path) throws IOException {
        URL url = new URL(String.format(TELEGRAM_FILE_URL, token, path));

        ByteArrayBuffer baf = new ByteArrayBuffer(32);
        InputStream in = (InputStream) url.getContent();
        BufferedInputStream bis = new BufferedInputStream(in);

        int buffer;
        while((buffer = bis.read()) != -1){
            baf.append((byte)buffer);
        }

        bis.close();
        in.close();
        return new String(baf.toByteArray());
    }
}
