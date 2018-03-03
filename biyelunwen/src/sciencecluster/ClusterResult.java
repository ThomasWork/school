package sciencecluster;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import draw.KmlFile;

public class ClusterResult
{
	public int id;
	public List<MyPoint> points;
	
	public Set<String> blocks;
	
	public String label;
	
	public static final int NotClusterId=-1;
	public static List<MyPoint> places;
	
	static{
		ClusterResult.places=KmlFile.getPointsFromKml("C:/Users/Admin/Desktop/北京景点选中.kml");
		
	}
	
	public ClusterResult(int cid, List<MyPoint> mps){
		this.id=cid;
		this.points=mps;
		this.setBlocks();
		this.label="未设置";
	}
	
	public ClusterResult(){
		this.points=new ArrayList<MyPoint>();
	}
	
	public static ClusterResult mergeClusterResult(ClusterResult cra,ClusterResult crb){
		ClusterResult cr=new ClusterResult();
		cr.id=cra.id;
		cr.label=cra.label;
		cr.points.addAll(cra.points);
		cr.points.addAll(crb.points);
		cr.setBlocks();
		return cr;
	}
	
	public static List<ClusterResult> mergetClusterResultWithLabel(List<ClusterResult> crs){
		List<ClusterResult> nrs=new ArrayList<ClusterResult>();
		Set<String> labels=new TreeSet<String>();
		for(ClusterResult cr: crs){
			labels.add(cr.label);
		}
		System.out.println("聚类结果数量："+crs.size()+"\t标签数量："+labels.size());
		for(ClusterResult cr: crs){
			System.out.println(cr.id+"\t"+cr.label+"\t数量："+cr.points.size()+"\t"+cr.blocks.size());
		}
		for(String label: labels){
			ClusterResult current=null;
			for(ClusterResult cr: crs){
				if(label.equals(cr.label)){
					if(null==current)//如果当前还没有
						current=cr;
					else
						current=mergeClusterResult(current, cr);
				}//if
			}//for cr
			nrs.add(current);
		}//for label
		System.out.println("合并之后：");
		for(ClusterResult cr: nrs){
			System.out.println(cr.id+"\t"+cr.label+"\t数量："+cr.points.size()+"\t"+cr.blocks.size());
		}
		return nrs;		
	}
	
	//设置聚类的块
	private void setBlocks(){
		this.blocks=new TreeSet<String>();
		
		for(MyPoint mp: this.points){
			String key=ClusterResult.getPointKey(mp);
			this.blocks.add(key);
		}
	}
	
	public boolean isPointInCluster(MyPoint mp){
		String key=mp.geoBlockIndexString;
		if(this.blocks.contains(key))
			return true;
		return false;
	}
	
	public MyPoint getDrawMyPoint(){
		MyPoint mp=new MyPoint();
		double xSum=0, ySum=0;
		for(MyPoint p: this.points){
			String[] ss=p.label.split(",");
			xSum+=Double.parseDouble(ss[0]);
			ySum+=Double.parseDouble(ss[1]);
		}
		mp.label=this.id+"";
		mp.x=xSum/this.points.size();
		mp.y=ySum/this.points.size();
		return mp;
	}
	
	//根据景点的地理位置来设置聚类中心的标签
	public void setClusterResultLabel(List<MyPoint> interests){
		MyPoint.MyPointGPSDistance mg=new MyPoint.MyPointGPSDistance();
		double minDis=Double.MAX_VALUE;
		MyPoint mpmin=null;
		double MIN=200;//如果在200米之内表示为景点
		for(MyPoint center: this.points){
			MyPoint temp=new MyPoint();
			String[] ss=center.label.split(",");
			temp.x=Double.parseDouble(ss[0]);temp.y=Double.parseDouble(ss[1]);
			for(int i=0; i<interests.size(); ++i){
				double distance=mg.getPointDistance(temp, interests.get(i));
				if(distance<minDis){
					minDis=distance;
					mpmin=interests.get(i);
		//			System.out.println(center+","+interests.get(i)+","+distance);
				}				
			}
		}
		this.label="没有匹配的景点";
		if(minDis<MIN){
			this.label=mpmin.label;
		}
	}
	
	public static void setClusterResultsLabel(List<ClusterResult> crs, List<MyPoint> interests){
		String temp="";
		List<MyPoint> mps = new ArrayList<MyPoint>();		
		for(ClusterResult cr: crs){
			cr.setClusterResultLabel(ClusterResult.places);
			MyPoint center=cr.getDrawMyPoint();//得到当前聚类中心的标识点
			center.label=center.label+"_"+cr.label;
		//	temp+=center.label+",";
			temp+=cr.label+"、";
			mps.add(center);//这里是为了调试用
		}
		System.out.println(temp);
		KmlFile.writeMyPoint("设置聚类结果标签：cluster_to_point", mps);
	}
	
	@Override
	public String toString(){
		StringBuilder sb=new StringBuilder("id:"+this.id);
		Iterator<String> it=this.blocks.iterator();
		while(it.hasNext()){
			sb.append("\n"+it.next());
		}
		return sb.toString();
	}
	
	//获取每个聚类结果的排名
	public static Map<String, Integer> getPlacesIndex (List<ClusterResult> crs){
		Map<String, Integer> index=new TreeMap<String, Integer>();
		for(ClusterResult cr: crs){
			cr.setClusterResultLabel(ClusterResult.places);
			MyPoint center=cr.getDrawMyPoint();//得到当前聚类中心的标识点
			int num=Integer.parseInt(center.label);
			index.put(cr.label, num);
			center.label=center.label+"_"+cr.label;
		}
		return index;
	}
	
	private static String getPointKey(MyPoint mp){
		String key=(int)(mp.x+0.01)+","+(int)(mp.y+0.01);//加0.01防止错误
		return key;
	}
	
	public static List<ClusterResult> getClusters(Map<Integer, List<MyPoint>> result){
		List<ClusterResult> clusters=new ArrayList<ClusterResult>();
		
		for(Map.Entry<Integer, List<MyPoint>> entry: result.entrySet()){
			int cId=entry.getKey();
			if(ClusterResult.NotClusterId==cId)
				continue;
			clusters.add(new ClusterResult(cId, entry.getValue()));
		}
		
		return clusters;
	}
	
	public static Map<String, List<Integer>> getInitPlaceIndex(){
		Map<String, List<Integer>> placesIndex= new TreeMap<String, List<Integer>>();
		for(int i=0; i<ClusterResult.places.size(); ++i){
			String label=places.get(i).label;
			placesIndex.put(label, new ArrayList<Integer>());
		}
		return placesIndex;
	}
	
	public static Map<String, List<Double>> getIndexScore(Map<String, List<Integer>> indexes){
		Map<String, List<Double>> placesScore=new TreeMap<String, List<Double>>();
		for(Map.Entry<String, List<Integer>> entry: indexes.entrySet()){
			List<Double> score=new ArrayList<Double>();
			List<Integer> index=entry.getValue();
			for(Integer in: index){
				score.add(getScore(in));
			}
			placesScore.put(entry.getKey(), score);
		}
		return placesScore;
	}
	
	public static double getScore(int in){
		if(-1==in)
			return 0;
		if(1<=in && in<=10)
		{
			double temp=(11-in)*8;
			return temp;
		}
		
/*		if(in<=2)
			return 5;
		if(in<=4)
			return 4;
		if(in<=6)
			return 3;
		if(in<=8)
			return 2;
		if(in<=10)
			return 1;*/
		System.out.println("haha:"+in);
		return 0;
	}
	
	public static void setPlacesIndex(Map<String, List<Integer>> places, Map<String, Integer> once){
		for(Map.Entry<String, Integer> entry: once.entrySet()){
			String key=entry.getKey();
			if(key.contains("未知"))
				System.out.println("包含未知");
			places.get(key).add(entry.getValue());
		}
	}
}
