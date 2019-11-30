package com.xuecheng.manage_cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Test
    public void testFindPage(){
        int page = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        System.out.println(all);
    }

    @Test
    public void testupdate(){
        //查询对象
       Optional<CmsPage> optional = cmsPageRepository.findById("5dbe4a90bc6e0f3d6183302e");

        if(optional.isPresent()){
            //设置要修改的值
            CmsPage cmsPage = optional.get();
            cmsPage.setPageAliase("test01");
            System.out.println(cmsPage);
            //修改
            CmsPage save = cmsPageRepository.save(cmsPage);
            System.out.println(save);
        }
    }

    @Test
    public void testfindAll(){
        //条件匹配器         
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        exampleMatcher = exampleMatcher.withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");
        cmsPage.setPageAliase("课程详情");
        Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);
        Pageable pageable = new PageRequest(0,10);
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);
        List<CmsPage> content = all.getContent();
        System.out.println(content);
        System.out.println("执行了++++++++++++++++");
    }
}
