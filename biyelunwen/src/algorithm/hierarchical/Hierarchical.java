package algorithm.hierarchical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sciencecluster.MyPoint;

public class Hierarchical
{
	public List<MyPoint> points;
	public Cluster finalResult;
	public List<Cluster> clusters;
	
	public static Map<String, Double> clustersDistance;
	
	public Hierarchical(List<MyPoint> mps){
		this.points=mps;
		this.setClustersDistance();
	}
	
	//初始化聚类之间的距离
	private void setClustersDistance(){
		Hierarchical.clustersDistance=new HashMap<String, Double>();
		for(int i=0; i<this.points.size(); ++i){
			for(int j=i+1; j<this.points.size(); ++j){
				String key=this.points.get(i).pointId+","+this.points.get(j).pointId;
				double dis=this.points.get(i).getDistance(this.points.get(j));
				clustersDistance.put(key, dis);
			}				
		}
	}
	
	public static double getClustersDistance(Cluster ca, Cluster cb){
		String key=ca.id+","+cb.id;
		Double temp=Hierarchical.clustersDistance.get(key);
		if(null==temp){//如果没有找到
			temp=ca.getDistance(cb);
			Hierarchical.clustersDistance.put(key, temp);
		}
		return temp;
	}
	
	public void clusterPoints(){
		while(this.clusters.size()>1){
			this.clusterOnce();
		}
	}
	
	public void clusterOnce(){
		double dis=Double.MAX_VALUE;
		int minI=-1, minJ=-1;
		for(int i=0; i<this.clusters.size(); ++i){
			for(int j=i+1; j<this.clusters.size(); ++j){
				double temp=Hierarchical.getClustersDistance(this.clusters.get(i), this.clusters.get(j));
				if(temp<dis){
					dis=temp;
					minI=i;
					minJ=j;
				}
			}
		}
		List<Cluster> left=new ArrayList<Cluster>();
		for(int i=0; i<this.clusters.size(); ++i){
			if(i!=minI && i!= minJ){
				left.add(this.clusters.get(i));
			}
		}
		left.add(Cluster.mergeCluster(this.clusters.get(minI), this.clusters.get(minJ)));
		this.clusters=left;
	}
	
	public void initClusters(){
		this.clusters=new ArrayList<Cluster>();
		for(MyPoint mp: this.points){
			this.clusters.add(new Cluster(mp));
		}
	}
	
	
	
	public static void main(String[] args)
	{
	}

}
