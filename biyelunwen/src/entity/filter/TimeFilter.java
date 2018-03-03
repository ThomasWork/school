package entity.filter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import myutil.DateUtil;
import entity.Photo;

public class TimeFilter extends PhotoFilter
{
	public static TimeType allDay=new HourFilter(0, 23);
	public TimeType realFilter;

	public TimeFilter(TimeType tt){
		this.realFilter=tt;
	}
	
	@Override
	protected boolean isValid(Photo p)
	{
		return this.realFilter.isValid(p);
	}
	
	public static List<Photo> filterPhoto(List<Photo> input, TimeFilter tf){
		List<Photo> output=new ArrayList<Photo>();
		for(Photo p: input){
			if(tf.getValid(p))
				output.add(p);
		}
		return output;
	}
	
	public static interface TimeType{
		public boolean isValid(Photo p);
	}
	
	public static class YearFilter implements TimeType{
		int year;		
		public YearFilter(int yearPar){
			this.year=yearPar;
		}
		
		@Override
		public boolean isValid(Photo p)
		{
			Date dt=p.dateTaken;
			int py=DateUtil.getDateField(dt, DateUtil.DateField.year);
			if(py==this.year){
			//	System.out.println(dt);
				return true;
			}
			return false;
		}
	}
	
	public static class MonthFilter implements TimeType{
		int month;		
		public MonthFilter(int monthPar){
			this.month=monthPar;
		}
		
		@Override
		public boolean isValid(Photo p)
		{
			Date dt=p.dateTaken;
			int mon=DateUtil.getDateField(dt, DateUtil.DateField.month);
			if(mon==this.month){
			//	System.out.println(dt);
				return true;
			}
			return false;
		}		
	}
	
	public static class HourFilter implements TimeType{

		public int startHour;
		public int endHour;
		
		public HourFilter(int startPar, int endPar){
	//		System.out.println("小时范围："+startPar+"---"+endPar);
			this.startHour=startPar;
			this.endHour=endPar;
		}
		
		@Override
		public boolean isValid(Photo p)
		{
			Date dt=p.dateTaken;
			int hour=DateUtil.getDateField(dt, DateUtil.DateField.hour);
			if(this.startHour<=this.endHour){
		//		System.out.println(hour+","+this.startHour+","+this.endHour);
				if(hour>=this.startHour && hour<=this.endHour){//如果没有跨天
			//		System.out.println("没有跨天");
					return true;
				}
			}else{
				if(hour>=this.startHour || hour<=this.endHour)//>=22，<=3
					return true;
			}
			return false;
		}
		
	}
}
