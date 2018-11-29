package com.fnjz.front.controller.api.updatedata;

import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.usercommtypepriority.UserCommTypePriorityRestEntity;
import com.fnjz.front.entity.api.userprivatelabel.UserPrivateLabelRestEntity;
import com.fnjz.front.service.api.usercommtypepriority.UserCommTypePriorityRestServiceI;
import com.fnjz.front.service.api.userprivatelabel.UserPrivateLabelRestService;
import net.sf.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Auther: yonghuizhao
 * @Date: 2018/11/26 10:36
 * @Description:
 */
@RestController
@RequestMapping(RedisPrefix.BASE_URL)
public class UpdateData {
    @Autowired
    private UserCommTypePriorityRestServiceI userCommTypePriorityRestService;
    @Autowired
    private UserPrivateLabelRestService userPrivateLabelRestService;


    @RequestMapping(value = "/updateRelationData", method = RequestMethod.GET)
    public void updateRelationData() {
        List<UserCommTypePriorityRestEntity> list = userCommTypePriorityRestService.getList(UserCommTypePriorityRestEntity.class);
        JSONArray jsonArray = null;
        if (list.size() > 0) {
            for (UserCommTypePriorityRestEntity userCommTypePriorityRestEntity : list) {
                String relation = userCommTypePriorityRestEntity.getRelation();
                int currentUserInfoId = userCommTypePriorityRestEntity.getUserInfoId();
                if (StringUtils.isNotEmpty(relation)) {
                    //json字符串转数组
                    jsonArray = JSONArray.fromObject(relation);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        Map array_map = (Map) jsonArray.get(i);
                        String typeId = (String) array_map.get("id");
                        //根据userinfoid,typeid 确定唯一记录
                        String sql = "select id from hbird_user_private_label where user_info_id =" + currentUserInfoId + "  AND type_id = '" + typeId + "'";
                        List<Object> listbySql = userPrivateLabelRestService.findListbySql(sql);
                        if (listbySql.size() > 0) {
                            String typeid = listbySql.get(0).toString();
                            //更新typeid
                            array_map.put("id", typeid);
                        }
                    }
                    //更新数据
                    if (jsonArray != null && jsonArray.size() > 0) {
                        userCommTypePriorityRestEntity.setRelation(jsonArray.toString());
                        userCommTypePriorityRestService.updateEntitie(userCommTypePriorityRestEntity);
                        System.out.println(userCommTypePriorityRestEntity.getId()+"----------更新完毕");
                    }

                }
            }
        }
    }
}
