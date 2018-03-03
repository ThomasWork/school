package algorithm.hierarchical;

import java.util.ArrayList;
import java.util.List;

import sciencecluster.MyPoint;

public class Cluster
{
	public int id;//聚类的编号
	
	public List<MyPoint> points;
	public List<Cluster> children;//合并到这个聚类中的
	
	public static int newClusterId=1;
	
	public Cluster(MyPoint point)
	{
		this.id=point.pointId;
		this.points=new ArrayList<MyPoint>();
		this.points.add(point);
	}
	
	public Cluster(int id)
	{
		this.id=id;
	}
	
	public double getDistance(Cluster other){
		double sum=0;
		int sa=this.points.size();
		int sb=other.points.size();
		for(int i=0; i<sa; ++i){
			for(int j=0; j<sb; ++j)
				sum+= this.points.get(i).getDistance(other.points.get(j));
		}
		sum=sum/(sa*sb);
		return sum;
	}
	
	public static Cluster mergeCluster(Cluster one, Cluster other){
		Cluster c=new Cluster(Cluster.newClusterId);
		Cluster.newClusterId++;
		c.points.addAll(one.points);
		c.points.addAll(other.points);
		c.children.add(one);
		c.children.add(other);
		return c;
	}
}
