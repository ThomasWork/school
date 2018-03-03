package trajectory;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import myutil.DateUtil;
import entity.Photo;


public class TimeTrajectory
{
	public Date startDate;//开始产生轨迹的时间
	List<Date> dates;
	List<Double> speed;
	double averageSpeed;
	
	public TimeTrajectory(List<Date> datesPar){
		this.dates=datesPar;
		this.setParameters();
	}
	
	public TimeTrajectory(GeoTrajectory gtra){
		this.dates=new ArrayList<Date>();
		for(MyPointWithTime mpt: gtra.points){
			dates.add(mpt.pointDate);
		}
		this.setParameters();
	}
	
	private void setParameters(){
		Collections.sort(this.dates);//对时间进行排序
		this.setHourDis();
	}
	
	//获得时间间隔，以小时为单位 
	public void setHourDis(){
		this.speed=new ArrayList<Double>();
		double sum=0;
		for(int i=1; i<this.dates.size(); ++i){
			double temp=DateUtil.getDateDisHour(this.dates.get(i), this.dates.get(i-1));
			sum+=temp;
			this.speed.add(temp);
		}
		this.averageSpeed=sum/this.speed.size();
	}
	
	public void showSpeed(){
		System.out.println("Speed num:"+this.speed.size());
		for(int i=0; i<this.speed.size(); ++i){
			System.out.println(this.speed.get(i));
		}
		System.out.println("平均速度："+this.averageSpeed);
	}
	
	public static void test1() throws ParseException{
		List<Date> times=new ArrayList<Date>();
		times.add(DateUtil.sdf.parse("2014-10-30 12:02:03"));
		times.add(DateUtil.sdf.parse("2014-10-29 12:02:03"));
		times.add(DateUtil.sdf.parse("2014-10-27 12:02:03"));
		TimeTrajectory tra=new TimeTrajectory(times);
		tra.showSpeed();
	}

	public static void main(String[] args) throws ParseException
	{
		test1();
	}

}
