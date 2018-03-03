package trajectory.trajectorydistance;

import java.util.ArrayList;
import java.util.List;

import entity.Photo;
import myutil.fileprocess.FileUtil;
import sciencecluster.MyPoint;
import trajectory.GeoTrajectory;


public abstract class TrajectoryDistance
{
	public GeoTrajectory gta;
	public GeoTrajectory gtb;
	public double[][] distance=null;

	public enum ALG{
		DTW, Hausdorff,Frechet
	}
	public static ALG currentAlg=ALG.DTW;
	
	
	public TrajectoryDistance(GeoTrajectory pgta, GeoTrajectory pgtb){
		this.gta=pgta;
		this.gtb=pgtb;
	}
	
	public TrajectoryDistance(String path){
		List<MyPoint> mps1=new ArrayList<MyPoint>();
		
		List<String> lines=FileUtil.getLinesFromFile(path);
		int current=0;
		int n1=Integer.parseInt(lines.get(current));
		for(int i=1; i<=n1; ++i){
			String t=lines.get(current+i);
			String[] ss=t.split(",");
			mps1.add(new MyPoint(Integer.parseInt(ss[0]), Integer.parseInt(ss[1])));
		}
		this.gta=GeoTrajectory.getTrajectoryWithPointsNoTime("", mps1);
		
		List<MyPoint> mps2=new ArrayList<MyPoint>();
		current=n1+1;
		int n2=Integer.parseInt(lines.get(current));
		for(int i=1; i<=n2; ++i){
			String t=lines.get(current+i);
			String[] ss=t.split(",");
			mps2.add(new MyPoint(Integer.parseInt(ss[0]), Integer.parseInt(ss[1])));
		}
		this.gtb=GeoTrajectory.getTrajectoryWithPointsNoTime("", mps2);
	//	this.showPar();
	}
	
	public abstract double getDistance();
	
	private double getDisFromPoint(int i, int j){
		MyPoint mp1=this.gta.points.get(i).point;
		MyPoint mp2=this.gtb.points.get(j).point;
		return mp1.getDistance(mp2);
	}
	
	protected double getDis(int i, int j){
		return this.getDisFromPoint(i, j);
	}
	
	private void showPar(){
		System.out.println(this.gta+"\n"+this.gtb);
	}
	
	public static double getDistance(GeoTrajectory gta, GeoTrajectory gtb){
		TrajectoryDistance td=null;
		switch(TrajectoryDistance.currentAlg){
		case DTW:
			td=new DTWDistance(gta, gtb);
			break;
		case Hausdorff:
			td=new HausdorffDistance(gta, gtb);
			break;
		case Frechet:
			td=new FrechetDistance(gta, gtb);
			break;
		}
		return td.getDistance();
	}
	
	public static String getCurrentAlgName(){
		String name="";
		switch(TrajectoryDistance.currentAlg){
		case DTW:
			name="dtw";
			break;
		case Hausdorff:
			name="hausdorff";
			break;
		case Frechet:
			name="frechet";
			break;
		}
		return name;
	}
	
	//获得轨迹距离矩阵的保存位置
	public static String getDistanceSavePath(){
		return Photo.clusterDir+TrajectoryDistance.getCurrentAlgName()+".txt";
	}
}
