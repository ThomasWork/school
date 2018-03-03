package algorithm.kmeans;

import java.util.ArrayList;
import java.util.List;

public class MyKMeans
{
	public int featureNumber;
	public int clusterNumber;
	public double totalDistance;
	
	public List<ClusterEntity>entityList;
	public List<Cluster>clusterList;
	
	public static void Test() throws CloneNotSupportedException
	{
		List<Feature>f1=new ArrayList<Feature>();f1.add(new DoubleFeature(1));f1.add(new DoubleFeature(3));
		List<Feature>f2=new ArrayList<Feature>();f2.add(new DoubleFeature(3));f2.add(new DoubleFeature(1));
		List<Feature>f3=new ArrayList<Feature>();f3.add(new DoubleFeature(3));f3.add(new DoubleFeature(0.5));
		List<Feature>f4=new ArrayList<Feature>();f4.add(new DoubleFeature(4));f4.add(new DoubleFeature(3));
		List<Feature>f5=new ArrayList<Feature>();f5.add(new DoubleFeature(5));f5.add(new DoubleFeature(3));
		List<Feature>f6=new ArrayList<Feature>();f6.add(new DoubleFeature(5));f6.add(new DoubleFeature(2));
		List<ClusterEntity> entities=new ArrayList<ClusterEntity>();
		entities.add(new ClusterEntity(1, f1));
		entities.add(new ClusterEntity(2, f2));
		entities.add(new ClusterEntity(3, f3));
		entities.add(new ClusterEntity(4, f4));
		entities.add(new ClusterEntity(5, f5));
		entities.add(new ClusterEntity(6, f6));
		
		MyKMeans mk=new MyKMeans(entities);

		for(int i=1;i<6;i++)
		{
			System.out.println(i+"个分类");
		mk.initCluster(i);
		mk.startCluster();
		mk.showResult();
		}
	}
	
	public MyKMeans(List<ClusterEntity>entities)//聚类的数量
	{
		this.featureNumber=entities.get(0).featureNumber;
		this.entityList=entities;
		this.clusterList=new ArrayList<Cluster>();
	}
	
	public void putToMinCluster()
	{
		for(int i=0;i<this.clusterNumber;i++)
			this.clusterList.get(i).entities.clear();
		for(int i=0;i<this.entityList.size();i++)
		{
			int index=-1;
			double dis=Double.MAX_VALUE;
			for(int j=0;j<this.clusterNumber;j++)
			{
				double temp=this.entityList.get(i).getDistance(this.clusterList.get(j).centerEntity);
				if(temp<dis)
				{
					index=j;
					dis=temp;
				}
			}
			this.clusterList.get(index).entities.add(this.entityList.get(i));
		}
	}
	
	/***
	 * 依次选择距离最大的clusterNumber个聚类中心点
	 * @return
	 */
	public List<ClusterEntity> getInitEntities()
	{
		List<ClusterEntity>inits=new ArrayList<ClusterEntity>();
		for(int i=0;i<this.clusterNumber;i++)
		{
			double maxDis=-1;
			int index=-1;
			for(int j=0;j<this.entityList.size();j++)
			{
				double minDis=Double.MAX_VALUE;
				for(int k=0;k<inits.size();k++)
				{
					double temp=this.entityList.get(j).getDistance(inits.get(k));
					if(temp<minDis)
						minDis=temp;
				}
				if(minDis>maxDis)
				{
					maxDis=minDis;
					index=j;
				}
			}
			inits.add(this.entityList.get(index));
		}
//		for(int i=0;i<inits.size();i++)
//			inits.get(i).show();
		return inits;
	}
	
	public void initCluster(int clusterNumber) throws CloneNotSupportedException
	{//初始化聚类中心点
		this.clusterNumber=clusterNumber;
		this.clusterList.clear();
		List<ClusterEntity>  entities=this.getInitEntities();
		for(int i=0;i<this.clusterNumber;i++)
		{
			ClusterEntity ce=(ClusterEntity) entities.get(i).clone();
			ce.id=i;//聚类中心采用新的id
			Cluster cluster=new Cluster(ce);
			this.clusterList.add(cluster);
		}
	}
	
	public void startCluster()
	{
		while(true)
		{
			boolean finish=true;
			this.putToMinCluster();
			for(int i=0;i<this.clusterNumber;i++)
			{
				boolean temp=this.clusterList.get(i).updateCenterEntity();
				if(!temp)
					finish=false;
			}
//			showResult();
			if(finish)
			{
				this.setTotalDistance();
				System.out.println(this.totalDistance);
				break;
			}
		}
	}
	
	public void setTotalDistance()
	{
		this.totalDistance=0;
		for(int i=0;i<this.clusterNumber;i++)
		{
			this.clusterList.get(i).setClusterDistance();
			this.totalDistance+=this.clusterList.get(i).clusterDistance;
		}
	}
	
	public void showResult()
	{
//		System.out.println("原始数据:"+this.entityList.size());
//		for(int i=0;i<this.entityList.size();i++)
//			this.entityList.get(i).show();
		for(int i=0;i<this.clusterNumber;i++)
			this.clusterList.get(i).showResult();
	}
}