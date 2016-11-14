package com.botu.img.bean;

/**
 * 微信个人基本信息
 * @author: swolf
 * @date : 2016-11-10 11:59
 */
public class WxUserInfo {

    /**
     * city : Shenzhen
     * country : CN
     * headimgurl : http://wx.qlogo.cn/mmopen/ajNVdqHZLLBpEcONqibtScEwGGnDMcj3iacp32o7hkYlPAtFfiag5vbHz91nnDbf2OFOX0LvQe34Dz9G6lMTvDYUA/0
     * language : zh_CN
     * nickname : Gson
     * openid : ovSu_weC_CdibNOxHw5Uh9oXk7ek
     * privilege : []
     * province : Guangdong
     * sex : 1
     * unionid : oDpJSwKGadvPQYUyXd6vJNlXkg-Y
     */

    private String city;
    private String headimgurl;
    private String nickname;
    private String province;
    private int sex;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "WxUserInfo{" +
                "city='" + city + '\'' +
                ", headimgurl='" + headimgurl + '\'' +
                ", nickname='" + nickname + '\'' +
                ", province='" + province + '\'' +
                ", sex=" + sex +
                '}';
    }
}
