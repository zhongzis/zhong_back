package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;

import java.util.Map;

public interface PageService {
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);
    public CmsPageResult add(CmsPage cmsPage);
    public CmsPage findById(String id);
    public CmsPageResult update(String id,CmsPage cmsPage);
    public ResponseResult deleteById(String id);
    public String getPageHtml(String pageId);
    public String generateHtml(String template, Map model);
    public String getTemplateByPageId(String pageId);
    public Map getModelByPageId(String pageId);
    }
