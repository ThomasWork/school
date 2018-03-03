package trajectory.trajectorydistance;

import java.util.ArrayList;
import java.util.List;

import sciencecluster.MyPoint;
import trajectory.GeoTrajectory;
import myutil.NumberUtil;

public class DTWDistance extends TrajectoryDistance
{

	public DTWDistance(GeoTrajectory pgta, GeoTrajectory pgtb)
	{
		super(pgta, pgtb);
	}

	public DTWDistance(String inputPath)
	{
		super(inputPath);
	}

	@Override
	public double getDistance()
	{
		int la=this.gta.points.size();
		int lb=this.gtb.points.size();
		
		double max=999999999;
		
		double[][] temp=new double[la+1][lb+1];
		for(int i=0; i<=la; ++i)
			temp[i][0]=max;
		for(int i=0; i<=lb; ++i)
			temp[0][i]=max;
		temp[0][0]=0;
		
		for(int i=1; i<=la; ++i){
			for(int j=1; j<=lb; ++j){
				double curDis=this.getDis(i-1, j-1);//这里需要进行   减 1   的操作
				temp[i][j]=NumberUtil.min3(temp[i-1][j-1], temp[i-1][j], temp[i][j-1])+curDis;
			}
		}
	/*	for(int i=0; i<=la; ++i){
			for(int j=0; j<=lb; ++j){
				System.out.print(NumberUtil.df.format(temp[i][j])+"\t");
			}
			System.out.println();
		}*/
		return temp[la][lb];
	}	

	public static void main(String[] args)
	{
		TrajectoryDistance td=new DTWDistance("./src/trajectory/DTW.txt");
		System.out.println(td.getDistance());
	}

}
