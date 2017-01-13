package com.botu.img.utils;

/**
 * @author: swolf
 * @date : 2016-11-17 17:09
 */
public class HtmlParser {
    /**
     * 获取Html页面中Body标签内容
     * @param result
     * @return
     */
    public static String getHtmlBody(String result) {
        return null;
    }
//        StringBuilder stringBuilder = new StringBuilder();
//        Parser parser = new Parser();
//        try {
//            parser.setInputHTML(result);
//            parser.setEncoding(parser.getURL());
//
//            HtmlPage page = new HtmlPage(parser);
//            parser.visitAllNodesWith(page);
//
//            NodeList list = page.getBody();
//            for(NodeIterator iterator = list.elements(); iterator.hasMoreNodes();) {
//                Node node = iterator.nextNode();
//                String html = node.toHtml();
//                stringBuilder.append(html);
//            }
//        } catch (ParserException e) {
//            e.printStackTrace();
//        }
//        return stringBuilder.toString();
//    }
}
