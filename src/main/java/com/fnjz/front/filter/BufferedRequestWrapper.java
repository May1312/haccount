package com.fnjz.front.filter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yhang on 2018/6/8.
 */
public class BufferedRequestWrapper extends HttpServletRequestWrapper {

    ByteArrayInputStream bais;

    BufferedServletInputStream bsis;

    byte[]                     buffer;

    public BufferedRequestWrapper(HttpServletRequest req, int length) throws IOException {
        super(req);
        InputStream is = req.getInputStream();
        buffer = new byte[length];

        int pad = 0;
        while (pad < length) {
            pad += is.read(buffer, pad, length);
        }
    }

    public ServletInputStream getInputStream() {
        try {
            bais = new ByteArrayInputStream(buffer);
            bsis = new BufferedServletInputStream(bais);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        }
        return bsis;
    }
}
