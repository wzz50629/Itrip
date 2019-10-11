package com.bdqn.text;


import sun.applet.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Setfremaket {
    public static void main(String[] args) throws Exception {

        List<User> users=new ArrayList<>();
        for (int i=0;i<10;i++)
        {
            User user=new User();
            user.setId(i);
            user.setName(i+"名字");

            users.add(user);
        }

        Map<String,Object> map=new HashMap<>();
        map.put("kn","Java程序中的字符串");


        Map<String,Object> map1=new HashMap<>();
        map1.put("li",users);

     /*   Configuration configuration=new Configuration();
        configuration.setDefaultEncoding("utf-8");
        configuration.setDirectoryForTemplateLoading(new File("F:\\学生 实例\\337-ItripProject\\itripbeans\\src\\main\\resources"));

        Template template=configuration.getTemplate("ConfigTxt.flt");

        template.process(map1,new FileWriter("F:\\c.txt"));*/
    }
}
