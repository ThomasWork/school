package trajectory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import entity.Photo;
import sciencecluster.MyPoint;

public class MyPointWithTime
{
	public MyPoint point;
	public Date pointDate;
	
	public MyPointWithTime(MyPoint pointPar){
		this.point=pointPar;
		this.pointDate=null;
	}
	
	public MyPointWithTime(Photo p){
		this.point=Photo.getPoint(p);
		this.pointDate=p.dateTaken;
	}
	
	public MyPointWithTime(Date datePar, MyPoint pointPar){
		this.point=pointPar;
		this.pointDate=datePar;
	}
	
	public static List<MyPoint> getMyPointList(List<MyPointWithTime> mpts){
		List<MyPoint> mps=new ArrayList<MyPoint>();
		for(MyPointWithTime mpt: mpts){
			mps.add(mpt.point);
		}
		return mps;
	}
}
