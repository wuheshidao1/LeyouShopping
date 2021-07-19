package com.leyou.goods.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

@Service
public class GoodsHtmlService {
    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private GoodsService goodsService;

    public void createHtml(Long spuId){

        PrintWriter printWriter = null;

        //初始化运行上下文
        Context context = new Context();
        //设置数据模型
        context.setVariables(goodsService.loadData(spuId));

        try {

            //把靜態文件放到nginx下
            File file = new File("D:\\develop\\ngin1.14.0\\nginx1.14.0\\html\\item\\" + spuId + ".html");
            printWriter = new PrintWriter(file);

            templateEngine.process("item",context,printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            printWriter.close();
        }
    }

    public void deleteHtml(Long id) {
        File file = new File("D:\\develop\\ngin1.14.0\\nginx1.14.0\\html\\item\\" + id + ".html");
        file.deleteOnExit();
    }
}
