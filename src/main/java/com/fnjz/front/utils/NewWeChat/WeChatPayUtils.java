package com.fnjz.front.utils.NewWeChat;

import com.alibaba.fastjson.JSON;
import com.fnjz.front.RestTemplate.AppConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.log4j.Logger;
import org.jeecgframework.core.util.MD5Util;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.PostConstruct;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.*;

/**
 * 微信支付工具类
 * Created by yhang on 2018/12/7.
 */
@Component
public class WeChatPayUtils {

    private static final Logger logger = Logger.getLogger(WeChatPayUtils.class);

    private String AppId;
    private String mchid;
    private String key;
    //公众号appid
    private String officialAppId;
    //小程序appid
    private String wxappletAppId;
    private HttpComponentsClientHttpRequestFactory clientHttpRequestFactory;

    @PostConstruct
    public void init() {
        Properties p = new Properties();
        InputStream in = WXAppletUtils.class.getResourceAsStream("/fnjz/wechat.properties");
        // 获得密钥库文件流
        InputStream p12 = WXAppletUtils.class.getResourceAsStream("/fnjz/wechatpay/apiclient_cert.p12");
        try {
            p.load(in);
            AppId = p.getProperty("appId", "");
            mchid = p.getProperty("mchid", "");
            key = p.getProperty("key", "");
            officialAppId = p.getProperty("officialAppId", "");
            wxappletAppId = p.getProperty("wxappletAppId", "");
            // 实例化密钥库
            KeyStore ks = KeyStore.getInstance("PKCS12");

            // 加载密钥库
            ks.load(p12, mchid.toCharArray());
            // Trust own CA and all self-signed certs
            SSLContext sslcontext = SSLContextBuilder.create()
                    .loadKeyMaterial(ks, mchid.toCharArray())
                    .build();
            // Allow TLSv1 protocol only
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,  new String[]{"TLSv1"},
                    null,hostnameVerifier);

            CloseableHttpClient httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .build();
            clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpclient);
            // 关闭密钥库文件流
            p12.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
        }
    }

    @Autowired
    private AppConfig restTemplate;

    /**
     * https://blog.csdn.net/dong_18383219470/article/details/53636943
     * 方法用途: 对所有传入参数按照字段名的Unicode码从小到大排序（字典序），并且生成url参数串<br>  生成md5签名
     *
     * @param paraMap    要排序的Map对象
     * @param urlEncode  是否需要URLENCODE
     * @param keyToLower 是否需要将Key转换为全小写 true:key转化成小写，false:不转化
     * @return
     */
    public String getSign(Map<String, String> paraMap, boolean urlEncode, boolean keyToLower) {
        String buff = "";
        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<>(paraMap.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, Comparator.comparing(Map.Entry<String, String>::getKey));
            // 构造URL 键值对的格式
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, String> item : infoIds) {
                if (StringUtils.isNotBlank(item.getKey())) {
                    String key = item.getKey();
                    String val = item.getValue();
                    if (urlEncode) {
                        val = URLEncoder.encode(val, "utf-8");
                    }
                    if (keyToLower) {
                        buf.append(key.toLowerCase() + "=" + val);
                    } else {
                        buf.append(key + "=" + val);
                    }
                    buf.append("&");
                }
            }
            buff = buf.toString();
            if (buff.isEmpty() == false) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            return null;
        }
        //注：key为商户平台设置的密钥key
        String stringSignTemp = buff + "&key=" + key;
        System.out.println(stringSignTemp);
        //md5加密
        String sign = MD5Util.MD5Encode(stringSignTemp, null).toUpperCase();
        return sign;
    }

    /**
     * https://blog.csdn.net/xiaozhegaa/article/details/79127283
     * 将Map转换为XML格式的字符串
     *
     * @param data Map类型数据
     * @return XML格式的字符串
     * @throws Exception
     */
    public String mapToXml(Map<String, String> data) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        org.w3c.dom.Document document = documentBuilder.newDocument();
        org.w3c.dom.Element root = document.createElement("xml");
        document.appendChild(root);
        for (String key : data.keySet()) {
            String value = data.get(key);
            if (value == null) {
                value = "";
            }
            value = value.trim();
            org.w3c.dom.Element filed = document.createElement(key);
            filed.appendChild(document.createTextNode(value));
            root.appendChild(filed);
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        DOMSource source = new DOMSource(document);
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        String output = writer.getBuffer().toString(); //.replaceAll("\n|\r", "");
        try {
            writer.close();
        } catch (Exception ex) {
        }
        return output;
    }

    //通用的。返回map格式

    /**
     * XML格式字符串转换为Map
     *
     * @param strXML XML字符串
     * @return XML数据转换后的Map
     * @throws Exception
     */
    public Map<String, String> xmlToMap(String strXML) {
        try {
            Map<String, String> data = new HashMap<String, String>();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
            org.w3c.dom.Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
                Node node = nodeList.item(idx);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    data.put(element.getNodeName(), element.getTextContent());
                }
            }
            stream.close();
            return data;
        } catch (Exception ex) {
            logger.error(ex.toString());
        }
        return null;
    }

    /**
     * 移动端 提现到微信零钱
     * @param money 金额 单位 元
     * @param openId 用户
     * @param orderId 订单号
     * @param desc 企业描述
     * @param flag 定义: 1 小程序--->公众号发送现金红包(暂不可用)   2:移动端---->提现到零钱
     * @param cash 定义: 提现到零钱功能---->1 小程序提现   2 移动端提现
     * @return
     */
    public Map<String, String> wechatPay(double money, String openId, String orderId, String desc,int flag,int cash){
        if(flag==1){
            Map<String, String> map = new HashMap<>();
            //随机生成
            map.put("nonce_str", UUID.randomUUID().toString().replace("-", "").toLowerCase());
            //订单号
            map.put("mch_billno", orderId);
            //商户号
            map.put("mch_id", mchid);
            //公众号 appid
            map.put("wxappid", officialAppId);
            //商户名称
            map.put("send_name", "蜂鸟记账");
            //用户openid
            map.put("re_openid", openId);
            //付款金额
            map.put("total_amount", (((int)money*100))+"");
            //红包发放总人数
            map.put("total_num", "1");
            //红包祝福语
            map.put("wishing", "点击领取你的兑换红包");
            //Ip地址
            map.put("client_ip", "192.168.0.1");
            //活动名称
            map.put("act_name", "蜂鸟记账丰丰票商城兑换");
            //备注
            map.put("remark", "丰丰票越多，兑换越多哦~");
            //场景id
            map.put("scene_id", "PRODUCT_3");
            String sign = getSign(map, false, false);
            map.put("sign", sign);
            try {
                String data = mapToXml(map);
                String url = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";
                restTemplate.customRestTemplate().setRequestFactory(clientHttpRequestFactory);
                Map<String, String> stringStringMap = xmlToMap(restTemplate.customRestTemplate().postForObject(url, data.getBytes(), String.class));
                logger.info("--------微信支付结果---------" + JSON.toJSON(stringStringMap).toString());
                return stringStringMap;
            } catch (Exception e) {
                logger.error(e.toString());
            }
        }else if(flag==2){
            Map<String, String> map = new HashMap<>();
            if(cash==1){
                map.put("mch_appid", wxappletAppId);
            }else if(cash==2){
                map.put("mch_appid", AppId);
            }
            map.put("mchid", mchid);
            //随机生成
            map.put("nonce_str", UUID.randomUUID().toString().replace("-", "").toLowerCase());
            //设置订单号
            map.put("partner_trade_no", orderId);
            //用户openid
            map.put("openid", openId);
            //校验用户姓名选项
            map.put("check_name", "NO_CHECK");
            //金额 企业付款金额，单位为分
            map.put("amount", (((int)money*100))+"");
            //企业付款备注
            map.put("desc", desc);
            //spbill_create_ip
            map.put("spbill_create_ip", "192.168.0.1");
            String sign = getSign(map, false, false);
            map.put("sign", sign);
            try {
                String data = mapToXml(map);
                String url = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
                restTemplate.customRestTemplate().setRequestFactory(clientHttpRequestFactory);
                //ByteArrayHttpMessageConverter mappingJackson2HttpMessageConverter = new ByteArrayHttpMessageConverter ();
               //restTemplate.customRestTemplate().getMessageConverters().add(mappingJackson2HttpMessageConverter);
                //HttpHeaders requestHeaders = new HttpHeaders();
                //MediaType type = MediaType.parseMediaType("application/xml; charset=UTF-8");
               // requestHeaders.setContentType(type);
                //requestHeaders.add("Content-Type", "application/xml; charset=UTF-8");
                //HttpEntity<String> requestEntity = new HttpEntity(new StringEntity(data,"application/xml","utf-8"),requestHeaders);
                Map<String, String> stringStringMap = xmlToMap(restTemplate.customRestTemplate().postForObject(url, data.getBytes(), String.class));
                logger.info("--------微信支付结果---------" + JSON.toJSON(stringStringMap).toString());
                return stringStringMap;
            } catch (Exception e) {
                logger.error(e.toString());
            }
        }
        return null;
    }

    //--------微信支付结果---------{"nonce_str":"2642d30b8caa46c8973fdec891b820c4","mchid":"1517826521","partner_trade_no":"1544171180638","payment_time":"2018-12-07 16:26:24","mch_appid":"wx3d767517f3b17ba5","payment_no":"1517826521201812072354807015","return_msg":"","result_code":"SUCCESS","return_code":"SUCCESS"}
    //陈樱  oyKyO1kZq-cdGLMmLOSOOErVJB88
    //黄学兵  oyKyO1n6sPJ1KxannqoD7lRhRwsM
    @Test
    public void run() {
        //提现到零钱
        //wechatPay(10,"111","132456","测试",2);
        //公众号发送红包
        wechatPay(1,"oXDiP4s0_BKsZ7qHkvfV4bLsYq00",System.currentTimeMillis()+"","测试",2,1);
    }
}