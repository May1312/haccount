package com.fnjz.utils.upload;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;

public class QiNiuUploadFileUtils {
    String ak = "cKJY4fTe4aaUX3I1xp0TWdXsjKBGYFA7l2nyVI7t";
    String sk = "AOyos15XUofQQN22qd2TJleC5pJp9sKB3FhgNgir";    // 密钥配置


    Auth auth = Auth.create(ak, sk);    // TODO Auto-generated constructor stub

    public String getUpToken(String bucketname) {
        return auth.uploadToken(bucketname, null, 3600, new StringMap().put("insertOnly", 1));
    }


    public String base64Upload(String imageBase64, int length, String domain, String bucketname, String fileName) throws IOException {

        String returnStr = "";

        String url = "http://upload-z1.qiniu.com/putb64/" + length + "/key/" + UrlSafeBase64.encodeToString(fileName);
        RequestBody rb = RequestBody.create(null, imageBase64);
        Request request = new Request.Builder().
                url(url).
                addHeader("Content-Type", "application/octet-stream")
                .addHeader("Authorization", "UpToken " + getUpToken(bucketname))
                .post(rb).build();
        System.out.println(request.headers());
        OkHttpClient client = new OkHttpClient();
        okhttp3.Response response = client.newCall(request).execute();
        System.out.println(response);
        if (response.message().equalsIgnoreCase("OK") && response.code() == 200) {
            returnStr = domain + fileName;
        } else {
            returnStr = "上传失败请重新上传";
        }
        return returnStr;
    }

    public String bytesUpload(String domain, byte[] uploadBytes, String bucket, String key) {
        /*String domain="http://ow78fg6o1.bkt.clouddn.com/";
        String bucket = "stockintro";*/
        int hashCode = uploadBytes.hashCode();

        //String key = UUID.randomUUID().toString()+String.valueOf(hashCode)+"."+ bucket;
        //构造一个带指定Zone对象的配置类
        Zone zone0 = Zone.zone1();
        com.qiniu.storage.Configuration cfg = new com.qiniu.storage.Configuration(zone0);
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);

        String upToken = auth.uploadToken(bucket);
        String result = "";
        try {
            Response response = uploadManager.put(uploadBytes, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            //System.out.println(putRet.hash);
            result = domain + key;
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }
        return result;

    }


    /*public void delFile(String bucket, String key) {

        //实例化一个BucketManager对象
        BucketManager bucketManager = new BucketManager(auth, null);
        if (bucket.trim().isEmpty()) {
            bucket = "";
        }
        //要测试的空间和key，并且这个key在你空间中存在
        try {
            //调用delete方法移动文件
            bucketManager.delete(bucket, key);
        } catch (QiniuException e) {
            //捕获异常信息
            Response r = e.response;
            System.out.println(r.toString());
        }
    }*/

    public static void main(String[] args) throws Exception {
        String file = "C:\\Users\\zyh\\Pictures\\Saved Pictures\\1.jpg";//图片路径

        /*FileInputStream fis = null;
        int l = (int) (new File(file).length());
        byte[] src = new byte[l];
        fis = new FileInputStream(new File(file));
        fis.read(src);
        String file64 = Base64.encodeToString(src, 0);

        QiNiuUploadFileUtils qiNiuUploadFileUtils = new QiNiuUploadFileUtils();

        String fileName = DateUtils.getMillis() + "_" + new File(file).getName();

        String s = qiNiuUploadFileUtils.base64Upload(file64, l, QiNiuStorageSpace.LABEL_PICTURE.getDomain(),
                QiNiuStorageSpace.LABEL_PICTURE.getStorageSpaceName(),
                fileName);*/

        //byte[] bytes = decodeBase64(file64);
        //String s = new QiNiuUploadFileUtils().bytesUpload(QiNiuStorageSpace.LABEL_PICTURE.getDomain(), bytes, QiNiuStorageSpace.LABEL_PICTURE.getStorageSpaceName(), "9999.jpg");

        //System.out.println("返回地址====" + s);
    }


}
