package trajectory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import myutil.MySort;
import myutil.NumberUtil;
import myutil.StringUtil;
import myutil.fileprocess.FileUtil;
import algorithm.LCSS;
import algorithm.markov.Markov;
import algorithm.markov.StatusChain;
import draw.KmlFile;
import entity.Photo;
import entity.filter.GeoFilter;
import sciencecluster.ClusterResult;
import sciencecluster.DistanceMatrix;
import sciencecluster.GeoBlock;
import sciencecluster.MyPoint;
import sciencecluster.MyPoint.MyPointCoordinateDistanceWithMax;
import sciencecluster.ScienceCluster;

public class KeyTrajectory
{
	public String id;
	public List<Integer> keyPath;
	public List<String> keyPathName;
	public Set<Integer> visited;
	
	public static Map<Integer, String> pathNameMap=null;
	
	public KeyTrajectory(List<ClusterResult> results, GeoTrajectory tra){
		
		if(null==pathNameMap){
			pathNameMap=new TreeMap<Integer, String>();
			for(ClusterResult cr: results){
				pathNameMap.put(cr.id, cr.label);
			}
		}
		
		this.id=tra.id;
		this.visited=new HashSet<Integer>();
		this.keyPath=new ArrayList<Integer>();
		this.keyPathName=new ArrayList<String>();
		List<MyPoint> mps=MyPointWithTime.getMyPointList(tra.points);
		for(MyPoint mp: mps){
			for(ClusterResult cr: results){
				if(cr.isPointInCluster(mp)){
					int tid=cr.id;
					if(!this.visited.contains(tid)){
						this.visited.add(tid);
						this.keyPath.add(tid);
						this.keyPathName.add(cr.label);
					}
				}
					
			}
		}
	}
	
	public static double getDistance(KeyTrajectory kta, KeyTrajectory ktb){
		return LCSS.getDistance(kta.keyPath, ktb.keyPath);
	//	return 0;//StatusChain.getIndexArrayDistance(kta.keyPath, ktb.keyPath);
	}
	
	public static List<List<Integer>> getSplitTras(int n, List<Integer> path){
	//	System.out.println(n+"\t"+path);
		List<List<Integer>> newTra=new ArrayList<List<Integer>>();
		if(path.size()<n)
			return newTra;
		if(1==n){
			for(Integer i: path){
				List<Integer> nt=new ArrayList<Integer>();
				nt.add(i);
				newTra.add(nt);
			}
			return newTra;
		}
		List<Integer> child=new ArrayList<Integer>();
		for(int i=0; i<path.size()-1; ++i)
			child.add(path.get(i));
		List<List<Integer>> temps=getSplitTras(n-1, child);
		for(List<Integer> temp: temps){
			Integer last=path.get(path.size()-1);
			temp.add(last);
		}
		newTra.addAll(temps);//包含最后一个节点的
		newTra.addAll(getSplitTras(n, path.subList(0, path.size()-1)));//不包含最后一个节点的
		return newTra;
	}
	
	public static void testGetSplitTras(){
		List<Integer> temp=new ArrayList<>();
		for(int i=1; i<=5; ++i)
			temp.add(i);
		for(int i=1; i<temp.size(); ++i){
			System.out.println("Cur:"+i);
			List<List<Integer>> lists=getSplitTras(i, temp);
			for(List<Integer> list: lists){
				System.out.println(list);
			}
		}
	}
	
	@Override
	public String toString(){
		StringBuilder sb=new StringBuilder(this.id+":\t");
		for(Integer t: this.keyPath){
			sb.append(t+",");
		}
		sb.append("\n");
		for(String s: this.keyPathName)
			sb.append(s+",");
		return sb.toString();
	}
	
	//这里可以设置轨迹的长度
	public static List<KeyTrajectory> getTrajectorys(List<ClusterResult> results, List<GeoTrajectory> tras, int minKey){
		List<KeyTrajectory> keyTras=new ArrayList<KeyTrajectory>();
		List<String> names=new ArrayList<String>();
		for(GeoTrajectory tra: tras){
			KeyTrajectory kt=new KeyTrajectory(results, tra);
			names.addAll(kt.keyPathName);
		}
		Map<String, Integer> countMap=StringUtil.countFrequency(names);//统计每个名字出现的次数
		Set<String> selected=new HashSet<String>();
		int min=150;
		for(Entry<String, Integer> entry: countMap.entrySet()){
			if(entry.getValue()>=min)
				selected.add(entry.getKey());
			else
				System.out.println("移除景点："+entry.getKey());
		}
		System.out.println("大于"+min+"的景点有"+selected.size()+"个");
		for(String t: selected){
			System.out.print(t+"、");
		}
		System.out.println();
		
		List<ClusterResult> selectedResults=new ArrayList<ClusterResult>();
		for(ClusterResult cr: results){
			if(selected.contains(cr.label)){
				selectedResults.add(cr);
			}
		}
		
		keyTras=new ArrayList<KeyTrajectory>();//清空
		KeyTrajectory.pathNameMap=null;//清空
		
		for(GeoTrajectory tra: tras){
			KeyTrajectory kt=new KeyTrajectory(selectedResults, tra);
			if(kt.keyPath.size()>=minKey)
				keyTras.add(kt);
		}
		return keyTras;
	}
	
	public static String getDistanceSavePath(){
		return Photo.clusterDir+"keyTraDistance.txt";
	}
	
	//将路径的距离进行保存
	public static DistanceMatrix writeTrajectoryDistance(List<KeyTrajectory> tras){
		int total=tras.size();
		double[][] temp=new double[total][total];
		for(int i=0; i<total; ++i){
	//		System.out.println(i);
			for(int j=i; j<total; ++j){
				double t=KeyTrajectory.getDistance(tras.get(i), tras.get(j));
				temp[i][j]=t;
				temp[j][i]=t;
				if(1.0<t){
					System.out.println(t+":"+tras.get(i)+"\t\t"+tras.get(j));
				}
			}
		}
		DistanceMatrix dm=new DistanceMatrix(temp);
	//	dm.saveToFile(getDistanceSavePath());
		return dm;
	}
	
	//对关键路径进行聚类
	public static ScienceCluster clusterKeyTrajectory(DistanceMatrix dm){
			MyPoint.mpd=new MyPoint.MyPointKnownDistance(dm);
			MyPoint.mpw=new MyPoint.MyPoint1Weight();
			
			List<MyPoint> mps=new ArrayList<MyPoint>();
			for(int i=0; i<dm.rowNum; ++i){
				MyPoint mp=new MyPoint();
				mp.pointId=i;
				mps.add(mp);
			}
			ScienceCluster sc=new ScienceCluster(mps);
			List<Double> dis=sc.getAllDistance();
			NumberUtil.countFrequency(dis);
			double cutoff=sc.getCutoffDistance(0.1);
			sc.initCluster(cutoff);
			sc.setClusterCenterWithSortN(10);//确定聚类中心
			sc.decideCluster(cutoff, 0.0);//决定聚类
			sc.showClusterResult();
		//	sc.showLocalDensity();
		//	sc.showDistance();
		//	sc.initCluster(2);
			return sc;
	}
	
	public static void getMarkovModel(List<KeyTrajectory> keys){
		List<StatusChain> chains=new ArrayList<StatusChain>();
		for(KeyTrajectory key: keys){
			chains.add(new StatusChain(key.id, key.keyPathName));
		}
		StatusChain.setStatusMapAndStatusIndex(chains);
		for(StatusChain sc: chains){
		//	System.out.println(sc);
		}
		for(Map.Entry<Integer, String> entry: StatusChain.indexToStatusMap.entrySet()){
			System.out.println(entry.getKey()+","+entry.getValue());
		}
		Markov mk=new Markov(StatusChain.statusNumber, chains);
		System.out.println(mk);
	}
	
	//对关键径集合进行统计分析
	public static void countKeyTrajectory(List<KeyTrajectory> tras){
		System.out.println("关键径数量："+tras.size());
		List<Integer> temp=new ArrayList<Integer>();
		for(KeyTrajectory tra: tras){
			temp.add(tra.keyPath.size());
		}
		int maxNum=11;
		Integer [] thres=new Integer[maxNum+1];
		for(int i=0; i<=maxNum; ++i){
			thres[i]=i;
		}		
		
		int[] count=NumberUtil.countFrequency(temp, thres);
		
		for(int i=0; i<count.length; ++i){
			
		}
			
		String tempS="";
		for(int i=0; i<count.length; ++i){
			tempS+=count[i]+"\t";
		}
		System.out.println(tempS);
	}
	
	public static void clusterKeysTrajectories(List<KeyTrajectory> tras){
		DistanceMatrix dm=KeyTrajectory.writeTrajectoryDistance(tras);
		ScienceCluster sc=KeyTrajectory.clusterKeyTrajectory(dm);
		Map<Integer, List<MyPoint>> clusters=sc.clusters;
		for(Map.Entry<Integer, List<MyPoint>> entry: clusters.entrySet()){
			int temp=entry.getKey();
			List<MyPoint> mps=entry.getValue();
		//	System.out.println(temp);
			for(MyPoint mp: mps){
				if(mp.isClusterCenter){
					List<String> path=tras.get(mp.pointId).keyPathName;
					String tempP=path.get(0);
					for(int i=1; i<path.size(); ++i){
						tempP+="->"+path.get(i);
					}
					System.out.println(tempP);
				}
			}
		}
	}
	
	public static void countTopNTras(List<KeyTrajectory> tras){
		for(int i=2; i<=5; ++i){
			List<List<Integer>> topN=new ArrayList<List<Integer>>();
			for(KeyTrajectory tra: tras){
				topN.addAll(KeyTrajectory.getSplitTras(i, tra.keyPath));
			}
			List<String> keys=new ArrayList<String>();
			for(List<Integer> top: topN){
				String temp="";
				for(int j=0; j<top.size(); ++j){
					temp+=pathNameMap.get(top.get(j));
					if(j<top.size()-1)
						temp+="->";
				}
				keys.add(temp);
			}
			Map<String, Integer> keyMap=StringUtil.countFrequency(keys);
			MySort.sortMap(keyMap, 5);
		}
	}
	

	
	//统计关键路径有多少节点
	public static void countKeyTrajectory(){
		List<KeyTrajectory> keys=GeoTrajectoryCluster.clusterWaiDiTrajectories(0);
		KeyTrajectory.countKeyTrajectory(keys);
	}
	
	//获得节点间的转移频率
	public static void getKeyMarkovModel(){
		List<KeyTrajectory> keys=GeoTrajectoryCluster.clusterWaiDiTrajectories(1);
		KeyTrajectory.getMarkovModel(keys);
	}
	
	//对关键路径进行聚类
	public static void clusterKeyTrajectories(){
		List<KeyTrajectory> keys=GeoTrajectoryCluster.clusterWaiDiTrajectories(3);
		KeyTrajectory.clusterKeysTrajectories(keys);
	}
	
	public static void countTopNTrajectories(){
		List<KeyTrajectory> keys=GeoTrajectoryCluster.clusterWaiDiTrajectories(3);
		KeyTrajectory.countTopNTras(keys);
	}
	
	/***************************************下面的程序在画三维轨迹的时候而已用到*******************************/
	//得到聚类结果的坐标
	private static Map<String, String> getClustersCoordinate(List<ClusterResult> crs){
		Map<String, String> corMap=new TreeMap<String, String>();
		for(ClusterResult cr: crs){
			MyPoint mp=cr.getDrawMyPoint();
			corMap.put(cr.id+"", mp.x+","+mp.y);
		}
		return corMap;
	}
	
	//得到关键点转换的频率，以及转移次序
	public static Map<String, Integer> getKeyTrajectoryFrequency(List<KeyTrajectory> tras){
		List<String> tran=new ArrayList<String>();
		for(KeyTrajectory tra: tras){
			List<Integer> t=tra.keyPath;
			for(int i=1; i<t.size(); ++i){
				int start=t.get(i-1);
				int end=t.get(i);
				int order=i;//表示第一个去，也可以使用0开始
				tran.add(start+","+end+","+order);
			}
		}
		Map<String, Integer> tmap=StringUtil.countFrequency(tran);
		for(Map.Entry<String, Integer> entry: tmap.entrySet()){
			System.out.println(entry.getKey()+"----"+entry.getValue());
		}
		return tmap;
	}
	
	public static Map<String, Integer> getKeyTrajectoryPointFrequency(List<KeyTrajectory> tras){
		List<String> fre=new ArrayList<String>();
		for(KeyTrajectory tra: tras){
			List<Integer> t=tra.keyPath;
			for(Integer in: t){
				fre.add(in+"");
			}
		}
		Map<String, Integer> tmap=StringUtil.countFrequency(fre);
		for(Map.Entry<String, Integer> entry: tmap.entrySet()){
			System.out.println(entry.getKey()+"----"+entry.getValue());
		}
		return tmap;
	}
	
	//得到关键点转移在三维坐标中的表示
	public static void getKeyTranPoints(Map<String, String> corMap, Map<String, Integer> fre, Map<String, Integer> pointFre){
		List<String> output=new ArrayList<String>();
		int maxOrder=-1;
		for(Map.Entry<String, Integer> entry: fre.entrySet()){
			String[] ss=entry.getKey().split(",");
			String start=ss[0];
			String end=ss[1];
			int order=Integer.parseInt(ss[2]);
			if(order>maxOrder)
				maxOrder=order;
			double weight=entry.getValue();
			weight=Math.log(weight);
	//		if(weight>10)
	//			weight=10;
		//	start=corMap.get(start)+","+(-(order-1));//起点纵坐标是0
			start=corMap.get(start)+","+0;//起点纵坐标是0
			end=corMap.get(end)+","+order;//终点纵坐标是次序
			String tran=start+","+end+","+weight;
			output.add(tran);
		//	System.out.println(tran);
		}
		System.out.println("行数："+output.size());
		double weight=4;//轴线的宽度
		for(Map.Entry<String, String> entry: corMap.entrySet()){
			Integer tempi=pointFre.get(entry.getKey());
			if(null!=tempi)
				weight=tempi;
			else
				weight=1;
			weight=Math.log(weight)*2;
			String temp=entry.getValue()+","+(0)+","+entry.getValue()+","+maxOrder+","+weight;
			output.add(temp);
	//		System.out.println(temp);
		}
		FileUtil.NewFile("C:/Users/Admin/Desktop/图片地理位置/画图/12-轨迹-3D-轨迹/data.txt", StringUtil.repalceContent(output, ",", " "));
		
		output.clear();
		for(Map.Entry<String, Integer> entry: pointFre.entrySet()){
			String point=entry.getKey();
			point=corMap.get(point)+","+maxOrder+","+entry.getValue();
			output.add(point);
			System.out.println(point);
		}
		FileUtil.NewFile("C:/Users/Admin/Desktop/图片地理位置/画图/12-轨迹-3D-轨迹/pointSize.txt", StringUtil.repalceContent(output, ",", " "));
	}
	
	public static void testDraw3DTran(){
	//	GeoFilter.Area gf=GeoFilter.areaBeijing;//.areaGuGonNeiBu;
	//	GeoFilter.Area gf=GeoFilter.areaGuGongNeiBu;
		GeoFilter.Area gf=GeoFilter.areaYiHeYuan;
		int maxHourDis=24;
		List<GeoTrajectory> tras=UserGeoTrajectory.getTrajectoryAndSetDistance(gf, maxHourDis);//加载轨迹
		ScienceCluster sc=GeoTrajectoryCluster.getScienceClusterResult(gf, tras);
		List<ClusterResult> crs=ClusterResult.getClusters(sc.clusters);
		
		Map<String, String> cors=getClustersCoordinate(crs);
		
		List<KeyTrajectory> keys=KeyTrajectory.getTrajectorys(crs, tras, 2);

		
	//	keys=keys.subList(0, 6);
		System.out.println("选出关键路径数目："+keys.size());
		for(KeyTrajectory key: keys){
			System.out.println(key);
		}
		
		Map<String, Integer> fre=getKeyTrajectoryFrequency(keys);
		Map<String, Integer> pointFre=getKeyTrajectoryPointFrequency(keys);
		
		getKeyTranPoints(cors, fre, pointFre);
		
	}
	/*******************************************************************************************/
	
	
	public static void main(String[] args)
	{
	//	testDraw3DTran();
	//	clusterKeyTrajectory();
	//	writeCluterResult();
		testGetSplitTras();
	}

}
