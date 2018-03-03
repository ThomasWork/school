package algorithm.kmeans;

import java.util.ArrayList;
import java.util.List;

public class Cluster
{
	private static double leastError=0.0001;
	
	public ClusterEntity centerEntity;	
	public List<ClusterEntity> entities;
	
	public double clusterDistance;
	
	public Cluster(ClusterEntity ce)
	{
		this.centerEntity=ce;
		this.entities=new ArrayList<ClusterEntity>();
	}
	
	public boolean updateCenterEntity()
	{
		if(this.entities.size()==0)
			return true;
		boolean finish=true;
		for(int i=0;i<this.centerEntity.featureNumber;i++)
		{//对于每一个特征进行更新
			double sum=0;
			for(int j=0;j<this.entities.size();j++)
				sum+=this.entities.get(j).features.get(i).getValue();
			DoubleFeature df=new DoubleFeature(sum/this.entities.size());///////////////////////////////////////////////如果没有怎么更新？
			if(Math.abs(df.getDistance(this.centerEntity.features.get(i)))>Cluster.leastError)//如果误差更新了
					finish=false;
			this.centerEntity.features.get(i).setValue(df);
		}
		return finish;
	}
	
	public void setClusterDistance()
	{
		this.clusterDistance=0;
		for(int i=0;i<this.entities.size();i++)
			this.clusterDistance+=this.centerEntity.getDistance(this.entities.get(i));
	}
	
	public void showResult()
	{
	//	System.out.print("中心点:");
		this.centerEntity.show();
	//	for(int i=0;i<this.entities.size();i++)
	//		this.entities.get(i).show();
	}
}
