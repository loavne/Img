package com.botu.img.bean;

import java.io.Serializable;

/**
 * @author: swolf
 * @date : 2016-12-23 11:14
 */
public class ListType implements Serializable{


    /**
     * ename : gaoxiao
     * filename : 586df6c221048.png
     * filepath : /upload/2017-01-05/
     * gif : 0
     * id : 55
     * level : 1
     * name : 搞笑
     * parent_id : 0
     * sort : 0
     * state : 1
     * task_id : 1
     * url : http://tu.enterdesk.com/funny/
     */

    public String ename;
    public String filename;
    public String filepath;
    public String gif;
    public int id;
    public String level;
    public String name;
    public String parent_id;
    public String sort;
    public String state;
    public String task_id;
    public String url;

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getGif() {
        return gif;
    }

    public void setGif(String gif) {
        this.gif = gif;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
