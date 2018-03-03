package trajectory.trajectorydistance;

import java.util.ArrayList;
import java.util.List;

import sciencecluster.MyPoint;
import trajectory.GeoTrajectory;
import trajectory.MyPointWithTime;

public class HausdorffDistance extends TrajectoryDistance
{

	public HausdorffDistance(GeoTrajectory pgta, GeoTrajectory pgtb)
	{
		super(pgta, pgtb);
	}

	@Override
	public double getDistance()
	{
		double a=getDistance(MyPointWithTime.getMyPointList(this.gta.points), MyPointWithTime.getMyPointList(this.gtb.points));
		double b=getDistance(MyPointWithTime.getMyPointList(this.gtb.points), MyPointWithTime.getMyPointList(this.gta.points));
	//	System.out.println(a+"\t"+b);
		return Math.max(a, b);
	}
	
	private static double getDistance(List<MyPoint> mpsa, List<MyPoint>mpsb){

		int la=mpsa.size();
		int lb=mpsb.size();
		
		double max=-1;
		for(int i=0; i<la; ++i){
			double min=Double.MAX_VALUE;
			for(int j=0; j<lb; ++j){
				MyPoint a=mpsa.get(i);
				MyPoint b=mpsb.get(j);
				double t=a.getDistance(b);
				if(t<min)
					min=t;
			}
			if(max<min)
				max=min;
		}
		return max;
	}

	public static void main(String[] args)
	{
		List<MyPoint> ps1=new ArrayList<MyPoint>();
		ps1.add(new MyPoint(2, 0));
		ps1.add(new MyPoint(3, 0));
		
		List<MyPoint> ps2=new ArrayList<MyPoint>();
		ps2.add(new MyPoint(1, 1));
		ps2.add(new MyPoint(2, 2));
		ps2.add(new MyPoint(3, 3));
		
		TrajectoryDistance td=new HausdorffDistance(GeoTrajectory.getTrajectoryWithPointsNoTime("", ps1), GeoTrajectory.getTrajectoryWithPointsNoTime("", ps2));
		System.out.println(td.getDistance());
	}

}
