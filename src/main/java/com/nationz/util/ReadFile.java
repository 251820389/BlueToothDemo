package com.nationz.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by LEO on 2016/8/12.
 */
public class ReadFile {

    private static void readFromFile() throws FileNotFoundException, IOException {
        File file = new File("E:\\helloworld.txt");// 指定要读取的文件
        FileReader reader = new FileReader(file);// 获取该文件的输入流


        char[] bb = new char[1024];// 用来保存每次读取到的字符
        String str = "";// 用来将每次读取到的字符拼接，当然使用StringBuffer类更好
        int n;// 每次读取到的字符长度
        while ((n = reader.read(bb)) != -1) {
            str += new String(bb, 0, n);
        }
        reader.close();// 关闭输入流，释放连接
        System.out.println(str);
    }

    private void readFile() throws IOException {
        File file = new File("E:\\test.jpg");
        FileInputStream in = new FileInputStream(file);// 指定要读取的图片
        byte[] bb = new byte[90];
        int n = 0;
        if (!file.exists()) {// 如果文件不存在，则创建该文件
            file.createNewFile();
        }
        while ((n = in.read(bb)) != -1) {

        }
        in.close();
    }
}
