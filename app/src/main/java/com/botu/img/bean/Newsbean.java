package com.botu.img.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * @author: swolf
 * @date : 2016-11-17 16:13
 */
@Entity
public class Newsbean implements Parcelable {

    /**
     * tilte : 居民改动电表致显示度数仅为用电量十分之一
     * picurl : http://img1.gtimg.com/news/pics/hv1/8/29/1107/71990078.jpg
     */
    @Id
    public Long id;
    public String tilte;
    public String picurl;

    @Generated(hash = 1322460635)
    public Newsbean(Long id, String tilte, String picurl) {
        this.id = id;
        this.tilte = tilte;
        this.picurl = picurl;
    }

    @Generated(hash = 385015437)
    public Newsbean() {
    }

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

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.tilte);
        dest.writeString(this.picurl);
    }

    protected Newsbean(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.tilte = in.readString();
        this.picurl = in.readString();
    }

    public static final Parcelable.Creator<Newsbean> CREATOR = new Parcelable.Creator<Newsbean>() {
        @Override
        public Newsbean createFromParcel(Parcel source) {
            return new Newsbean(source);
        }

        @Override
        public Newsbean[] newArray(int size) {
            return new Newsbean[size];
        }
    };

    @Override
    public String toString() {
        return "Newsbean{" +
                "id=" + id +
                ", tilte='" + tilte + '\'' +
                ", picurl='" + picurl + '\'' +
                '}';
    }
}
