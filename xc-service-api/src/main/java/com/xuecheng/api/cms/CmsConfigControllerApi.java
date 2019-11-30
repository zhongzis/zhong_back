package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsConfig;

public interface CmsConfigControllerApi {
    //通过id查询cmsConfig的信息
    public CmsConfig getModel(String id);
}
