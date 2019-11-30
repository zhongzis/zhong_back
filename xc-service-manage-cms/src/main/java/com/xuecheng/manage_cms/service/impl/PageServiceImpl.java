package com.xuecheng.manage_cms.service.impl;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.service.PageService;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
public class PageServiceImpl implements PageService {

    @Autowired
    CmsPageRepository cmsPageRepository;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    /**
     * 页面列表分页查询
     * @param page 当前页码
     * @param size 每页显示数据数
     * @param queryPageRequest 查询条件
     * @return
     */
    @Override
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        //条件匹配器         
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        //模糊匹配别名
        exampleMatcher = exampleMatcher.withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());

        //创建条件值对象
        CmsPage cmsPage = new CmsPage();
        //判断别名是否为空
        if(StringUtils.isNotEmpty(queryPageRequest.getPageAliase())){
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        //判断站点id是否为空
        if(StringUtils.isNotEmpty(queryPageRequest.getSiteId())){
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        //判断模版id是否为空
        if(StringUtils.isNotEmpty(queryPageRequest.getTemplateId())){
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }
        //创建条件实例对象
        Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);

        //判断当前页码是否小于0
        if(page <= 0){
            page = 1;
        }
        //为了适应mongodb的接口将页码减一
        page = page -1;
        //判断size是否小于0
        if(size <= 0){
            size = 10;
        }
        //分页对象
        Pageable pageable = new PageRequest(page, size);
        //根据分页对象和条件实例对象查询数据
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);
        //新建QueryResult<T> 对象
        QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<>();
        //分别给QueryResult<T> 对象中的list集合total赋值
        cmsPageQueryResult.setList(all.getContent());
        cmsPageQueryResult.setTotal(all.getTotalElements());
        //返回结果
        return new QueryResponseResult(CommonCode.SUCCESS,cmsPageQueryResult);
    }

//    /**
//     * 新增页面,不使用自定义异常
//     * @param cmsPage
//     * @return
//     */
//    @Override
//    public CmsPageResult add(CmsPage cmsPage) {
//        //1.判断页面是否存在
//       CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(
//                cmsPage.getPageName(),
//                cmsPage.getSiteId(),
//                cmsPage.getPageWebPath());
//        //2.如果不存在，则保存页面,返回页面和success
//        if(cmsPage1 == null){
//            cmsPage.setPageId(null);//添加页面主键由spring data 自动生成
//            cmsPageRepository.save(cmsPage);
//            return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
//        }
//
//        //3.如果存在，则返回fail,null
//        return new CmsPageResult(CommonCode.FAIL,null);
//    }

    /**
     * 新增页面，使用自定义异常
     * @param cmsPage
     * @return
     */
    @Override
    public CmsPageResult add(CmsPage cmsPage) {
        //1.判断页面是否存在
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(
                cmsPage.getPageName(),
                cmsPage.getSiteId(),
                cmsPage.getPageWebPath());
        //2.如果存在，则抛出自定义异常
        if(cmsPage1 != null){
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        //3.如果不存在，则保存，并出给提示代码和提示信息
        //添加页面主键由spring data 自动生成
        cmsPage.setPageId(null);
        cmsPageRepository.save(cmsPage);
        return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
    }


    /**
     * 根据id查询页面
     * @param id
     * @return
     */
    @Override
    public CmsPage findById(String id) {
        //调用dao层，查询页面
        Optional<CmsPage> cmsPage = cmsPageRepository.findById(id);
        //若存在，则返回页面
        if(cmsPage.isPresent()){
            return cmsPage.get();
        }
        //若不存在，则返回null
        return null;
    }

    /**
     * 更新页面
     * @param id
     * @param cmsPage
     * @return
     */
    @Override
    public CmsPageResult update(String id, CmsPage cmsPage) {
        //根据Id查询页面信息
        CmsPage cmsPage1 = this.findById(id);
        //若存在，则调用set方法更新数据，并保存
        if(cmsPage1!=null){
            cmsPage1.setPageAliase(cmsPage.getPageAliase());
            cmsPage1.setTemplateId(cmsPage1.getTemplateId());
            cmsPage1.setSiteId(cmsPage.getSiteId());
            cmsPage1.setPageName(cmsPage.getPageName());
            cmsPage1.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            cmsPage1.setPageWebPath(cmsPage.getPageWebPath());
            cmsPage1.setDataUrl(cmsPage.getDataUrl());
            CmsPage save = cmsPageRepository.save(cmsPage1);
            if(save!=null){
                return new CmsPageResult(CommonCode.SUCCESS,save);
            }
        }
        //若不存在，则返回失败
        return new CmsPageResult(CommonCode.FAIL,null);
    }

    /**
     * 根据Id删除页面
     * @param id
     * @return
     */
    @Override
    public ResponseResult deleteById(String id) {
        //根据Id查询页面信息
        CmsPage cmsPage1 = this.findById(id);
        if(cmsPage1!=null){
            //若存在，删除页面
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        //若不存在，则返回fail
        return new ResponseResult(CommonCode.FAIL);
    }

    /**
     * 执行页面静态化
     * @param pageId
     * @return
     */
    @Override
    public String getPageHtml(String pageId) {
        //获取数据模型
        Map model = this.getModelByPageId(pageId);
        //若数据模型数据为空，抛出异常
        if(model == null){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        //获取页面模版
        String templateContent = getTemplateByPageId(pageId);
        //若页面模版数据为空，抛出异常
        if(StringUtils.isEmpty(templateContent)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //执行静态化
        String html = generateHtml(templateContent, model);
         //判断html内容是否为空
        if(StringUtils.isEmpty(html)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        return html;
    }

    /**
     * 页面静态化
     * @param template
     * @param model
     * @return
     */
    @Override
    public String generateHtml(String template, Map model) {
        try {
            //生成配置类
            Configuration configuration = new Configuration(Configuration.getVersion());
            //模版加载器
            StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
            stringTemplateLoader.putTemplate("template",template);
            //配置模版加载器
            configuration.setTemplateLoader(stringTemplateLoader);
            //获取模版
            Template template1 = configuration.getTemplate("template");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template1,model);
            return html;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取页面模版
     * @param pageId
     * @return
     */
    @Override
    public String   getTemplateByPageId(String pageId) {
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        //页面不存在,抛出异常
        if(!optional.isPresent()){
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //页面存在，取出模版id
        CmsPage cmsPage = optional.get();
        String templateId = cmsPage.getTemplateId();
        //判断模版id是否存在
        if(StringUtils.isEmpty(templateId)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //根据模版id找到模版数据模型
        Optional<CmsPage> optional1 = cmsPageRepository.findById(templateId);
        //判断模版数据模型是否存在
        if(optional1.isPresent()){
            CmsPage cmsPage1 = optional1.get();
            String templateId1 = cmsPage1.getTemplateId();
            //根据id查询文件
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateId1)));
            //打开下载流对象
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            //创建gridFsResource，用于获取流对象
            GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
            //获取流中的数据
            try {
                String connect = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
                return connect;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取页面模型数据
     * @param pageId
     * @return
     */
    @Override
    public Map getModelByPageId(String pageId) {
        //查询页面信息
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        //页面不存在,抛出异常
        if(!optional.isPresent()){
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //页面存在，取出DataUrl
        CmsPage cmsPage = optional.get();
        String dataUrl = cmsPage.getDataUrl();
        //判断dataUrl是否为空
        if(StringUtils.isEmpty(dataUrl)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        //根据restTemplate获取模型数据
        ResponseEntity<Map> forEntity = restTemplate.getForEntity("dataUrl", Map.class);
        Map body = forEntity.getBody();
        return body;
    }

}
