package trajectory;

import java.util.ArrayList;
import java.util.List;

import myutil.DateUtil;
import myutil.fileprocess.FileUtil;
import sciencecluster.ClusterResult;
import sciencecluster.DistanceMatrix;
import sciencecluster.MyPoint;
import trajectory.trajectorydistance.TrajectoryDistance;

public class GeoTrajectory
{
	public String id="";
	public List<MyPointWithTime> points;
	
	public GeoTrajectory(String line){
		String[] ss=line.split(",");
		this.id=ss[0];
		this.points=new ArrayList<MyPointWithTime>();
		for(int i=1; i<ss.length; i+=2){
			double x=Double.parseDouble(ss[i]);
			double y=Double.parseDouble(ss[i+1]);
			MyPoint mp=new MyPoint(x, y);
			mp.userId=this.id;
			this.points.add(new MyPointWithTime(mp));
		}
	}
	
	public GeoTrajectory(String idPar, List<MyPointWithTime> mpts){
		this.id=idPar;
		this.points=mpts;
	}
	
	public static GeoTrajectory getTrajectoryWithPointsNoTime(String idPar, List<MyPoint> mps){
		List<MyPointWithTime> mpts=new ArrayList<MyPointWithTime>();
		for(MyPoint mp: mps){
			mpts.add(new MyPointWithTime(mp));
		}
		return new GeoTrajectory(idPar, mpts);
	}
	
	public GeoTrajectory getTrajectoryPointsInClusterResults (List<ClusterResult> results){
		List<MyPointWithTime> selected=new ArrayList<MyPointWithTime>();
		
		for(MyPointWithTime mpt: this.points){
			for(ClusterResult cr: results){
				if(cr.isPointInCluster(mpt.point))
					selected.add(mpt);
			}
		}
		return new GeoTrajectory(this.id, selected);
	}
	
	public void replacePointsUserIdWithTraId(){
		for(MyPointWithTime mp: this.points){
			mp.point.userId=this.id;
		}
	}
	
	@Override
	public String toString(){
		StringBuilder sb=new StringBuilder(this.id+"：\n");
		for(int i=0; i<this.points.size(); ++i){
			MyPointWithTime mp=this.points.get(i);
			sb.append(","+mp.point.userId+","+mp.point.x+","+mp.point.y);
		}
		return sb.toString();
	}
	
	public static List<GeoTrajectory> getSingleToursWithDay(List<GeoTrajectory> tras, double dayDis){
		double hourDis=dayDis*24;//转换为小时
		List<GeoTrajectory> selected=new ArrayList<GeoTrajectory>();
		for(int i=0; i<tras.size(); ++i){
			if(judgeSingleTour(tras, i, hourDis)){
				selected.add(tras.get(i));
			}
		}
		return selected;
	}
	
	public static List<GeoTrajectory> getNotSingleToursWithDay(List<GeoTrajectory> tras, double dayDis){
		double hourDis=dayDis*24;//转换为小时
		List<GeoTrajectory> selected=new ArrayList<GeoTrajectory>();
		for(int i=0; i<tras.size(); ++i){
			if(!judgeSingleTour(tras, i, hourDis)){
				selected.add(tras.get(i));
			}
		}
		return selected;
	}
	
	public static boolean judgeSingleTour(List<GeoTrajectory> tras, int index, double hourDis){
		if(index>0){
			double dis=getTraTimeHourDis(tras.get(index-1), tras.get(index));
			if(dis<hourDis)//如果距离太小返回错误，否则接着进行判断
				return false;
		}
		if(index<tras.size()-1){//表示后面还有
			double dis=getTraTimeHourDis(tras.get(index), tras.get(index+1));
			if(dis<hourDis)
				return false;
		}
		return true;
	}
	
	//看看后一个时间与前一个时间差了多少
	public static double getTraTimeHourDis(GeoTrajectory gta, GeoTrajectory gtb){
		List<MyPointWithTime> mts=gta.points;
		MyPointWithTime mpa=mts.get(mts.size()-1);//最后一个
		MyPointWithTime mpb=gtb.points.get(0);
		return DateUtil.getDateDisHour(mpb.pointDate, mpa.pointDate);
	}
	
	//将轨迹写到文件中
	public static void writeTrajectoryToFile(List<GeoTrajectory> tras, String file){
		List<String> lines=new ArrayList<String>();
		for(GeoTrajectory tra: tras){
			lines.add(tra.toString());
		}
		FileUtil.NewFile(file, lines);
	}
	
	//从文件中加载轨迹
	public static List<GeoTrajectory> loadTrajectoryFromFile(String file){
		List<GeoTrajectory> tras=new ArrayList<GeoTrajectory>();
		List<String> lines=FileUtil.getLinesFromFile(file);
		for(String line: lines)
			tras.add(new GeoTrajectory(line));
		return tras;
	}
	
	//获得轨迹距离并保存，在调用此函数时需要先设置距离计算方法
	public static void writeTrajectoryDistance(List<GeoTrajectory> tras){
		int total=tras.size();
		double[][] temp=new double[total][total];
		for(int i=0; i<total; ++i){
			System.out.println(i);
			for(int j=i; j<total; ++j){
				double t=TrajectoryDistance.getDistance(tras.get(i), tras.get(j));
				temp[i][j]=t;
				temp[j][i]=t;
			}
		}
		DistanceMatrix dm=new DistanceMatrix(temp);
		dm.saveToFile(TrajectoryDistance.getDistanceSavePath());
	}
	
	//对轨迹进行过滤
	public static List<GeoTrajectory> filterTrajectory(List<GeoTrajectory> tras){
		List<GeoTrajectory> selected=new ArrayList<GeoTrajectory>();
		for(GeoTrajectory gt: tras){
			if(gt.points.size()>2)
				selected.add(gt);
		}
		return selected;
	}
	
	//计算轨迹列表中总共有多少个点
	public static int countTrasPointsNum(List<GeoTrajectory> tras){
		int sum=0;
		for(GeoTrajectory gt: tras){
			sum+=gt.points.size();
		}
		return sum;
	}
	
	public static void main(String[] args){
	//	test1();
	//	getKeyTrajectory(GeoFilter.areaBeijing);
	//	testGetTrajectorysFromPhotosList();//对函数进行测试
	}
}
