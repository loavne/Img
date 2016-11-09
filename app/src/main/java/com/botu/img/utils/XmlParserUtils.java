package com.botu.img.utils;

import com.botu.img.bean.UpdateInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * XML文件解析器
 * @author: swolf
 * @date : 2016-11-09 16:15
 */
public class XmlParserUtils {
    /**
     * @param inputStream 输入流
     * @param info 实体
     */
    public static UpdateInfo xmlParser(InputStream inputStream, UpdateInfo info){
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(inputStream, "utf-8");
            int type = xmlPullParser.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("update".equals(xmlPullParser.getName())) {
                            info =  new UpdateInfo();
                        } else if ("version".equals(xmlPullParser.getName())) {
                            info.setVersion(xmlPullParser.nextText());
                        } else if ("description".equals(xmlPullParser.getName())) {
                            info.setDescription(xmlPullParser.nextText());
                        } else if ("apkurl".equals(xmlPullParser.getName())) {
                            info.setApkurl(xmlPullParser.nextText());
                        }
                        break;
                }
                type = xmlPullParser.next();
            }
            return info;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
