package com.fnjz.front.filter;

import org.apache.log4j.Logger;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yhang on 2018/6/8.
 */
public class Apifilter implements Filter {

    private static final Logger logger = Logger.getLogger(Apifilter.class);

    public void destroy() {
        // TODO Auto-generated method stub

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        logger.info("请求地址:" + req.getRequestURL() + "  涉及到的方法为：" + req.getMethod());
        if ("GET".equals(req.getMethod())) {
            logger.info("GET请求参数:" + req.getQueryString());
        }
        int length = req.getContentLength();
        if (length > 0) {
            BufferedRequestWrapper bufferedRequest = new BufferedRequestWrapper(req,length);

            InputStream is = bufferedRequest.getInputStream();
            byte[] content = new byte[length];

            int pad = 0;
            while (pad < length) {
                pad += is.read(content, pad, length);
            }
            logger.info("请求参数:" + new String(content, "utf-8"));
            request = bufferedRequest;
        }

        chain.doFilter(request, response);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub
        logger.info("创建api自定义filter");
    }

}
