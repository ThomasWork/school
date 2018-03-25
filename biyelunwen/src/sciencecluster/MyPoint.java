package sciencecluster;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import myutil.StringUtil;
import myutil.fileprocess.FileUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MyPoint
{
	public int pointId;
	public String userId;
	public String label;
	public String filePath;
	public String clusterId;
	public boolean isClusterCenter=false;
	public double x;//东经西经
	public double y;//南纬北纬
	public double localDensity;
	public double clusterMaxDensity;//所属聚类中的最大密度
	public int clusterPointNumber;//所属聚类中点的数量
	public GeoBlock geoBlock;
	public String geoBlockIndexString;//用来保存在geoBlock中的下标
	private double pointWeight=1.0;//不加可见性表示对本包可见
	
	public static double sameUserDiscount=0.0;//统一用户
	
	public static MyPointDistance mpd=null;//new MyPointDegreeDistance();
//	private static MyPointDistance mpd=new MyPointCoordinateDistance();
	public static MyPointWeight mpw=null;
		
	public MyPoint()
	{
	}
	
	public MyPoint(double xPar, double yPar){
		this.setXYPar(xPar, yPar);
	}
	public MyPoint(double xPar, double yPar, String userIdStr){
		this.setXYPar(xPar, yPar);
		this.userId=userIdStr;
	}
	
	public MyPoint(String labelPar, double xPar, double yPar, double weight)
	{
		this.label=labelPar;
		this.setXYPar(xPar, yPar);
		this.pointWeight=weight;
	}
	
	public void setPointWeight(double weight){
		this.pointWeight=weight;
	}
	
	public double getPointWeight() {
		return this.pointWeight;
	}
	
	private void setXYPar(double xPar, double yPar){
		this.x=xPar;
		this.y=yPar;
	}
	
	public double getDistance(MyPoint b)
	{
	//	System.out.println(MyPoint.mpd+",,,");
		return MyPoint.mpd.getPointDistance(this, b);
	}
	
	public String getBox() {
		double xStart = GeoBlock.LEFT + GeoBlock.COLUMN_WIDTH * x;
		double yStart = GeoBlock.TOP + GeoBlock.ROW_HEIGHT * y;
		double xMax = xStart + GeoBlock.COLUMN_WIDTH;
		double yMax = yStart + GeoBlock.ROW_HEIGHT;
		return xStart + "," + xMax + "," + yStart + "," + yMax;
	}
	
	@Override
	public String toString()
	{
		String temp="label:";
		temp += this.label + ",\tcordi:"+this.x+",\t"+this.y;
		if(null==MyPoint.mpw)
			MyPoint.mpw = new MyPoint.MyPointSelfWeight();
		temp+=",\t"+MyPoint.mpw.getPointWeight(this) + ",\t" + getBox();
		return temp;
	}
	
	public static MyPoint getAverageValue(List<MyPoint> mps) {
		double x = 0, y = 0;
		int count = mps.size();
		for (MyPoint mp: mps) {
			x += mp.x;
			y += mp.y;
		}
		x /= count;
		y /= count;
		return new MyPoint(x, y);
	}
	
	public static void setUseBlock(){
		MyPoint.mpd=new MyPoint.MyPointBlockDistance();
		MyPoint.mpw=new MyPoint.MyPointBlockWeight();
	}
	
	public static List<MyPoint> getPoints(String path)
	{
		List<MyPoint> points=new ArrayList<MyPoint>();
		List<String> lines=FileUtil.getLinesFromFile(path);
		for(String line: lines)
		{
			String[] ss=line.split(",");
			points.add(new MyPoint(ss[0], Double.parseDouble(ss[1]), Double.parseDouble(ss[2]), Double.parseDouble(ss[3])));
		}
		return points;
	}
	
	//从标注的kml文件中读取信息
	public static void loadTagFile()
	{
		String path="C:/Users/Admin/Desktop/tag.kml";
		try
		{
			Document doc=Jsoup.parse(new File(path), "utf-8");
		//	System.out.println(doc);
			Elements elems=doc.select("placemark");
			for(Element elem: elems)
			{
				String name=elem.select("name").first().text();
				System.out.println(name);
				String coordinates=elem.select("coordinates").first().text();
				System.out.println(coordinates);
				String description=elem.select("description").first().text();
				System.out.println(description+"\n");
			}
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public static class MyPointGPSDistance implements MyPointDistance {
		private static double EARTH_RADIUS = 6378137;//地球半径，单位为米
		public MyPointGPSDistance(){
			System.out.println("使用经纬度计算距离");
		}
		
		@Override
		public double getPointDistance(MyPoint a, MyPoint b)
		{
			return getDistance(a.x, a.y, b.x, b.y);
		}
		
		public static double getMyPointDistance(MyPoint a, MyPoint b)
		{
			return getDistance(a.x, a.y, b.x, b.y);
		}
	
		public static double getDistance(double long1, double lat1, double long2, double lat2)
		{
	//		System.out.println(long1+","+long2+","+lat1+","+lat2);
		   double radLat1 = rad(lat1);
		   double radLat2 = rad(lat2);
		   double a = radLat1 - radLat2;
		   double b = rad(long1) - rad(long2);
	
		   double s = 2 * Math.asin(
				   Math.sqrt(
						   Math.pow(Math.sin(a/2),2) +
						   Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)
						   )
						   );
		   s = s * EARTH_RADIUS;
		//   s = Math.round(s * 10000) / 10000;
		   return s;
		}
		
		private static double rad(double d)
		{
		   return d * Math.PI / 180.0;
		}
	}
	
	public static class MyPointKnownDistance implements MyPointDistance{
		public DistanceMatrix distance;
		
		public MyPointKnownDistance(String path){
			System.out.println("使用已知距离");
			this.distance=new DistanceMatrix(path);
		}
		
		public MyPointKnownDistance(DistanceMatrix dmPar){
			System.out.println("使用已知距离");
			this.distance=dmPar;
		}

		@Override
		public double getPointDistance(MyPoint a, MyPoint b)
		{
			return this.distance.getDistance(a.pointId, b.pointId);
		}		
	}

	public static class MyPointCoordinateDistance implements MyPointDistance{
	
		public MyPointCoordinateDistance(){
			System.out.println("使用坐标距离！");
		}
		@Override
		public double getPointDistance(MyPoint a, MyPoint b)
		{
			double dx = a.x-b.x;
			double dy = a.y-b.y;
			return Math.sqrt(dx*dx+dy*dy);
		}
	}
	
	public static class MyPointCoordinateDistanceWithMax implements MyPointDistance{

		public double maxDis;
		
		public MyPointCoordinateDistanceWithMax(double maxDisPar){
			this.maxDis=maxDisPar;
			System.out.println("使用带有最大值的坐标距离！");
		}
		@Override
		public double getPointDistance(MyPoint a, MyPoint b)
		{
			double dx=a.x-b.x;
			double dy=a.y-b.y;
			double temp = Math.sqrt(dx*dx+dy*dy);
			if(temp>this.maxDis)
				temp=maxDis;
			return temp;
		}
		
	}
	
	public static class MyPointBlockDistance implements MyPointDistance{
		public MyPointBlockDistance(){
			System.out.println("使用栅格距离！");
		}

		@Override
		public double getPointDistance(MyPoint a, MyPoint b)
		{
			double dx=a.geoBlock.xIndex-b.geoBlock.xIndex;
			double dy=a.geoBlock.yIndex-b.geoBlock.yIndex;
			return Math.sqrt(dx*dx+dy*dy);
		}
		
		
	}

	interface MyPointWeight{
		public abstract double getPointWeight(MyPoint p);
	}
	
	public static class MyPointSelfWeight implements MyPointWeight{
	
		public MyPointSelfWeight(){
			System.out.println("计算数据点权重时使用自身的权重");
		}
		
		@Override
		public double getPointWeight(MyPoint p)
		{
			return p.pointWeight;
		}		
	}
	
	public static class MyPoint1Weight implements MyPointWeight{

		public MyPoint1Weight(){
			System.out.println("数据点权重为1");
		}
		
		@Override
		public double getPointWeight(MyPoint p)
		{
			return 1.0;
		}
		
	}
	
	public static class MyPointBlockWeight implements MyPointWeight{

		public MyPointBlockWeight(){
			System.out.println("使用栅格权重");
		}
		
		@Override
		public double getPointWeight(MyPoint p)
		{
			// TODO Auto-generated method stub
			return 0;
		}
	}
}

interface MyPointDistance{
	public abstract double getPointDistance(MyPoint a, MyPoint b);
}


