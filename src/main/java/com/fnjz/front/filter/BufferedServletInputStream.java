package com.fnjz.front.filter;

import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;

/**
 * Created by yhang on 2018/6/8.
 */
public class BufferedServletInputStream extends ServletInputStream {

    ByteArrayInputStream bais;

    public BufferedServletInputStream(ByteArrayInputStream bais) {
        this.bais = bais;
    }

    public int available() {
        return bais.available();
    }

    public int read() {
        return bais.read();
    }

    public int read(byte[] buf, int off, int len) {
        return bais.read(buf, off, len);
    }

    public boolean isFinished() {
        return false;
    }

    public boolean isReady() {
        return false;
    }

}
