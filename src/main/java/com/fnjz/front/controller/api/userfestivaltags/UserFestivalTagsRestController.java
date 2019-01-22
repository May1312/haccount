package com.fnjz.front.controller.api.userfestivaltags;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.userfestivaltags.FestivalTagsRestEntity;
import com.fnjz.front.service.api.userfestivaltags.UserFestivalTagsRestService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户-节日贴纸-标语关联表
 * Created by yhang on 2019/1/18.
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class UserFestivalTagsRestController {

    private static final Logger logger = Logger.getLogger(UserFestivalTagsRestController.class);

    @Autowired
    private UserFestivalTagsRestService userFestivalTagsRestService;

    /**
     * 获取用户贴纸列表
     * @param type
     * @param request
     * @return
     */
    @RequestMapping(value = "/getFestivalTags/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getFestivalTags(@PathVariable("type") String type, HttpServletRequest request, @RequestParam String festivalType) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        try {
            List<FestivalTagsRestEntity> list = userFestivalTagsRestService.getFestivalTags(userInfoId,festivalType);
            return new ResultBean(ApiResultType.OK, list);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @RequestMapping(value = {"/getFestivalTags"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getFestivalTags(HttpServletRequest request, @RequestParam String festivalType) {
        return this.getFestivalTags(null, request, festivalType);
    }
}
