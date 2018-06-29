package life.cluster;

import java.util.ArrayList;
import java.util.Iterator;

public class kmedoids
{
	public static class DataPoint
	{
		public double dimension[]; // 样本点的维度
		public String pointName; // 样本点名字
		public Cluster cluster; // 类簇
		public double euDt;// 样本点到质点的距离

		public DataPoint(double dimension[], String pointName)
		{
			this.dimension = dimension;
			this.pointName = pointName;
			this.cluster = null;
		}

		public void setCluster(Cluster cluster)
		{
			this.cluster = cluster;
		}

		public double calEuclideanDistanceSum()
		{
			double sum = 0.0;
			Cluster cluster = this.cluster;
			ArrayList<DataPoint> dataPoints = cluster.dataPoints;

			for (int i = 0; i < dataPoints.size(); i++)
			{
				double[] dims = dataPoints.get(i).dimension;
				for (int j = 0; j < dims.length; j++)
				{
					double temp = Math.pow((dims[j] - this.dimension[j]), 2);
					sum = sum + temp;
				}
			}
			return Math.sqrt(sum);
		}

		public double testEuclideanDistance(Medoid c)
		{
			double sum = 0.0;
			double[] cDim = c.dimension;
			for (int i = 0; i < dimension.length; i++)
			{
				double temp = Math.pow((dimension[i] - cDim[i]), 2);
				sum = sum + temp;
			}
			return Math.sqrt(sum);
		}
	}

	public static class Medoid
	{
		public double dimension[]; // 质点的维度
		public Cluster cluster; // 所属类簇
		public double etdDisSum;// Medoid到本类簇中所有的欧式距离之和

		public Medoid(double dimension[])
		{
			this.dimension = dimension;
		}

		public void calcMedoid()
		{// 取代价最小的点
			calcEtdDisSum();
			double minEucDisSum = this.etdDisSum;
			ArrayList<DataPoint> dps = this.cluster.dataPoints;
			for (int i = 0; i < dps.size(); i++)
			{
				double tempeucDisSum = dps.get(i).calEuclideanDistanceSum();
				if (tempeucDisSum < minEucDisSum)
				{
					dimension = dps.get(i).dimension;
					minEucDisSum = tempeucDisSum;
				}
			}
		}

		// 计算该Medoid到同类簇所有样本点的欧斯距离和
		private void calcEtdDisSum()
		{
			double sum = 0.0;
			Cluster cluster = this.cluster;
			ArrayList<DataPoint> dataPoints = cluster.dataPoints;

			for (int i = 0; i < dataPoints.size(); i++)
			{
				double[] dims = dataPoints.get(i).dimension;
				for (int j = 0; j < dims.length; j++)
				{
					double temp = Math.abs(dims[j] - this.dimension[j]);
					sum = sum + temp;
				}
			}
			etdDisSum = sum;
		}
	}

	public static class Cluster
	{
		private String clusterName; // 类簇名
		private Medoid medoid; // 类簇的质点
		private ArrayList<DataPoint> dataPoints; // 类簇中各样本点
		
		public Cluster(String clusterName)
		{
			this.clusterName = clusterName;
			this.medoid = null; // will be set by calling setCentroid()
			dataPoints = new ArrayList<DataPoint>();
		}

		public void addDataPoint(DataPoint dp)
		{ // called from CAInstance
			dp.setCluster(this);// 标注该类簇属于某点,计算欧式距离
			this.dataPoints.add(dp);
		}

		public void removeDataPoint(DataPoint dp)
		{
			this.dataPoints.remove(dp);
		}

		public int getNumDataPoints()
		{
			return this.dataPoints.size();
		}

		public DataPoint getDataPoint(int pos)
		{
			return (DataPoint) this.dataPoints.get(pos);
		}
	}
	
	public static class ClusterAnalysis {

	    private Cluster[] clusters;// 所有类簇
	    private int miter;// 迭代次数
	    private ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();// 所有样本点
	    private int dimNum;//维度

	    public ClusterAnalysis(int k, int iter, ArrayList<DataPoint> dataPoints,int dimNum) {
	        clusters = new Cluster[k];// 类簇种类数
	        for (int i = 0; i < k; i++) {
	            clusters[i] = new Cluster("Cluster:" + i);
	        }
	        this.miter = iter;
	        this.dataPoints = dataPoints;
	        this.dimNum=dimNum;
	    }

	    public int getIterations() {
	        return miter;
	    }

	    public ArrayList<DataPoint>[] getClusterOutput() {
	        ArrayList<DataPoint> v[] = new ArrayList[clusters.length];
	        for (int i = 0; i < clusters.length; i++) {
	            v[i] = clusters[i].dataPoints;
	        }
	        return v;
	    }

	    public void startAnalysis(double[][] medoids) {
	        setInitialMedoids(medoids);
	        double[][] newMedoids=medoids;
	        double[][] oldMedoids=new double[medoids.length][this.dimNum];

	        while(!isEqual(oldMedoids,newMedoids)){
	            for(int m = 0; m < clusters.length; m++){//每次迭代开始情况各类簇的点
	                clusters[m].dataPoints.clear();
	            }
	            for (int j = 0; j < dataPoints.size(); j++) {
	                int clusterIndex=0;
	                double minDistance=Double.MAX_VALUE;

	                for (int k = 0; k < clusters.length; k++) {//判断样本点属于哪个类簇
	                    double eucDistance=dataPoints.get(j).testEuclideanDistance(clusters[k].medoid);
	                    if(eucDistance<minDistance){
	                        minDistance=eucDistance;
	                        clusterIndex=k;
	                    }
	                }
	               //将该样本点添加到该类簇
	                clusters[clusterIndex].addDataPoint(dataPoints.get(j));
	            }
	            for(int m = 0; m < clusters.length; m++){
	                clusters[m].medoid.calcMedoid();//重新计算各类簇的质点
	            }
	            for(int i=0;i<medoids.length;i++){
	                for(int j=0;j<this.dimNum;j++){
	                    oldMedoids[i][j]=newMedoids[i][j];
	                }
	            }
	            for(int n=0;n<clusters.length;n++){
	                newMedoids[n]=clusters[n].medoid.dimension;
	            }
	            this.miter++;
	        }


	    }

	    private void setInitialMedoids(double[][] medoids) {
	        for (int n = 0; n < clusters.length; n++) {
	            Medoid medoid = new Medoid(medoids[n]);
	            clusters[n].medoid = medoid;
	            medoid.cluster = clusters[n];
	        }
	    }

	   
	    private boolean isEqual(double[][] oldMedoids,double[][] newMedoids){
	        boolean flag=false;
	        for(int i=0;i<oldMedoids.length;i++){
	            for(int j=0;j<oldMedoids[i].length;j++){
	                if(oldMedoids[i][j]!=newMedoids[i][j]){
	                    return flag;
	                }
	            }
	        }
	        flag=true;
	        return flag;
	    }
	}

	public static void main (String args[]){
        ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();
        double[] a={2,3};
        double[] b={2,4};
        double[] c={1,4};
        double[] d={1,3};
        double[] e={2,2};
        double[] f={3,2};

        double[] g={8,7};
        double[] h={8,6};
        double[] i={7,7};
        double[] j={7,6};
        double[] k={8,5};

        double[] l={100,2};//孤立点

        double[] m={8,20};
        double[] n={8,19};
        double[] o={7,18};
        double[] p={7,17};
        double[] q={7,20};

        dataPoints.add(new DataPoint(a,"a"));
        dataPoints.add(new DataPoint(b,"b"));
        dataPoints.add(new DataPoint(c,"c"));
        dataPoints.add(new DataPoint(d,"d"));
        dataPoints.add(new DataPoint(e,"e"));
        dataPoints.add(new DataPoint(f,"f"));

        dataPoints.add(new DataPoint(g,"g"));
        dataPoints.add(new DataPoint(h,"h"));
        dataPoints.add(new DataPoint(i,"i"));
        dataPoints.add(new DataPoint(j,"j"));
        dataPoints.add(new DataPoint(k,"k"));

        dataPoints.add(new DataPoint(l,"l"));

        dataPoints.add(new DataPoint(m,"m"));
        dataPoints.add(new DataPoint(n,"n"));
        dataPoints.add(new DataPoint(o,"o"));
        dataPoints.add(new DataPoint(p,"p"));
        dataPoints.add(new DataPoint(q,"q"));

        ClusterAnalysis ca=new ClusterAnalysis(3,0,dataPoints,2);
       double[][] cen={{8,7},{8,6},{7,7}};
       ca.startAnalysis(cen);

      ArrayList<DataPoint>[] v = ca.getClusterOutput();
        for (int ii=0; ii<v.length; ii++){
            ArrayList tempV = v[ii];
            System.out.println("-----------Cluster"+ii+"---------");
            Iterator iter = tempV.iterator();
            while(iter.hasNext()){
                DataPoint dpTemp = (DataPoint)iter.next();
                System.out.println(dpTemp.pointName);
            }
        }
    }
}
//参考网址：http://lib.csdn.net/article/machinelearning/34246
