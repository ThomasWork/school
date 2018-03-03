package trajectory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import myutil.DateUtil;
import myutil.fileprocess.FileUtil;
import sciencecluster.ClusterResult;
import sciencecluster.DistanceMatrix;
import sciencecluster.GeoBlock;
import sciencecluster.MyPoint;
import sciencecluster.ScienceCluster;
import sciencecluster.MyPoint.MyPointCoordinateDistanceWithMax;
import trajectory.trajectorydistance.TrajectoryDistance;
import trajectory.trajectorydistance.TrajectoryDistance.ALG;
import draw.KmlFile;
import entity.Photo;
import entity.User;
import entity.filter.GeoFilter;
import entity.filter.UserFilter;

public class GeoTrajectoryCluster
{
	//对轨迹中的所有点进行聚类
	public static ScienceCluster getScienceClusterResult(GeoFilter.Area area, List<GeoTrajectory> tras){
		List<MyPoint> mps=new ArrayList<MyPoint>();
		for(GeoTrajectory tra: tras){
			mps.addAll(MyPointWithTime.getMyPointList(tra.points));
		}
		return scienceCluster(area, mps);
	}
	
	//对轨迹中的所有点进行聚类
	public static ScienceCluster getUserScienceClusterResult(GeoFilter.Area area, List<User> users){
		List<MyPoint> mps=new ArrayList<MyPoint>();
		for(User u: users){
			mps.addAll(Photo.getPoints(u.photosList));
		}
		return scienceCluster(area, mps);
	}
	
	private static ScienceCluster scienceCluster(GeoFilter.Area area, List<MyPoint> mps){
		int row=area.row, column=area.column;
		List<GeoBlock> blocks=GeoBlock.getBlock(mps, row, column);//这里已经设置数据点对应的栅格下标
		List<MyPoint> clusterPoints=GeoBlock.getMyPoints(blocks);//待聚类的数据点
		
		double clusterR=area.clusterR;//用来设置局部密度的截断距离
		int clusterNum=area.clusterNum;//聚类的数量
		double rate=0.5;//平均密度比率
		
		area.showClusterParameter();
		
		MyPoint.mpw=new MyPoint.MyPointSelfWeight();//设置开启数据点的权重
		MyPoint.mpd=new MyPointCoordinateDistanceWithMax(clusterR*20);//使用带有最大值的坐标距离
		
		ScienceCluster sc=new ScienceCluster(clusterPoints);
		sc.initCluster(clusterR);
	//	sc.showLocalDensity();
		sc.cluster(clusterNum, clusterR, rate);
		List<MyPoint> used=sc.getUsedPoints();
		System.out.println("聚类后还剩下："+used.size());
		KmlFile.writeClusterResult("对轨迹中的点进行聚类_"+clusterR+"_"+clusterNum, used);
		return sc;
	}
	
	//根据聚类后的结果在聚类结果中的点保存下来
	public static List<GeoTrajectory> getKeyTrajectory(List<GeoTrajectory> tras, GeoFilter.Area area){
		System.out.println("开始选择出轨迹数目："+tras.size());
		ScienceCluster sc=GeoTrajectoryCluster.getScienceClusterResult(area, tras);
		List<ClusterResult> crs=ClusterResult.getClusters(sc.clusters);
		
		List<GeoTrajectory> selected=new ArrayList<GeoTrajectory>();
		for(GeoTrajectory tra: tras){
			GeoTrajectory temp=tra.getTrajectoryPointsInClusterResults(crs);
			if(temp.points.size()>1)
				selected.add(temp);
		}
		System.out.println("找到在聚类中的轨迹数目："+selected.size());
		KmlFile.writeTrajectories(selected, "在聚类中的点构成的轨迹");
		return selected;
	}
	

	
	/****************************************过时***************************************/
	//使用DTW等距离对轨迹进行聚类
	public static ScienceCluster scienceCluster(){
		TrajectoryDistance.currentAlg=ALG.Frechet;
	//	TrajectoryDistance.currentAlg=ALG.DTW;
	//	TrajectoryDistance.currentAlg=ALG.Hausdorff;
		String path=TrajectoryDistance.getDistanceSavePath();
	//	System.out.println(path);
		DistanceMatrix dm=new DistanceMatrix(path);
		MyPoint.mpd=new MyPoint.MyPointKnownDistance(dm);
		List<MyPoint> mps=new ArrayList<MyPoint>();
		for(int i=0; i<dm.rowNum; ++i){
			MyPoint mp=new MyPoint();
			mp.pointId=i;
			mps.add(mp);
		}
		ScienceCluster sc=new ScienceCluster(mps);
		List<Double> dis=sc.getAllDistance();
	//	NumberUtil.countFrequency(dis);
		double cutoff=sc.getCutoffDistance(0.02);
		sc.initCluster(cutoff);
		sc.setClusterCenterWithSortN(3);//确定聚类中心
		sc.decideCluster(cutoff, 0.6);//决定聚类
		sc.showClusterResult();
	//	sc.showLocalDensity();
	//	sc.showDistance();
	//	sc.initCluster(2);
		return sc;
	}
	
	public static List<KeyTrajectory> clusterWaiDiTrajectories(int n){
		GeoFilter.Area area=GeoFilter.areaBeijing;
		List<User> users=User.getUsersWithPhotos();
		List<GeoTrajectory> tours=UserGeoTrajectory.getTourTrajectory(users, 72, 180);
		ScienceCluster sc=GeoTrajectoryCluster.getScienceClusterResult(area, tours);
		List<ClusterResult> results=ClusterResult.getClusters(sc.clusters);
		ClusterResult.setClusterResultsLabel(results, ClusterResult.places);
		results=ClusterResult.mergetClusterResultWithLabel(results);
		List<KeyTrajectory> keys=KeyTrajectory.getTrajectorys(results, tours, n);
		return keys;
	}
	
	
	public static void main(String[] args){
		//clusterUsersTrajectories();
		//clusterWaiDiTrajectories();
	}
}
