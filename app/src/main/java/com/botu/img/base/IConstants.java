package com.botu.img.base;

/**
 * @author: swolf
 * @date : 2016-11-09 17:43
 */
public class IConstants {
    public static final String loginReceiver = "com.botu_img_login";
    public static final String networkStateReceiver = "com.botu.img.service.NetworkState";

    //是否登录
    public static final String isLogin = "islogin";
    //头像
    public static final String header = "header";
    public static final String username = "username";
    public static final String openId = "openid";

    public static final String SCOPE = "all";

    public static final String WX_APP_ID = "wx4ba043e232bd1d17";
    public static final String WX_APP_SECRET = "0614c1a696a4265ba3bb4c78e745f4a1";

    public static final String QQ_APP_ID = "1105806134";
    public static final String QQ_APP_KEY = "m4l5qWXxuk73LNeb";

    public static final String SINA_APP_KEY = "2651927103";
    public static final String REDIRECT_URL = "https://www.baidu.com"; //回调页

//    public static final String NEWS_URL = "http://61.144.222.81/masonry_ue/interface_pic.php";
    public static final String BASE_URL = "http://pic.newerest.com";

    //登录
    public static final String LOGIN_URL = "http://pic.newerest.com/home/api/login.php";

    //图片分类
    public static final String NEWS_LIST_URL = "http://pic.newerest.com/home/api/getCategory.php";
    public static final String parent_id = "parent_id";
    public static final String gif = "gif";

    //图片内容
    public static final String NEWS_CONTENT = "http://pic.newerest.com/home/api/getPicture.php";
    public static final String category_id = "category_id";

    public static final String TITLE = "title";

    //添加收藏
    public static final String COLLECT = "http://pic.newerest.com/home/api/addFavorites.php";
    public static final String member_id = "member_id";
    public static final String picture_id = "picture_id";
    public static final String userId = "user_id";

    //获取收藏
    public static final String GET_COLLECT = "http://pic.newerest.com/home/api/getFavorites.php";
    //删除收藏
    public static final String DEL_COLLECT = "http://pic.newerest.com/home/api/delFavorites.php";



    //添加足迹
    public static final String FOOT = "http://pic.newerest.com/home/api/addFootprint.php";
    //获取足迹
    public static final String GET_FOOT = "http://pic.newerest.com/home/api/getFootprint.php";
    //删除足迹
    public static final String DEL_FOOT = "http://pic.newerest.com/home/api/delFootprint.php";

    //添加分享
    public static final String SHARE = "http://pic.newerest.com/home/api/addShare.php";
    //获取分享
    public static final String GET_SHARE = "http://pic.newerest.com/home/api/getShare.php";





}
