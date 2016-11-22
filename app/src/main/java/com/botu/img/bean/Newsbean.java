package com.botu.img.bean;

import java.io.Serializable;

/**
 * @author: swolf
 * @date : 2016-11-17 16:13
 */
public class Newsbean implements Serializable{

    /**
     * tilte : 居民改动电表致显示度数仅为用电量十分之一
     * picurl : http://img1.gtimg.com/news/pics/hv1/8/29/1107/71990078.jpg
     */

    public String tilte;
    public String picurl;

    public String getTilte() {
        return tilte;
    }

    public void setTilte(String tilte) {
        this.tilte = tilte;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }
}
