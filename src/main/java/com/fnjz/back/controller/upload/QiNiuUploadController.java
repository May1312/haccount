package com.fnjz.back.controller.upload;

import com.fnjz.utils.upload.QiNiuStorageSpace;
import com.fnjz.utils.upload.QiNiuUploadFileUtils;
import org.jeecgframework.core.common.model.json.AjaxJson;
import org.jeecgframework.core.util.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

@Controller
@RequestMapping("/qiNiuUploadController")
public class QiNiuUploadController {


    @RequestMapping(params = "uploadFiles")
    @ResponseBody
    /**
     * 文件类型直接上传
     */
    public AjaxJson uploadFiles(MultipartHttpServletRequest request, HttpServletResponse response) throws IOException {

        MultipartFile File = request.getFile("file");
        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf = null;
        while (itr.hasNext()) {
            mpf = request.getFile(itr.next());
        }
        String originalFilename = mpf.getOriginalFilename();
        String fileName = DateUtils.getMillis() + "_" + originalFilename;
        String s = new QiNiuUploadFileUtils().bytesUpload(QiNiuStorageSpace.LABEL_PICTURE.getDomain(),
                mpf.getBytes(),
                QiNiuStorageSpace.LABEL_PICTURE.getStorageSpaceName(),
                fileName);
        AjaxJson j = new AjaxJson();
        j.setObj(s);
        return j;
    }

    @RequestMapping(params = "uploadFilesByBase64")
    @ResponseBody
    /**
     * 图片转base64上传
     */
    public AjaxJson uploadFilesByBase64(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String imageBase64 = request.getParameter("imageBase64");

        String domain = "";

        String backetName = "";

        String fileName = "";

        int length = 888;
        String s = new QiNiuUploadFileUtils().base64Upload(imageBase64, length, domain, backetName, fileName);
        AjaxJson j = new AjaxJson();
        j.setObj(s);
        return j;
    }

    public HashMap<String, String> chooseDomainByBacketName(String storageSpaceName){
        String domain = "";
        String backetName = "";
        if (storageSpaceName.equalsIgnoreCase(QiNiuStorageSpace.LABEL_PICTURE.getStorageSpaceName())){
            domain=QiNiuStorageSpace.LABEL_PICTURE.getDomain();
            backetName=QiNiuStorageSpace.LABEL_PICTURE.getStorageSpaceName();
        }else if (storageSpaceName.equalsIgnoreCase(QiNiuStorageSpace.HEAD_PICTURE.getStorageSpaceName())){
            domain = QiNiuStorageSpace.HEAD_PICTURE.getDomain();
            backetName=QiNiuStorageSpace.HEAD_PICTURE.getStorageSpaceName();
        }else if (storageSpaceName.equalsIgnoreCase(QiNiuStorageSpace.FEEDBACK_PICTURE.getStorageSpaceName())){
            domain=QiNiuStorageSpace.FEEDBACK_PICTURE.getDomain();
            backetName=QiNiuStorageSpace.FEEDBACK_PICTURE.getStorageSpaceName();
        }
        HashMap<String, String> spaces = new HashMap<>();
        spaces.put("domain",domain);
        spaces.put("backetName",backetName);
        return spaces;
    }
}
