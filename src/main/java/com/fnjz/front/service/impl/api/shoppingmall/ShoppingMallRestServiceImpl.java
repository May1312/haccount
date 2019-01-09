package com.fnjz.front.service.impl.api.shoppingmall;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.front.dao.ShoppingMallRestDao;
import com.fnjz.front.dao.UserInfoAddFieldRestDao;
import com.fnjz.front.dao.UserIntegralRestDao;
import com.fnjz.front.dao.UserInviteRestDao;
import com.fnjz.front.entity.api.goods.GoodsInfoRestDTO;
import com.fnjz.front.entity.api.goods.GoodsListRestDTO;
import com.fnjz.front.entity.api.goods.GoodsRestEntity;
import com.fnjz.front.entity.api.shoppingmallintegralexchange.ReportShopRestDTO;
import com.fnjz.front.entity.api.shoppingmallintegralexchange.ShoppingMallIntegralExchangePhysicalRestDTO;
import com.fnjz.front.entity.api.shoppingmallintegralexchange.ShoppingMallIntegralExchangePhysicalRestEntity;
import com.fnjz.front.entity.api.shoppingmallintegralexchange.ShoppingMallIntegralExchangeRestEntity;
import com.fnjz.front.enums.AcquisitionModeEnum;
import com.fnjz.front.enums.CategoryOfBehaviorEnum;
import com.fnjz.front.enums.ShoppingMallExchangeEnum;
import com.fnjz.front.service.api.shoppingmall.ShoppingMallRestService;
import com.fnjz.front.utils.newWeChat.WeChatPayUtils;
import com.fnjz.utils.sms.TemplateCode;
import com.fnjz.utils.sms.chuanglan.sms.util.ChuangLanSmsUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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

    private static final Logger logger = Logger.getLogger(ShoppingMallRestServiceImpl.class);

    @Autowired
    private ShoppingMallRestDao shoppingMallRestDao;

    @Autowired
    private UserIntegralRestDao userIntegralRestDao;

    @Autowired
    private UserInfoAddFieldRestDao userInfoAddFieldRestDao;

    @Autowired
    private WeChatPayUtils weChatPayUtils;

    @Autowired
    private UserInviteRestDao userInviteRestDao;

    /**
     * 获取可用商品
     *
     * @return
     */
    @Override
    public List<GoodsListRestDTO> getGoods() {
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
    public JSONObject toExchange(Map<String, String> map, GoodsRestEntity goodsRestEntity, String userInfoId) throws Exception {
        JSONObject result2 = new JSONObject();
        //加入实物兑换
        if (goodsRestEntity.getGoodsType() == 2) {
            exchangePhysical(map, goodsRestEntity, userInfoId);
            result2.put("status", 2);
            return result2;
        } else if (goodsRestEntity.getGoodsType() == 3) {
            //现金红包类兑换
            ResultBean rb = exchangeCash(map, goodsRestEntity, userInfoId);
            result2.put("result", rb);
            return result2;
        }
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
            jsonObject.put("chargeparvalue", Double.valueOf(goodsRestEntity.getFaceValue() + "").intValue() + "");
            //充值手机号
            jsonObject.put("chargephone", map.get("exchangeMobile"));
            //回调地址
            jsonObject.put("notifyurl", callback);
        } else if (ShoppingMallExchangeEnum.NETFLOW.getIndex() == goodsRestEntity.getType()) {
            //流量兑换类型
            jsonObject.put("method", "kamenwang.trafficgoods.add");
            //流量大小(注：单位为MB，1GB=1024MB)
            jsonObject.put("chargeparvalue", Double.valueOf(goodsRestEntity.getFaceValue() + "").intValue() + "");
            //充值手机号
            jsonObject.put("chargephone", map.get("exchangeMobile"));
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
        shoppingMall.setExchangeMobile(map.get("exchangeMobile"));
        //设置数量
        shoppingMall.setCount(1);
        if (jsonObject1.get("MessageCode") == null) {
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
                            userIntegralRestDao.insertShoppingMallIntegral(userInfoId, shoppingMallId, "-" + goodsRestEntity.getFengfengTicketValue(), goodsRestEntity.getGoodsName(), CategoryOfBehaviorEnum.SHOPPING_MALL_EXCHANGE.getIndex(), Double.parseDouble("-" + goodsRestEntity.getFengfengTicketValue() + ""));
                            //修改总积分值
                            userIntegralRestDao.updateForTotalIntegral(userInfoId, Integer.valueOf("-" + goodsRestEntity.getFengfengTicketValue()), new BigDecimal("-" + goodsRestEntity.getFengfengTicketValue()));
                            result2.put("cardDeadline", Date.from(instant));
                            result2.put("cardCode", decryCardPwd);
                            result2.put("status", 2);
                            ChuangLanSmsUtil.sendSmsByPost(goodsRestEntity.getGoodsName(), decryCardPwd, ldt.toLocalDate().toString(), TemplateCode.SEND_EXCHANGE_GOODS.getTemplateContent(), map.get("exchangeMobile"), true);
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
                //兑换中---->先扣除积分
                //记录积分消耗表
                userIntegralRestDao.insertShoppingMallIntegral(userInfoId, shoppingMallId, "-" + goodsRestEntity.getFengfengTicketValue(), goodsRestEntity.getGoodsName(), CategoryOfBehaviorEnum.SHOPPING_MALL_EXCHANGE.getIndex(), Double.parseDouble("-" + goodsRestEntity.getFengfengTicketValue() + ""));
                //修改总积分值
                userIntegralRestDao.updateForTotalIntegral(userInfoId, Integer.valueOf("-" + goodsRestEntity.getFengfengTicketValue()), new BigDecimal("-" + goodsRestEntity.getFengfengTicketValue()));
                return result2;
            }
        } else {
            //下架操作
            if (jsonObject1.getInteger("MessageCode") == 2100 || jsonObject1.getInteger("MessageCode") == 2101 || jsonObject1.getInteger("MessageCode") == 2103 || jsonObject1.getInteger("MessageCode") == 2104 || jsonObject1.getInteger("MessageCode") == 2106 || jsonObject1.getInteger("MessageCode") == 2108) {
                shoppingMallRestDao.downGoods(Integer.valueOf(goodsRestEntity.getId()));
                ChuangLanSmsUtil.sendSmsByPost(jsonObject1.getString("MessageInfo"), TemplateCode.DOWN_GOODS.getTemplateContent(), "13552570975", true);
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
     * 实物兑换流程
     */
    private void exchangePhysical(Map<String, String> map, GoodsRestEntity goodsRestEntity, String userInfoId) {
        //订单号
        String shoppingMallId = userInfoId + System.currentTimeMillis();
        //插入积分兑换记录
        ShoppingMallIntegralExchangePhysicalRestEntity shoppingMall = JSON.parseObject(map.get("consigneeAddress"), ShoppingMallIntegralExchangePhysicalRestEntity.class);
        //设置订单号
        shoppingMall.setId(Long.valueOf(shoppingMallId));
        //设置id
        shoppingMall.setGoodsId(Integer.valueOf(goodsRestEntity.getId()));
        //设置数量
        shoppingMall.setCount(1);
        //设置兑换状态
        shoppingMall.setStatus(2);
        //添加兑换记录
        shoppingMallRestDao.insertPhysical(shoppingMall, userInfoId);
        //记录积分消耗表
        userIntegralRestDao.insertShoppingMallIntegral(userInfoId, shoppingMallId, "-" + goodsRestEntity.getFengfengTicketValue(), goodsRestEntity.getGoodsName(), CategoryOfBehaviorEnum.SHOPPING_MALL_EXCHANGE.getIndex(), Double.parseDouble("-" + goodsRestEntity.getFengfengTicketValue() + ""));
        //修改总积分值
        userIntegralRestDao.updateForTotalIntegral(userInfoId, Integer.valueOf("-" + goodsRestEntity.getFengfengTicketValue()), new BigDecimal("-" + goodsRestEntity.getFengfengTicketValue()));
    }

    /**
     * 现金红包兑换流程
     */
    private ResultBean exchangeCash(Map<String, String> map, GoodsRestEntity goodsRestEntity, String userInfoId) {
        ResultBean rb = new ResultBean();
        //订单号
        String shoppingMallId = userInfoId + System.currentTimeMillis();
        boolean flag = false;
        //小程序公众号兑换
        String wechatOpenId = null;
        if (StringUtils.equals(map.get("channel"), "1")) {
            wechatOpenId = userInfoAddFieldRestDao.getByUserInfoId(userInfoId);

        } else if (StringUtils.equals(map.get("channel"), "2")) {
            wechatOpenId = userInfoAddFieldRestDao.getWechatOpenId(userInfoId);
        }
        Map<String, String> stringStringMap = null;
        if (StringUtils.isNotEmpty(wechatOpenId)) {
            double money = goodsRestEntity.getFaceValue().doubleValue();
            stringStringMap = weChatPayUtils.wechatPay(money, wechatOpenId, shoppingMallId, "现金红包兑换" + money + "元", 2, Integer.valueOf(map.get("channel")));
            if (StringUtils.equals(stringStringMap.get("return_code"), "SUCCESS") && StringUtils.equals(stringStringMap.get("result_code"), "SUCCESS")) {
                //成功
                flag = true;
                rb.setSucResult(null);
            } else {
                //失败重试一次  系统繁忙，请稍后再试
                if (StringUtils.equals(stringStringMap.get("err_code"), "SYSTEMERROR")) {
                    stringStringMap = weChatPayUtils.wechatPay(money, wechatOpenId, shoppingMallId, "现金红包兑换" + money + "元", 2, Integer.valueOf(map.get("channel")));
                    if (StringUtils.equals(stringStringMap.get("return_code"), "SUCCESS") && StringUtils.equals(stringStringMap.get("result_code"), "SUCCESS")) {
                        //成功
                        flag = true;
                    } else {
                        //仍然失败 ---->兑换失败
                        logger.error("--------微信支付失败---------:" + JSON.toJSON(stringStringMap).toString());
                        rb.setFailMsg(ApiResultType.SYSTE_BUSY);
                    }
                } else if (StringUtils.equals(stringStringMap.get("err_code"), "NOTENOUGH")) {
                    shoppingMallRestDao.downGoods(Integer.valueOf(goodsRestEntity.getId()));
                    //余额不足  下架
                    ChuangLanSmsUtil.sendSmsByPost(stringStringMap.get("return_msg") + "[微信支付]", TemplateCode.DOWN_GOODS.getTemplateContent(), "13552570975", true);
                    rb.setFailMsg(ApiResultType.MONEY_LIMIT);
                } else if (StringUtils.equals(stringStringMap.get("err_code"), "SENDNUM_LIMIT")) {
                    //该用户今日付款次数超过限制,如有需要请登录微信支付商户平台更改API安全配置.
                    logger.error("--------微信支付失败---------" + JSON.toJSON(stringStringMap).toString());
                    rb.setFailMsg(ApiResultType.MONEY_LIMIT);
                } else {
                    logger.error("--------微信支付失败---------" + JSON.toJSON(stringStringMap).toString());
                    rb.setFailMsg(ApiResultType.MONEY_LIMIT);
                }
            }
        }
        // }
        if (flag) {
            //插入积分兑换记录
            ShoppingMallIntegralExchangePhysicalRestEntity shoppingMall = new ShoppingMallIntegralExchangePhysicalRestEntity();
            //设置订单号
            shoppingMall.setId(Long.valueOf(shoppingMallId));
            //设置id
            shoppingMall.setGoodsId(Integer.valueOf(goodsRestEntity.getId()));
            //设置数量
            shoppingMall.setCount(1);
            //设置兑换状态
            shoppingMall.setStatus(2);
            //设置兑换手机号
            shoppingMall.setExchangeMobile(map.get("exchangeMobile"));
            //添加兑换记录
            shoppingMallRestDao.insertPhysical(shoppingMall, userInfoId);
            //记录积分消耗表
            userIntegralRestDao.insertShoppingMallIntegral(userInfoId, shoppingMallId, "-" + goodsRestEntity.getFengfengTicketValue(), goodsRestEntity.getGoodsName(), CategoryOfBehaviorEnum.SHOPPING_MALL_EXCHANGE.getIndex(), Double.parseDouble("-" + goodsRestEntity.getFengfengTicketValue()));
            //修改总积分值
            userIntegralRestDao.updateForTotalIntegral(userInfoId, Integer.valueOf("-" + goodsRestEntity.getFengfengTicketValue()), new BigDecimal("-" + goodsRestEntity.getFengfengTicketValue()));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", 2);
            rb.setResult(jsonObject);
            return rb;
        } else {
            //插入积分兑换记录
            ShoppingMallIntegralExchangePhysicalRestEntity shoppingMall = new ShoppingMallIntegralExchangePhysicalRestEntity();
            //设置订单号
            shoppingMall.setId(Long.valueOf(shoppingMallId));
            //设置id
            shoppingMall.setGoodsId(Integer.valueOf(goodsRestEntity.getId()));
            //设置数量
            shoppingMall.setCount(1);
            //设置兑换状态
            shoppingMall.setStatus(3);
            //设置兑换手机号
            shoppingMall.setExchangeMobile(map.get("exchangeMobile"));
            //设置失败原因
            shoppingMall.setDescription(JSON.toJSON(stringStringMap).toString());
            //添加兑换记录
            shoppingMallRestDao.insertPhysical(shoppingMall, userInfoId);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", 3);
            rb.setResult(jsonObject);
            return rb;
        }
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
            //成功
            shoppingMallRestDao.update(customerOrderNo, 2, null);
        } else {
            //获取订单状态
            ShoppingMallIntegralExchangeRestEntity shopping = shoppingMallRestDao.checkStatusById(customerOrderNo);
            //根据goodsid--->获取
            GoodsRestEntity goodsById = shoppingMallRestDao.getGoodsById(shopping.getGoodsId());
            //兑换失败
            shoppingMallRestDao.update(customerOrderNo, 3, reMark);
            //记录积分消耗表
            userIntegralRestDao.insertShoppingMallIntegral(shopping.getUserInfoId() + "", customerOrderNo, goodsById.getFengfengTicketValue() + "", goodsById.getGoodsName(), CategoryOfBehaviorEnum.SHOPPING_MALL_EXCHANGE.getIndex(), Double.parseDouble(goodsById.getFengfengTicketValue() + ""));
            //修改总积分值
            userIntegralRestDao.updateForTotalIntegral(shopping.getUserInfoId() + "", Integer.valueOf(goodsById.getFengfengTicketValue()), new BigDecimal(goodsById.getFengfengTicketValue()));
        }
    }

    /**
     * 积分兑换---->历史兑换列表
     *
     * @param userInfoId
     * @return
     */
    @Override
    public List<ShoppingMallIntegralExchangePhysicalRestDTO> historyIntegralExchange(String userInfoId) {
        List<ShoppingMallIntegralExchangePhysicalRestDTO> list = shoppingMallRestDao.historyIntegralExchange(userInfoId);
        if (list.size() > 0) {
            if (list.get(0).getStatus() == 1 && list.get(0).getGoodsType() == 1) {
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
                jsonObject.put("customerorderno", list.get(0).getId() + "");
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
                        shoppingMallRestDao.update(list.get(0).getId() + "", 2, jsonObject1.getString("ReMark"));
                        list.get(0).setStatus(2);
                    } else if (StringUtils.equals(jsonObject1.getString("Status"), "失败")) {
                        shoppingMallRestDao.update(list.get(0).getId() + "", 3, jsonObject1.getString("ReMark"));
                        //记录积分消耗表
                        userIntegralRestDao.insertShoppingMallIntegral(userInfoId, list.get(0).getId() + "", list.get(0).getFengfengTicketValue()+"", list.get(0).getGoodsName(), CategoryOfBehaviorEnum.SHOPPING_MALL_EXCHANGE.getIndex(), Double.parseDouble(list.get(0).getFengfengTicketValue()+""));
                        //修改总积分值
                        userIntegralRestDao.updateForTotalIntegral(userInfoId, Integer.valueOf(list.get(0).getFengfengTicketValue()), new BigDecimal(list.get(0).getFengfengTicketValue()));
                        list.get(0).setStatus(3);
                    }
                } else {
                    shoppingMallRestDao.update(list.get(0).getId() + "", 3, jsonObject1.getString("ReMark"));
                    list.get(0).setStatus(3);
                }
            }
        }
        return list;
    }

    @Override
    public boolean checkExchangeStatus(String userInfoId) {
        int count = shoppingMallRestDao.checkExchangeStatus(userInfoId);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<ReportShopRestDTO> reportShop() {
        return shoppingMallRestDao.reportShop();
    }

    @Override
    public List<ReportShopRestDTO> reportForInvited() {
        return shoppingMallRestDao.reportForInvited();
    }

    /**
     * 好友累计贡献积分+已邀请好友数+好友邀请好友数
     * @param userInfoId
     * @return
     */
    @Override
    public Map<String, Object> invitedData(String userInfoId) {
        //好友累计贡献积分
        Double integrales = userIntegralRestDao.getIntegralByType(userInfoId,AcquisitionModeEnum.BONUS.getIndex());
        //已邀请好友数
        Integer invitedNum = userInviteRestDao.getCountForInvitedUsers(userInfoId);
        //好友邀请好友数
        Integer invitedNum2 = userInviteRestDao.getCountForReInvitedUsers(userInfoId);
        Map<String,Object> map = new HashMap<>();
        map.put("invitedIntegrales",integrales);
        map.put("invitedFriendsNum",invitedNum);
        map.put("invitedFriendsNum2",invitedNum2);
        return map;
    }
}
