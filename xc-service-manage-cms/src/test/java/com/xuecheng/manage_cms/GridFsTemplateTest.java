package com.xuecheng.manage_cms;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GridFsTemplateTest {

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    /**
     * 向GridFs存储文件
     * @throws FileNotFoundException
     */
    @Test
    public void testgridFsTemplate() throws FileNotFoundException {
        //要存储的文件
        File file = new File("D:/index.ftl");
        //定义输入流
        FileInputStream fileInputStream = new FileInputStream(file);
        //向GridFs存储文件
        ObjectId id = gridFsTemplate.store(fileInputStream, "首页02", "");
        //得到文件id
        String fileId = id.toString();
        System.out.println(fileId);
    }

    /**
     * 向GridFs取文件
     * @throws IOException
     */
    @Test
    public void testgridFSBucket() throws IOException {
       String fileId = "5dd5040277801cb8f837b84b";
       //根据id查询文件
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        //打开下载流对象
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //创建gridFsResource，用于获取流对象
        GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
        //获取流中的数据
        String connect = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
        System.out.println(connect);
    }

    @Test
    public void testDel() throws IOException {
        //根据文件id删除fs.files和fs.chunks中的记录
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is("5dd5040277801cb8f837b84b")));
    }

}
