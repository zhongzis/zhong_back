package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;

public interface CmsPageControllerApi {
    //页面查询
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);
    //新增页面
    public CmsPageResult add(CmsPage cmsPage);
    //通过Id查询页面
    public CmsPage findById(String id);
    //修改页面
    public CmsPageResult edit(String id,CmsPage cmsPage);
    //删除页面
    public ResponseResult delete(String id);
}
