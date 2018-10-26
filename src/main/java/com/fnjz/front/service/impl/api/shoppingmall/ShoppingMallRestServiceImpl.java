package com.fnjz.front.service.impl.api.shoppingmall;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.front.dao.ShoppingMallRestDao;
import com.fnjz.front.dao.UserIntegralRestDao;
import com.fnjz.front.entity.api.goods.GoodsInfoRestDTO;
import com.fnjz.front.entity.api.goods.GoodsRestDTO;
import com.fnjz.front.entity.api.goods.GoodsRestEntity;
import com.fnjz.front.entity.api.shoppingmallintegralexchange.ShoppingMallIntegralExchangeRestDTO;
import com.fnjz.front.entity.api.shoppingmallintegralexchange.ShoppingMallIntegralExchangeRestEntity;
import com.fnjz.front.enums.CategoryOfBehaviorEnum;
import com.fnjz.front.enums.ShoppingMallExchangeEnum;
import com.fnjz.front.service.api.shoppingmall.ShoppingMallRestService;
import com.fnjz.utils.sms.TemplateCode;
import com.fnjz.utils.sms.chuanglan.sms.util.ChuangLanSmsUtil;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.util.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by yhang on 2018/10/20.
 */
@Service("shoppingMallRestService")
@Transactional
public class ShoppingMallRestServiceImpl implements ShoppingMallRestService {

    @Autowired
    private ShoppingMallRestDao shoppingMallRestDao;

    @Autowired
    private UserIntegralRestDao userIntegralRestDao;

    /**
     * 获取可用商品
     *
     * @return
     */
    @Override
    public List<GoodsRestDTO> getGoods() {
        return shoppingMallRestDao.getGoods();
    }

    /**
     * 根据id获取商品详情
     *
     * @param id
     * @return
     */
    @Override
    public GoodsInfoRestDTO getGoodsInfoById(Integer id) {
        return shoppingMallRestDao.getGoodsInfoById(id);
    }

    @Override
    public GoodsRestEntity getGoodsById(Integer id) {
        return shoppingMallRestDao.getGoodsById(id);
    }

    //福禄积分兑换 url
    @Value("${soouu_url}")
    private String URL;
    //private static String URL = "http://test.ccapi.soouu.cn/Interface/Method";
    //云接口编号
    @Value("${soouu_customerid}")
    private String customerid;
    //private static String customerid = "803683";
    //树鱼秘钥
    @Value("${soouu_secret}")
    private String secret;
    //private static String secret = "CC11F561EBF14204089A5C64DE61C8DF";

    //回调接口
    @Value("${soouu_callback}")
    private String callback;

    /**
     * 商品兑换
     *
     * @return
     */
    @Override
    public JSONObject toExchange(String exchangeMobile, GoodsRestEntity goodsRestEntity, String userInfoId) throws Exception {
        JSONObject result2 = new JSONObject();
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        Map<String, String> jsonObject = new HashMap<String, String>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //树鱼云接口编号
        jsonObject.put("customerid", customerid);
        //公共参数
        jsonObject.put("format", "json");
        //时间戳
        jsonObject.put("timestamp", LocalDateTime.now().format(formatter));
        jsonObject.put("v", "1.0");

        //合作商家订单号(唯一不重复,任何系统的唯一性)
        String shoppingMallId = userInfoId + System.currentTimeMillis();
        jsonObject.put("customerorderno", shoppingMallId);
        //话费兑换类型  自有参数
        if (ShoppingMallExchangeEnum.TELEPHONE_CHARGE.getIndex() == goodsRestEntity.getType()) {
            jsonObject.put("method", "kamenwang.phoneorder.add");
            //充值面值
            jsonObject.put("chargeparvalue", Double.valueOf(goodsRestEntity.getFaceValue()+"").intValue()+"");
            //充值手机号
            jsonObject.put("chargephone", exchangeMobile);
            //回调地址
            jsonObject.put("notifyurl", callback);
        } else if (ShoppingMallExchangeEnum.NETFLOW.getIndex() == goodsRestEntity.getType()) {
            //流量兑换类型
            jsonObject.put("method", "kamenwang.trafficgoods.add");
            //流量大小(注：单位为MB，1GB=1024MB)
            jsonObject.put("chargeparvalue", Double.valueOf(goodsRestEntity.getFaceValue()+"").intValue()+"");
            //充值手机号
            jsonObject.put("chargephone", exchangeMobile);
            //流量类型(注：1.全国  0.省内)
            jsonObject.put("areatype", "1");
            //回调地址
            jsonObject.put("notifyurl", callback);
        } else if (ShoppingMallExchangeEnum.VIDEO_MEMBERSHIP.getIndex() == goodsRestEntity.getType()) {
            jsonObject.put("method", "kamenwang.order.cardorder.add");
            //商品编号
            jsonObject.put("productid", goodsRestEntity.getSoouuGoodsId());
            //购买数量
            jsonObject.put("buynum", "1");
        }

        String param = MaptoString(jsonObject);
        // 将秘钥拼接到URL参数对后
        String postData = param + secret;
        // 将请求参数进行MD5加密得到签名
        String sign = MD5(postData);
        // 将签名添加到URL参数后
        param = param + "&sign=" + sign.toUpperCase();
        // 向福禄api接口地址发起http post请求，并传递url参数
        String result = HttpRequest.sendPost(URL, param);
        JSONObject jsonObject1 = JSONObject.parseObject(result);
        //{"OrderId":1141820,"ProductId":1204406,"BuyNum":1,"Status":"成功","CreateTime":"2018-10-22 18:35:00","FinishTime":"","StatusMsg":"订单创建成功","Cards":[{"CardNumber":"8ANggbVRlDfuC9nHSs5dPg==","CardPwd":"cZLO8lEoT+LtNl2Sa1ZCfA==","CardDeadline":"2019-03-21 00:00:00"}],"PurchasePrice":1.0000}
        //插入积分兑换记录
        ShoppingMallIntegralExchangeRestEntity shoppingMall = new ShoppingMallIntegralExchangeRestEntity();
        //设置订单号
        shoppingMall.setId(Long.valueOf(shoppingMallId));
        //设置id
        shoppingMall.setGoodsId(Integer.valueOf(goodsRestEntity.getId()));
        //设置兑换手机号
        shoppingMall.setExchangeMobile(exchangeMobile);
        //设置数量
        shoppingMall.setCount(1);
        if (jsonObject1.get("MessageCode") == null) {
            //兑换中

            //设置兑换状态  先定义兑换中
            shoppingMall.setStatus(1);
            //树鱼订单号
            shoppingMall.setSoouuOrderId(Integer.valueOf(jsonObject1.get("OrderId") + ""));
            //树鱼商品进价
            shoppingMall.setPurchasePrice(new BigDecimal(jsonObject1.get("PurchasePrice") + ""));

            //视频会员类型解密兑换码
            if (ShoppingMallExchangeEnum.VIDEO_MEMBERSHIP.getIndex() == goodsRestEntity.getType()) {
                //兑换成功
                if (jsonObject1.get("Cards") != null) {
                    if (jsonObject1.getJSONArray("Cards").getJSONObject(0) != null) {
                        if (jsonObject1.getJSONArray("Cards").getJSONObject(0).getString("CardPwd") != null) {
                            //DES加密的私钥，长度要是8的倍数(卡密解密方法 (福禄订单号 秘钥 生成KEY，从第4位开始，截取8位))
                            String password = MD5(jsonObject1.getString("OrderId") + secret).substring(4, 12);
                            //待解密的卡密
                            String CardPwd = jsonObject1.getJSONArray("Cards").getJSONObject(0).getString("CardPwd");
                            //解密
                            String decryCardPwd = decode(password.getBytes("UTF-8"), CardPwd);
                            System.out.print("解密后的卡密：" + decryCardPwd);
                            //设置兑换状态
                            shoppingMall.setStatus(2);
                            //兑换码
                            shoppingMall.setCardCode(decryCardPwd);
                            //有效期
                            LocalDateTime ldt = LocalDateTime.parse(jsonObject1.getJSONArray("Cards").getJSONObject(0).getString("CardDeadline"), formatter);
                            ZoneId zone = ZoneId.systemDefault();
                            Instant instant = ldt.atZone(zone).toInstant();
                            shoppingMall.setCardDeadline(Date.from(instant));
                            //添加兑换记录
                            shoppingMallRestDao.insert(shoppingMall, userInfoId);
                            //记录积分消耗表
                            userIntegralRestDao.insertShoppingMallIntegral(userInfoId, shoppingMallId, "-" + goodsRestEntity.getFengfengTicketValue(), goodsRestEntity.getGoodsName(), CategoryOfBehaviorEnum.SHOPPING_MALL_EXCHANGE.getIndex());
                            //修改总积分值
                            userIntegralRestDao.updateForTotalIntegral(userInfoId,Integer.valueOf("-" + goodsRestEntity.getFengfengTicketValue()));
                            result2.put("cardDeadline", Date.from(instant));
                            result2.put("cardCode", decryCardPwd);
                            result2.put("status", 2);
                            ChuangLanSmsUtil.sendSmsByPost(goodsRestEntity.getGoodsName(),decryCardPwd,ldt.toLocalDate().toString(),TemplateCode.SEND_EXCHANGE_GOODS.getTemplateContent(),exchangeMobile,true);
                            return result2;
                        }
                    }
                }
            } else {
                //话费和流量兑换类型
                //设置兑换状态
                shoppingMall.setStatus(1);
                result2.put("status", 1);
                shoppingMallRestDao.insert(shoppingMall, userInfoId);
                return result2;
            }
        } else {
            //下架操作
            if (jsonObject1.getInteger("MessageCode") == 2100 || jsonObject1.getInteger("MessageCode") == 2101 || jsonObject1.getInteger("MessageCode") == 2103 || jsonObject1.getInteger("MessageCode") == 2104 || jsonObject1.getInteger("MessageCode") == 2106 || jsonObject1.getInteger("MessageCode") == 2108) {
                shoppingMallRestDao.downGoods(Integer.valueOf(goodsRestEntity.getId()));
                ChuangLanSmsUtil.sendSmsByPost(jsonObject1.getString("MessageInfo"),TemplateCode.DOWN_GOODS.getTemplateContent(),"13552570975",true);
            }
            //兑换失败
            shoppingMall.setStatus(3);
            //录入错误原因
            shoppingMall.setDescription(jsonObject1.getString("MessageInfo"));
            shoppingMallRestDao.insert(shoppingMall, userInfoId);
            result2.put("status", 3);
            return result2;
        }
        return null;
    }

    /**
     * 将字典集合按键排序，并拼接为URL参数对（param1=value1&param2=value2...)
     *
     * @param params 需要转换的字典集合
     * @return String字符串 拼接完的URL参数对
     */
    private static String MaptoString(Map<String, String> params) {
        Set<String> keySet = params.keySet();
        String[] keysArr = keySet.toArray(new String[0]);
        Arrays.sort(keysArr);//对键进行排序
        StringBuilder signedContent = new StringBuilder();
        for (int i = 0; i < keysArr.length; i++) {//将字典集合转换为URL参数对
            signedContent.append(keysArr[i]).append("=").append(params.get(keysArr[i])).append("&");
        }
        String signedContentStr = signedContent.toString();
        if (signedContentStr.endsWith("&")) {
            signedContentStr = signedContentStr.substring(0, signedContentStr.length() - 1);
        }
        return signedContentStr;
    }

    /**
     * 获取MD5加密
     *
     * @param pwd 需要加密的字符串
     * @return String字符串 加密后的字符串
     */
    private static String MD5(String pwd) {
        try {
            // 创建加密对象
            MessageDigest digest = MessageDigest.getInstance("md5");

            // 调用加密对象的方法，加密的动作已经完成
            byte[] bs = digest.digest(pwd.getBytes());
            // 接下来，我们要对加密后的结果，进行优化，按照mysql的优化思路走
            // mysql的优化思路：
            // 第一步，将数据全部转换成正数：
            String hexString = "";
            for (byte b : bs) {
                // 第一步，将数据全部转换成正数：
                // 解释：为什么采用b&255
                /*
                 * b:它本来是一个byte类型的数据(1个字节) 255：是一个int类型的数据(4个字节)
                 * byte类型的数据与int类型的数据进行运算，会自动类型提升为int类型 eg: b: 1001 1100(原始数据)
                 * 运算时： b: 0000 0000 0000 0000 0000 0000 1001 1100 255: 0000
                 * 0000 0000 0000 0000 0000 1111 1111 结果：0000 0000 0000 0000
                 * 0000 0000 1001 1100 此时的temp是一个int类型的整数
                 */
                int temp = b & 255;
                // 第二步，将所有的数据转换成16进制的形式
                // 注意：转换的时候注意if正数>=0&&<16，那么如果使用Integer.toHexString()，可能会造成缺少位数
                // 因此，需要对temp进行判断
                if (temp < 16 && temp >= 0) {
                    // 手动补上一个“0”
                    hexString = hexString + "0" + Integer.toHexString(temp);
                } else {
                    hexString = hexString + Integer.toHexString(temp);
                }
            }
            return hexString;
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 解密方法
     *
     * @param DESkey DES加密的私钥
     * @param data   需要进行解密的秘闻
     * @return URL 解密后的内容
     */
    public static String decode(byte[] DESkey, String data) throws Exception {
        DESKeySpec keySpec = new DESKeySpec(DESkey);// 设置密钥参数
        byte[] DESIV = {0, 0, 0, 0, 0, 0, 0, 0};// 设置向量
        AlgorithmParameterSpec iv = new IvParameterSpec(DESIV);// 加密算法的参数接口，IvParameterSpec是它的一个实现
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");// 获得密钥工厂
        SecretKey key = keyFactory.generateSecret(keySpec);// 得到密钥对象

        Cipher deCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        deCipher.init(Cipher.DECRYPT_MODE, key, iv);
        BASE64Decoder base64Decoder = new BASE64Decoder();
        byte[] pasByte = deCipher.doFinal(base64Decoder.decodeBuffer(data));
        return new String(pasByte, "UTF-8");
    }

    /**
     * 树鱼回调接口
     *
     * @param orderNo
     * @param customerOrderNo
     * @param status
     * @param reMark
     */
    @Override
    public void updateExchange(String orderNo, String customerOrderNo, String status, String reMark) {
        if (Boolean.valueOf(status)) {
            //获取订单状态
            ShoppingMallIntegralExchangeRestEntity shopping = shoppingMallRestDao.checkStatusById(customerOrderNo);
            if (shopping.getStatus() != 2) {
                //根据goodsid--->获取
                GoodsRestEntity goodsById = shoppingMallRestDao.getGoodsById(shopping.getGoodsId());
                //成功
                shoppingMallRestDao.update(customerOrderNo, 2);
                //记录积分消耗表
                userIntegralRestDao.insertShoppingMallIntegral(shopping.getUserInfoId() + "", customerOrderNo, "-" + goodsById.getFengfengTicketValue(), goodsById.getGoodsName(), CategoryOfBehaviorEnum.SHOPPING_MALL_EXCHANGE.getIndex());
                //修改总积分值
                userIntegralRestDao.updateForTotalIntegral(shopping.getUserInfoId() + "",Integer.valueOf("-" + goodsById.getFengfengTicketValue()));
            }
        } else {
            //兑换失败
            shoppingMallRestDao.update(customerOrderNo, 3);
        }
    }

    /**
     * 积分兑换---->历史兑换列表
     *
     * @param userInfoId
     * @return
     */
    @Override
    public List<ShoppingMallIntegralExchangeRestDTO> historyIntegralExchange(String userInfoId) {
        List<ShoppingMallIntegralExchangeRestDTO> list = shoppingMallRestDao.historyIntegralExchange(userInfoId);
        if (list.size() > 0) {
            if (list.get(0).getStatus() == 1) {
                //兑换中状态----->rpc查询树鱼
                HttpHeaders headers = new HttpHeaders();
                MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
                headers.setContentType(type);
                Map<String, String> jsonObject = new HashMap<String, String>();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                //树鱼云接口编号
                jsonObject.put("customerid", customerid);
                //公共参数
                jsonObject.put("format", "json");
                //时间戳
                jsonObject.put("timestamp", LocalDateTime.now().format(formatter));
                jsonObject.put("v", "1.0");
                jsonObject.put("method", "kamenwang.order.get");
                //订单号
                jsonObject.put("customerorderno", list.get(0).getId()+"");
                String param = MaptoString(jsonObject);
                // 将秘钥拼接到URL参数对后
                String postData = param + secret;
                // 将请求参数进行MD5加密得到签名
                String sign = MD5(postData);
                // 将签名添加到URL参数后
                param = param + "&sign=" + sign.toUpperCase();
                // 向福禄api接口地址发起http post请求，并传递url参数
                String result = HttpRequest.sendPost(URL, param);
                JSONObject jsonObject1 = JSONObject.parseObject(result);
                if (jsonObject1.get("MessageCode") == null) {
                    if (StringUtils.equals(jsonObject1.getString("Status"), "成功")) {
                        //成功
                        shoppingMallRestDao.update(list.get(0).getId() + "", 2);
                        //记录积分消耗表
                        userIntegralRestDao.insertShoppingMallIntegral(userInfoId, list.get(0).getId() + "", "-" + list.get(0).getFengfengTicketValue(), list.get(0).getGoodsName(), CategoryOfBehaviorEnum.SHOPPING_MALL_EXCHANGE.getIndex());
                        //修改总积分值
                        userIntegralRestDao.updateForTotalIntegral(userInfoId,Integer.valueOf("-" + list.get(0).getFengfengTicketValue()));
                        list.get(0).setStatus(2);
                    } else if (StringUtils.equals(jsonObject1.getString("Status"), "失败")) {
                        shoppingMallRestDao.update(list.get(0).getId() + "", 3);
                        list.get(0).setStatus(3);
                    }
                }else{
                    shoppingMallRestDao.update(list.get(0).getId() + "", 3);
                    list.get(0).setStatus(3);
                }
            }
        }
        return list;
    }
}
