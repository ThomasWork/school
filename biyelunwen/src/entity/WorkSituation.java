package entity;

import trajectory.KeyTrajectory;

public class WorkSituation
{
	public static String pythonDrawDir="C:/Users/Admin/Desktop/图片地理位置/画图/";
	
	
	public static void main(String[] args){
		//统计每个用户的滞留时间
		UserStatistic.countEachUserStay();
		
		//测试分割时间对不同轨迹用户数量，以及总的轨迹数量的影响
		//UserStatistic.testSplitTimeEffect();
		
		//查看12个月的聚类结果
	//	PhotoStatistic.beijingMonthly();
		
		//看看所有轨迹的在地图上画出来的结果
	//	UserGeoTrajectory.testTourTrajectory();
	//	UserGeoTrajectory.testTourTrajectory();
		
		
		//统计关键路径有多少节点
	//	KeyTrajectory.countKeyTrajectory();		
		//获得节点间的转移频率
	//	KeyTrajectory.getKeyMarkovModel();		
		//对关键路径进行聚类
	//	KeyTrajectory.clusterKeyTrajectories();
		//统计出现频率最高的轨迹
	//	KeyTrajectory.countTopNTrajectories();
		
		
	}
}
