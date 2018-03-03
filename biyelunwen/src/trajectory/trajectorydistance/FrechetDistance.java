package trajectory.trajectorydistance;

import java.util.ArrayList;
import java.util.List;

import myutil.NumberUtil;
import sciencecluster.MyPoint;
import trajectory.GeoTrajectory;

public class FrechetDistance extends TrajectoryDistance
{
	public FrechetDistance(GeoTrajectory pgta, GeoTrajectory pgtb)
	{
		super(pgta, pgtb);
	}

	@Override
	public double getDistance()
	{
		int la=this.gta.points.size();
		int lb=this.gtb.points.size();
		
		double[][] temp=new double[la][lb];
		for(int i=0; i<la; ++i)
			for(int j=0; j<lb; ++j)
				temp[i][j]=-1;
		
		return Cal(temp, la-1, lb-1);
	}
	
	private double Cal(double[][] ca, int i, int j){

	//	System.out.println(i+","+j);
		if(ca[i][j] > -1.0)
			return ca[i][j];
		if(i == 0 && j == 0)
			ca[i][j] =  this.getDis(0,0);
		else if(i > 0 && j == 0)
		{
			ca[i][j] = Math.max(Cal(ca,i-1,0), this.getDis(i,0));
		}
		else if(i == 0 && j > 0)
		{
			ca[i][j] = Math.max(Cal(ca,0,j-1), this.getDis(0,j));
		}
		else if(i > 0 && j > 0)
		{
			ca[i][j] = Math.max(NumberUtil.min3( Cal(ca,i-1,j), Cal(ca,i-1,j-1), Cal(ca,i,j-1) ),  this.getDis(i,j));
		}
		else
		{
			ca[i][j] = Double.MAX_VALUE;
		}

		return ca[i][j];
	}

	public static void main(String[] args)
	{
		List<MyPoint> ps1=new ArrayList<MyPoint>();
		ps1.add(new MyPoint(1, 2));
		ps1.add(new MyPoint(2, 3));
		ps1.add(new MyPoint(3, 4));
		ps1.add(new MyPoint(4, 5));
		ps1.add(new MyPoint(5, 6));
		
		List<MyPoint> ps2=new ArrayList<MyPoint>();
		ps2.add(new MyPoint(1, -2));
		ps2.add(new MyPoint(2, -3));
		ps2.add(new MyPoint(3, -4));
		ps2.add(new MyPoint(4, -5));
		ps2.add(new MyPoint(5, -6));
		ps2.add(new MyPoint(6, -7));
		ps2.add(new MyPoint(7, -8));
		
		TrajectoryDistance td=new FrechetDistance(GeoTrajectory.getTrajectoryWithPointsNoTime("", ps1), GeoTrajectory.getTrajectoryWithPointsNoTime("", ps2));
		System.out.println(td.getDistance());
	}
}
