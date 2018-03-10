package myutil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil
{
	public static String sdfString = "yyyy-MM-dd HH:mm:ss";
	public static SimpleDateFormat sdfDHM=new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static SimpleDateFormat sdfDay=new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat sdfLeft=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public static SimpleDateFormat sdfHMS=new SimpleDateFormat("HH:mm:ss");
	public static SimpleDateFormat sdfMD=new SimpleDateFormat("MM-dd");
	public static Calendar calendar=Calendar.getInstance();
	
	public static enum DateField
	{
		year,
		month,
		dayOfMonth,
		dayOfWeek,
		dayOfYear,
		hour,
		minute,
		year_month,
		year_month_day,
		month_day_hour,
		day_hour_minute
	}
	
	public static Date getDate(String str, SimpleDateFormat format){
		Date d = null;
		try
		{
			d = format.parse(str);
		} catch (ParseException e)
		{
			System.out.println("转换时间出错: " + str);
			System.out.println(e);
			System.exit(0);
		}
		return d;
	}
	
	public static void show(Date date){
		SimpleDateFormat sdf=new SimpleDateFormat(sdfString);
		System.out.println(sdf.format(date));
	}
	
	public static String getDateString(Date date)
	{
		if(null==date)
			return "0";
		SimpleDateFormat sdf=new SimpleDateFormat(sdfString);
		return sdf.format(date);
	}
	
	public static String getNowString() {
		SimpleDateFormat sdf = new SimpleDateFormat(sdfString);
		return sdf.format(new Date());
	}
	
	public static Date getDate(String dateStr)
	{
		Date date=null;
		try
		{
			SimpleDateFormat sdf=new SimpleDateFormat(sdfString);
			date = sdf.parse(dateStr);
		} 
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return date;
	}

	public static Date getDate(int seconds)
	{
    //    DateFormat dateformat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        long millions=new Long(seconds)*1000;
        Date d=new Date(millions);
      //  String temp=dateformat.format(d);
    //    System.out.println(temp.substring(0, temp.indexOf(":")));
        return d;
	}
	
	public static int getDateField(Date date, DateField field)
	{
		if(null==date)
			return -1;
		DateUtil.calendar.setTime(date);
		switch(field)
		{
		case year:
			return DateUtil.calendar.get(Calendar.YEAR);
		case month:
			return DateUtil.calendar.get(Calendar.MONTH)+1;
		case dayOfMonth:
			return DateUtil.calendar.get(Calendar.DAY_OF_MONTH);
		case dayOfWeek:
			return DateUtil.calendar.get(Calendar.DAY_OF_WEEK);
		case hour:
			return DateUtil.calendar.get(Calendar.HOUR_OF_DAY);
		case minute:
			return DateUtil.calendar.get(Calendar.MINUTE);
		case year_month:
			return DateUtil.calendar.get(Calendar.YEAR)*100+(DateUtil.calendar.get(Calendar.MONTH)+1);
		case year_month_day:
			return DateUtil.calendar.get(Calendar.YEAR)*10000+(DateUtil.calendar.get(Calendar.MONTH)+1)*100+DateUtil.calendar.get(Calendar.DAY_OF_MONTH);
		case month_day_hour:
			return DateUtil.calendar.get(Calendar.MONTH)*10000+DateUtil.calendar.get(Calendar.DAY_OF_MONTH)*100+DateUtil.calendar.get(Calendar.HOUR_OF_DAY);
		case day_hour_minute:
			return DateUtil.calendar.get(Calendar.DAY_OF_MONTH)*10000+DateUtil.calendar.get(Calendar.HOUR_OF_DAY)*100+DateUtil.calendar.get(Calendar.MINUTE);
			default:
				return -1;
		}
	}
	
	public static Date dateOffset(Date date, DateField field, int offset){
		DateUtil.calendar.setTime(date);
		switch(field){
		case year:
			DateUtil.calendar.add(Calendar.YEAR, offset);
			break;
		case month:
			DateUtil.calendar.add(Calendar.MONTH, offset);
			break;
		case dayOfYear:
			DateUtil.calendar.add(Calendar.DAY_OF_YEAR, offset);
			break;
			default:
			{
				System.out.println("类型错误");
			}
		}
		return DateUtil.calendar.getTime();
	}
	
	public static double getDateDisDay(Date d1, Date d2){
		long diff = d1.getTime() - d2.getTime();
		double days = diff*1.0 / (1000 * 60 * 60 * 24);
		return days;
	}
	
	public static double getDateDisHour(Date d1, Date d2){
		long diff = d1.getTime() - d2.getTime();
		double hours = diff*1.0 / (1000 * 60 * 60 );
		return hours;
	}
	
	public static double getDateDisSecond(Date d1, Date d2){
		long diff=d1.getTime()-d2.getTime();
		double seconds=diff/1000;
		return seconds;
	}
	
	public static String getTodayMonthDay(){
		Date today=new Date();
		return DateUtil.sdfMD.format(today);
	}
	
	public static Date dateUpdate(Date date, DateField field, int dis){
	    DateUtil.calendar.setTime(date);
	    switch(field)
		{
		case year:
			DateUtil.calendar.add(Calendar.YEAR, dis);
			break;
		case month:
			DateUtil.calendar.add(Calendar.MONTH, dis);
			break;
		case hour:
			DateUtil.calendar.add(Calendar.HOUR, dis);
			break;
		case dayOfYear:
			DateUtil.calendar.add(Calendar.DAY_OF_YEAR, dis);
			break;
			default:
				System.out.println("时间 域出错，程序退出" + field);
				System.exit(0);
		}
	    return DateUtil.calendar.getTime();
	}
	public static void dateUpdateTest(){
		SimpleDateFormat sdf=new SimpleDateFormat(sdfString);
		Date d=DateUtil.getDate("2011-10-31 20:45:30", sdf);
		d=DateUtil.dateUpdate(d, DateUtil.DateField.year, -1);
		System.out.println(sdf.format(d));
		d=DateUtil.dateUpdate(d, DateUtil.DateField.month, 3);
		System.out.println(sdf.format(d));
		d=DateUtil.dateUpdate(d, DateUtil.DateField.hour, 4);
		System.out.println(sdf.format(d));
	}
	
	public static void main(String[] args){
		dateUpdateTest();
	}
}
