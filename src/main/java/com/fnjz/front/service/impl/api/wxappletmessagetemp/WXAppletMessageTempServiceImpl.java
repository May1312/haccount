package com.fnjz.front.service.impl.api.wxappletmessagetemp;

import com.fnjz.front.dao.WXAppletMessageTempRestDao;
import com.fnjz.front.entity.api.wxappletmessagetemp.WXAppletMessageTempRestEntity;
import com.fnjz.front.service.api.wxappletmessagetemp.WXAppletMessageTempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by yhang on 2018/12/1.
 */
@Service("wxappletMessageTempServiceImpl")
public class WXAppletMessageTempServiceImpl implements WXAppletMessageTempService {

    @Autowired
    private WXAppletMessageTempRestDao wxappletMessageTempRestDao;

    @Override
    @Transactional(rollbackFor=Exception.class)
    public void foreachInsert(List<WXAppletMessageTempRestEntity> list) {
        list.forEach(v->{
            wxappletMessageTempRestDao.insert(v);
        });
    }
}
