package sciencecluster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import myutil.NumberUtil;
import myutil.SortValue;
import myutil.fileprocess.FileUtil;

public class ScienceCluster
{
	public static double cutoffRatio=0.02;
	
	public int pointNumber;
	public int clusterNumber=0;
	
	public double[] localDensity;//数据点的局部密度
	public int[] densitySortIndexes;//对局部密度排序之前的节点的下标
	public int[] nearestHigherIndex;//最近的具有更高密度的点
	public double[] nearestHigherDistance;//最近的具有更高密度的点的距离
	public double maxDistance;
	public double cutoffDistance;//截断距离
	public List<MyPoint> points;
	
	public int[] clusterId;//每一个id所对应的聚类
	public Map<Integer, List<MyPoint>> clusters;
	
	public static boolean showDistance=false;
	public static boolean showPoints=true;
	public static boolean showLocalDensity=false;
	public static boolean showDensityDistance=false;
	
	public static int densityAlgorithm=0;//等于0表示基于单用户互质的方式进行
	
	public ScienceCluster(List<MyPoint> points)
	{
		this.points=points;
		this.pointNumber=points.size();
		System.out.println("共有聚类点："+this.pointNumber);
		if(ScienceCluster.showDistance)
			this.showDistance();
	}
	
	//为了防止空间不够用，所以采用增加计算量的方式
	private double getDistance(int i, int j){
		double dis = this.points.get(i).getDistance(this.points.get(j));
		return dis;
	}
	
	//将所有点对的距离放到一个列表中
	public List<Double> getAllDistance(){
		List<Double> dis=new ArrayList<Double>();
		for(int i=0; i<this.pointNumber; ++i){
			for(int j=i+1; j<this.pointNumber; ++j)
				dis.add(this.getDistance(i, j));
		}
		return dis;
	}
	
	//根据平均邻居的数量来设置截断距离
	public double getCutoffDistance(double rate){
		double neigh=this.pointNumber*rate;
		System.out.println("需要的平均密度："+neigh);
		List<Double> dis=this.getAllDistance();
		Collections.sort(dis);
		double min=dis.get(0);
		double max=dis.get(dis.size()-1);
		double mid=0;
		double cur=0;
		int loop=10;
		while(Math.abs(neigh-cur)>0.1 && (loop--)>0){
			mid=(max+min)/2;
			System.out.println("当前截断距离："+mid+"\t当前平均密度："+cur);
			this.cutoffDistance=mid;
			this.setLocalDensityWithCount();
			cur=0;
			for(int i=0; i<this.pointNumber; ++i){
				cur+=this.localDensity[i];
			}
			cur/=this.pointNumber;//设置当前的平均密度
			if(cur>neigh)
				max=mid;
			else
				min=mid;
		}
		return mid;
	}
	
	//获得节点为n的点的局部密度排序
	private int getPointInOrder(int n){
		return this.densitySortIndexes[n];
	}
	
	//设置截断距离
	public void setCutoffDistance(double distance)
	{
	/*	int total=this.pointNumber*this.pointNumber;		
		double[] temp=new double[total];
		for(int i=0; i<this.pointNumber; ++i)
			for(int j=0; j<this.pointNumber; ++j)
				temp[i*this.pointNumber+j]=this.distances[i][j];
		Arrays.sort(temp);
		int index=(int) (total*ScienceCluster.cutoffRatio);
		this.cutoffDistance=temp[index];
		if(distance>0)*/
			this.cutoffDistance=distance;
		System.out.println("截断距离为："+this.cutoffDistance);
	}
	
	//使用高斯核计算局部密度
	public void setLocalDensityWithGaussian()
	{
		this.localDensity=new double[this.pointNumber];
		
		for(int i=0; i<this.pointNumber; ++i)
			this.localDensity[i]=0;
		
		for(int i=0; i<this.pointNumber; ++i)
		{
			for(int j=i+1; j<this.pointNumber; ++j)
			{				
				double temp=this.getDistance(i, j)/this.cutoffDistance;
				temp=Math.exp(-temp*temp);
				this.localDensity[i]+=temp;
				this.localDensity[j]+=temp;
			}
		}
	}
	
	//使用计数的方法计算局部密度，注意，这里使用了点的权重
	public void setLocalDensityWithCount()
	{
		this.localDensity=new double[this.pointNumber];
		for(int i=0; i<this.pointNumber; ++i)
			this.localDensity[i]=MyPoint.mpw.getPointWeight(this.points.get(i));//初始化局部密度等于权重
		
		for(int i=0; i<this.pointNumber; ++i)
		{
		//	if(i%50==0)
		//		System.out.println(i);
			for(int j=i+1; j<this.pointNumber; ++j)//这里+1，防止再次添加自己
			{
				if(this.getDistance(i, j)<=this.cutoffDistance)
				{
					this.localDensity[i]+=MyPoint.mpw.getPointWeight(this.points.get(j));//+j
					this.localDensity[j]+=MyPoint.mpw.getPointWeight(this.points.get(i));//+i
			//		System.out.println(i);
				}
			}
		}		
	}
	
	public void setNearestHigherIndex()
	{
		this.nearestHigherIndex=new int[this.pointNumber];
		this.nearestHigherDistance=new double[this.pointNumber];
		
		int maxIndex=this.getPointInOrder(0);
//		System.out.println("密度最大的为："+this.points.get(maxIndex).label);
		this.nearestHigherIndex[maxIndex]=-1;//密度最大的点设置为-1
		this.nearestHigherDistance[maxIndex]=0;//这里只是为了方便处理
		
		for(int i=1; i<this.pointNumber; ++i)//从下标为1的点开始
		{
			int cur=this.getPointInOrder(i);//当前处理的点的下标
			this.nearestHigherDistance[cur]=Double.MAX_VALUE;
			for(int j=0; j<i; ++j)
			{
				int neigh=this.getPointInOrder(j);//密度更大的或者相等的点的下标
				double temp=this.getDistance(cur, neigh);
				if(temp<this.nearestHigherDistance[cur])
				{
					this.nearestHigherDistance[cur]=temp;
					this.nearestHigherIndex[cur]=neigh;
				}
			}
		}
		
		for(int i=0; i<this.pointNumber; ++i)
		{
			if(this.nearestHigherDistance[i]>this.nearestHigherDistance[maxIndex])
				this.nearestHigherDistance[maxIndex]=this.nearestHigherDistance[i];
		}
	}
	
	
	//对密度和距离的乘积进行排序，然后选择前n大的作为聚类中心的依据。
	public void setClusterCenterWithSortN(int nMax)//获得第n大的数据
	{
		List<Double> multi=new ArrayList<Double>();
		for(int i=0; i<this.pointNumber; ++i)
		{
			double tdis=this.nearestHigherDistance[i];
		//	System.out.println(tdis);
			double tden=this.localDensity[i];
			
			multi.add(tdis*tden);
		}
		double nMaxNumber=NumberUtil.getSortN(multi, nMax);

		this.clusterId=new int[this.pointNumber];
		final int noflag=-1;
		for(int i=0; i<this.pointNumber; ++i)
			this.clusterId[i]=noflag;//初始化为-1
		int total=0;
		for(int i=0; i<this.pointNumber; ++i)
		{
			int cur=this.densitySortIndexes[i];//这里按照密度降序访问，最后的结果就是越靠前局部密度越大
			double tdis=this.nearestHigherDistance[cur];
		//	System.out.println(tdis);
			double tden=this.localDensity[cur];
			
			double temp=tdis*tden;
			
			if(temp>=nMaxNumber)
			{
			//	System.out.println(tden+"--->"+density);
				total++;
				this.clusterId[cur]=total;//这里使用正确的下标
				this.points.get(cur).clusterMaxDensity=tden;
				this.points.get(cur).isClusterCenter=true;
			//	System.out.println("index"+index);
		//		System.out.println("聚类中心标签："+this.points.get(i).label);
			}
			else
				this.points.get(cur).isClusterCenter=false;
		}
		
		this.clusterNumber=total;
		System.out.println("第"+nMax+"大的数为："+NumberUtil.df4.format(nMaxNumber)+"聚类个数为："+total);
	}
	
	public void decideCluster(double maxDistance, double rate)//如果距离最近较高密度点的距离太大，则把它作为边界点
	{
		int num=this.clusterNumber+1;
		double[] sum=new double[num];//用来保存局部密度之和
		int[] count=new int[num];
		for(int i=0; i<this.pointNumber; ++i){
			int temp=this.clusterId[i];
			if(temp>-1){
				sum[temp]=this.localDensity[i];
				count[temp]=1;
			}
		}
		for(int i=0; i<this.pointNumber; ++i)
		{
			int index=this.getPointInOrder(i);//这里必须从高到低
			if(-1==this.clusterId[index] && this.nearestHigherDistance[index]<=maxDistance)
			{
				int centerIndex=this.nearestHigherIndex[index];//最近更高密度的点
		//		System.out.println("index:"+index+"\t"+centerIndex);//+"\t"+"C:"+this.clusterId[centerIndex]);
				int ccId=this.clusterId[centerIndex];//更高的点的聚类结果
				if(-1==ccId)//如果更高点没有设置，则继续循环
					continue;
				if(this.localDensity[index]<rate*sum[ccId]/count[ccId])//如果密度过小
					continue;
		//		System.out.println("标签："+this.points.get(index).label+"加入聚类："+ccId+":\t距离："+this.nearestHigherDistance[index]+"\t密度："+this.localDensity[index]+","+rate*(sum[ccId]/count[ccId]));
				sum[ccId]+=this.localDensity[index];
				count[ccId]+=1;
				this.clusterId[index]=ccId;
				this.points.get(index).clusterMaxDensity=this.points.get(centerIndex).clusterMaxDensity;
			}
		}
		
		for(int i=0; i<this.points.size(); ++i){
			this.points.get(i).clusterId=this.clusterId[i]+"";
		}
		
		this.clusters=new HashMap<Integer, List<MyPoint>>();
		for(int i=0; i<this.pointNumber; ++i)
		{
			int cId = this.clusterId[i];
			List<MyPoint> mps=this.clusters.get(cId);
			if(null==mps)
			{
				mps=new ArrayList<MyPoint>();
				this.clusters.put(cId, mps);
			}
			mps.add(this.points.get(i));
		}
	}
	
	//输出每个点的密度和距离
	public void showDensityDistance()
	{
		System.out.println("标签-密度-距离最近标签-距离");

	//	for(int i=0; i<this.pointNumber; ++i)
	//		System.out.println("hi:"+this.nearestHigherIndex[i]);
		for(int i=0; i<this.pointNumber; ++i)
		{
			int index=this.getPointInOrder(i);//正确的下标
			if(this.nearestHigherIndex[index]>-1)
			{
				System.out.print(this.points.get(index).label+",");
				System.out.print(this.localDensity[i]+",");
				System.out.print(this.points.get(this.nearestHigherIndex[index]).label+",");
				System.out.println(this.nearestHigherDistance[index]);
			}
			else
			{
				System.out.print(this.points.get(index).label+",");
				System.out.println(this.localDensity[i]+",");
			}
		}
	}
	
	//输出每个点的坐标
	public void showPoints()
	{
		System.out.println("点的坐标为:");
		for(MyPoint mp: this.points)
			System.out.println(mp);
	}
	
	//输出点对之间的距离
	public void showDistance()
	{
		System.out.println("Distance:");
		for(int i=0; i<this.pointNumber; ++i)
		{
			for(int j=0; j<this.pointNumber; ++j)
				System.out.printf("%3f", this.getDistance(i, j));
			System.out.println();
		}
	}
	
	//输出每个点的局部密度
	public void showLocalDensity()
	{
		System.out.println("局部密度:");
		for(int i=0; i<this.pointNumber; ++i)
		{
			int index=this.getPointInOrder(i);//密度最高的下标
		//	int index=i;
			MyPoint mp=this.points.get(index);
			System.out.println(mp.label+":"+mp.x+":"+mp.y+":"+this.localDensity[index]);
		}
	}
	
	public void showHeigerPointIndexAndDistance(){
		for(int i=0; i<this.pointNumber; ++i){
			String highLabel="self";
			if(this.nearestHigherIndex[i]>-1)
				highLabel=this.points.get(this.nearestHigherIndex[i]).toString();
			System.out.println(this.points.get(i)+"-->"
			+highLabel+":\t"
					+this.nearestHigherDistance[i]);
		}
	}
	
	public void showClusterResult(){
		for(Map.Entry<Integer, List<MyPoint>> cluster: this.clusters.entrySet())
		{
			int cId=cluster.getKey();
			List<MyPoint> mps= cluster.getValue();
			StringBuilder sb=new StringBuilder();
			for(MyPoint mp: mps)
				sb.append(mp.label+"\t");
			System.out.println("聚类"+cId+"("+mps.size()+"):\t"+sb.toString());
		}
	}
	
	public List<MyPoint> getUsedPoints(){
		List<MyPoint> used=new ArrayList<MyPoint>();
		for(MyPoint mp: this.points){
			if(Integer.parseInt(mp.clusterId)>-1)
				used.add(mp);
		}
		return used;
	}
	
	public void show()
	{
//		this.showPoints();
//		this.showDistance();
		System.out.println("maxDistance:"+this.maxDistance);
		System.out.println("cutoffDistance:"+this.cutoffDistance);
		if(ScienceCluster.showLocalDensity)
			this.showLocalDensity();
		if(ScienceCluster.showDensityDistance)
			this.showDensityDistance();
	}
	
	//根据截断距离设置密度，然后设置最近更高密度点
	public void initCluster(double cutDistance)
	{
		this.setCutoffDistance(cutDistance);//设置截断距离
		this.setLocalDensityWithCount();
	//	this.setLocalDensityWithGaussian();//使用高斯函数求解局部密度
		System.out.println("设置局部密度完毕！");
		this.densitySortIndexes=SortValue.sortReturnIndex(this.localDensity);
		this.setNearestHigherIndex();
	}
	
	/******************************下面的代码是最后进行聚类*******************/
	
	public void cluster(int nMax, double centerDistance, double rate)
	{
		System.out.println("使用第"+nMax+"大的数作为聚类中心划分");
		this.setClusterCenterWithSortN(nMax);
		this.decideCluster(centerDistance, rate);
	}
	
	public static void test1()
	{
		List<MyPoint> points=MyPoint.getPoints("./src/sciencecluster/ceshi/points.txt");
		ScienceCluster nc=new ScienceCluster(points);
		MyPoint.mpw=new MyPoint.MyPoint1Weight();
		MyPoint.mpd=new MyPoint.MyPointCoordinateDistance();
		nc.showPoints();
		nc.initCluster(1);
	//	nc.showLocalDensity();
	//	nc.showHeigerPointIndexAndDistance();
		nc.cluster(3, 0.9, 0);
		nc.showClusterResult();
		List<ClusterResult> results=ClusterResult.getClusters(nc.clusters);
		for(ClusterResult cr: results){
			System.out.println(cr);
		}
		for(MyPoint mp: points){
			for(ClusterResult cr: results){
				if(cr.isPointInCluster(mp)){
					System.out.println(mp+"在聚类："+cr.id);
				}
			}
		}
	}
	
	public static void main(String[] args){
		test1();
	}
}
