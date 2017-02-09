package com.botu.img.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils
{
	public static SimpleDateFormat sf = null;

	/**
	 * 获取系统时间 格式为："yyyy/MM/dd "
	 */
	public static String getCurrentDate()
	{
		Date d = new Date();
		sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sf.format(d);
	}

	/**
	 * 获取当前日期
	 *
	 * @return
	 */
	public static String getCurrentDateString() {
		String result = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
				Locale.getDefault());
		Date nowDate = new Date();
		result = sdf.format(nowDate);
		return result;
	}


	/**
	 * 时间戳转换成字符串yyyy-MM-dd
	 * 传入string时先转化为Long
	 */
	public static String getDateToString(long time)
	{
		sf = new SimpleDateFormat("yyyy-MM-dd");
		return sf.format(time*1000);
	}
	
	/**
	 * 时间总长转换成字符串mm:ss
	 */
	public static String getDateToString2(long time)
	{
		sf = new SimpleDateFormat("mm:ss");
		return sf.format(time);
	}
	
	/**
	 * 将字符串转为时间戳
	 */
	public static long getStringToDate(String time)
	{
		sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		try
		{
			date = sf.parse(time);
		} catch (ParseException e)
		{
			e.printStackTrace();
		}
		return date.getTime();
	}

	public static String getCurrentTime(String format)
	{
		Date date = new Date();
		sf = new SimpleDateFormat(format, Locale.getDefault());
		String currentTime = sf.format(date);
		return currentTime;
	}

	public static String getCurrentTime()
	{
		return getCurrentTime("yyyy-MM-dd HH:mm:ss");
	}
	
    /** 
     * 将时间戳转为代表"距现在多久之前"的字符串 
     * @param timeStr   时间戳 
     * @return 
     */  
    public static String getStandardDate(String timeStr) {  
      
        StringBuffer sb = new StringBuffer();  
      
        long t = Long.parseLong(timeStr);  
        long time = System.currentTimeMillis() - (t*1000);  
        long mill = (long) Math.ceil(time /1000);//秒前  
      
        long minute = (long) Math.ceil(time/60/1000.0f);// 分钟前  
      
        long hour = (long) Math.ceil(time/60/60/1000.0f);// 小时  
      
        long day = (long) Math.ceil(time/24/60/60/1000.0f);// 天前  
      
        if (day - 1 > 0) { 
        	//当时时间
        	sb.append(getDateToString(Long.parseLong(timeStr)));
        } else if (hour - 1 > 0) {  
            if (hour >= 24) {  
                sb.append("1天前");  
            } else {  
                sb.append(hour + "小时前");  
            }  
        } else if (minute - 1 > 0) {  
            if (minute == 60) {  
                sb.append("1小时前");  
            } else {  
                sb.append(minute + "分钟前");  
            }  
        } else if (mill - 1 > 0) {  
            if (mill == 60) {  
                sb.append("1分钟前");  
            } else {  
                sb.append(mill + "秒前");  
            }  
        } else {  
            sb.append("刚刚");  
        } 
        return sb.toString();  
    }  
}
