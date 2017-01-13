package com.botu.img.bean;

import java.io.Serializable;

/**
 * @author: swolf
 * @date : 2016-12-26 10:09
 */
public class ImgType implements Serializable{

    /**
     * category_id : 2
     * filename : 586e11fa96231.png
     * filepath : /upload/2017-01-05/
     * height : 900
     * id : 13
     * intime : 1483608642
     * isFav : 0
     * label : 明星,明星写真,大陆女明星,女演员,刘诗诗
     * suffix : image/jpeg
     * title : 刘诗诗演绎摩登风韵
     * width : 717
     */

    public int category_id;
    public String filename;
    public String filepath;
    public int height;
    public int id;
    public String intime;
    public int isFav;
    public String label;
    public String suffix;
    public String title;
    public int width;

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
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

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIntime() {
        return intime;
    }

    public void setIntime(String intime) {
        this.intime = intime;
    }

    public int getIsFav() {
        return isFav;
    }

    public void setIsFav(int isFav) {
        this.isFav = isFav;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeInt(this.category_id);
//        dest.writeString(this.filename);
//        dest.writeString(this.filepath);
//        dest.writeInt(this.height);
//        dest.writeInt(this.id);
//        dest.writeString(this.intime);
//        dest.writeInt(this.isFav);
//        dest.writeString(this.label);
//        dest.writeString(this.suffix);
//        dest.writeString(this.title);
//        dest.writeInt(this.width);
//    }
//
//    public ImgType() {
//    }
//
//    protected ImgType(Parcel in) {
//        this.category_id = in.readInt();
//        this.filename = in.readString();
//        this.filepath = in.readString();
//        this.height = in.readInt();
//        this.id = in.readInt();
//        this.intime = in.readString();
//        this.isFav = in.readInt();
//        this.label = in.readString();
//        this.suffix = in.readString();
//        this.title = in.readString();
//        this.width = in.readInt();
//    }
//
//    public static final Parcelable.Creator<ImgType> CREATOR = new Parcelable.Creator<ImgType>() {
//        @Override
//        public ImgType createFromParcel(Parcel source) {
//            return new ImgType(source);
//        }
//
//        @Override
//        public ImgType[] newArray(int size) {
//            return new ImgType[size];
//        }
//    };
}
