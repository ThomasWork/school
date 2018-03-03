package life.riskprediction.timecorrection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import life.riskprediction.BankDetail;
import life.riskprediction.BillDetail;
import life.riskprediction.BrowseHistory;
import life.riskprediction.MainConfigure;
import myutil.DateUtil;
import myutil.NumberUtil;
import myutil.fileprocess.FileProcessLine;

public class TimeCorrection
{ 
	private static long hourMsec=60*60*1000;
	private static long dayMsec=hourMsec*24;
	
	public static long hourSec=60*60;
	public static long daySec=24*hourSec;
	public static long monthSecBigger=35*daySec;

	//对时间进行测试
	public static void testTime(){
		long big = 5952662481000L;//已知的最大时间
		Date tempD=DateUtil.getDate("2016-12-31 00:00:00", DateUtil.sdf);//2017-01-10 19:13:39
		System.out.println("标准时间："+DateUtil.sdf.format(tempD));
		long tempL=tempD.getTime();//获取当前时间
		long dayDis=big-tempL+TimeCorrection.dayMsec*90;//日期上的差距要用上  加号
		long hourDis=6*TimeCorrection.hourMsec;//小时时间差6个小时
		long totalDis=dayDis;//-hourDis;//减去的话，小时会向后移动
		Date correct=new Date(big-totalDis);//这里应该保证纠正后的时间比
		System.out.println("纠正后的时间："+DateUtil.sdf.format(correct));
		System.out.println("时间差为："+totalDis);
	}
	
	//查看银行流水记录时间上的特点，如果是工资的话，应该在30天左右有一个波动
	public static void bankDetailTimeFeature(){
		List<BankDetail> details=BankDetail.getBankDetails();
		Map<Integer, List<Long>> map=new TreeMap<Integer, List<Long>>();
		for(BankDetail detail: details){
			if(detail.isSalary==0 ||detail.time==0)//如果不是工资，则继续
				continue;
			int id=detail.userId;
			List<Long> time=map.get(id);
			if(null==time){
				time=new ArrayList<Long>();
				map.put(id, time);
			}
			time.add(detail.time);
		}
		List<Long> timeDis=new ArrayList<Long>();
		for(Entry<Integer, List<Long>> entry: map.entrySet()){
			List<Long> time=entry.getValue();
			if(time.size()<=1)
				continue;
			Collections.sort(time);
			
			for(int i=1; i<time.size(); ++i){//这里从1开始
				timeDis.add(time.get(i)-time.get(i-1));
			}
		}
		Long[] thres=new Long[100];
		double daySeconds=60*60*24;
		for(int i=0; i<thres.length; ++i){
			thres[i]=(long) (i*daySeconds);
		}
		int[] count=NumberUtil.countFrequency(timeDis, thres);
	}
	
	//查看信用卡账单记录时间上的特点，是不是以24小时为周期
	public static void billDetailTimeFeature(){
		long minT= 5746916127L; 
		long hour=60*60;
		List<BillDetail> details=BillDetail.getBillDetails();
		List<Long> times=new ArrayList<Long>();
		int loop=72;
		for(BillDetail detail: details){
			if(0==detail.time)
				continue;
			long totalHour=(detail.time-minT)/hour;
			times.add(totalHour%loop);//获得时间，以小时计算
		}
		Long[] thres=new Long[loop];
		for(int i=0; i<thres.length; ++i){
			thres[i]=(long) (i);
		}
		int[] count=NumberUtil.countFrequency(times, thres);
	}
	
	//查看浏览历史的时间特征，浏览历史应该有每天重复的特点
	public static void browseHistoryTimeFeature(){
		long minT= 5805329238L;
		long hour=60*60;
		List<BrowseHistory> historys=BrowseHistory.getBrowseHistorys();
		List<Long> times=new ArrayList<Long>();
		int loop=72;
		for(BrowseHistory history: historys){
			if(0==history.time)
				continue;
			long totalHour=(history.time-minT)/hour;
			times.add(totalHour%loop);//获得时间，以小时计算
		}
		Long[] thres=new Long[loop];
		for(int i=0; i<thres.length; ++i){
			thres[i]=(long) (i);
		}
		int[] count=NumberUtil.countFrequency(times, thres);
	}
	
	public static void testCorrectTimeWithBrowseHistory(){
		final long Dist=4477324881000L-TimeCorrection.hourMsec*12;//时间整体向右移动，使用减号，前面的时间是使用日期差得到的
		FileProcessLine.processFile(MainConfigure.browseHistoryPath, new FileProcessLine(){
			List<Integer> times=new ArrayList<Integer>();
			@Override
			public void parseLine(String line)
			{
				long time=Long.parseLong(line.split(",")[1]);
				if(0==time)
					return;
				long correct=(time*1000-Dist);//获得正确的时间
				Date date=new Date(correct);
				times.add(DateUtil.getDateField(date, DateUtil.DateField.hour));//获得时间，以小时计算
			}		
			@Override
			public void processList(){
				Integer[] thres=new Integer[24];
				for(int i=0; i<thres.length; ++i){
					thres[i]= i;
				}
				int[] counts=NumberUtil.countFrequency(times, thres);
			}
		});
	}

	public static void main(String[] args)
	{
		testTime();
	}
}
