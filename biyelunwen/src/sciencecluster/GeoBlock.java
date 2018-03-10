package sciencecluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import draw.KmlFile;
import myutil.StringUtil;
import myutil.fileprocess.FileUtil;
import entity.Photo;
import entity.filter.GeoFilter;


/**
 * 这个类的特点是使用栅格
 * @author Admin
 *
 */

public class GeoBlock
{
	public static double LEFT;
	public static double RIGHT;
	public static double TOP;//注意，这里TOP的数值比较小
	public static double BOTTOM;
	
	public static double WIDTH;
	public static double HEIGHT;
	
	public static int ROW_NUM;
	public static int COLUMN_NUM;
	public static double ROW_HEIGHT=0.5;//每一个块的高度
	public static double COLUMN_WIDTH=0.4;//每一个块的宽度
	
	public int xIndex;
	public int yIndex;
	public double xCoordinate;
	public double yCoordinate;
	public double weight;
	
	public GeoBlock(Map.Entry<String, Integer> entry)
	{
		String key=entry.getKey();
		this.weight=entry.getValue();
		String[] ss=key.split(",");
		this.xIndex=Integer.parseInt(ss[0]);
		this.yIndex=Integer.parseInt(ss[1]);
		this.setCoordinate();
	}
	
	public GeoBlock(Map.Entry<String, Set<String>> entry, String split){
		String key=entry.getKey();
		this.weight=entry.getValue().size();//这里使用的用户的个数
		String[] ss=key.split(split);
		this.xIndex=Integer.parseInt(ss[0]);
		this.yIndex=Integer.parseInt(ss[1]);
		this.setCoordinate();
	}

	
	//得到栅格的坐标，即中心点位置
	public void setCoordinate()
	{
		final double offset=0.5;
		this.xCoordinate=GeoBlock.LEFT+(this.xIndex+offset)*GeoBlock.COLUMN_WIDTH;
		this.yCoordinate=GeoBlock.TOP+(this.yIndex+offset)*GeoBlock.ROW_HEIGHT;
	}
	
	@Override
	public String toString()
	{
		String temp="";
		temp+="("
		+"["+this.xIndex+","+this.yIndex+"],"
		+"["+this.xCoordinate+","+this.yCoordinate+"],"
		+"["+this.weight+"]"+")";
		return temp;
	}
	
	public static void setParameter(int row, int column){
		GeoBlock.HEIGHT = GeoBlock.BOTTOM-GeoBlock.TOP;
		GeoBlock.WIDTH = GeoBlock.RIGHT-GeoBlock.LEFT;
		GeoBlock.ROW_NUM = row;
		GeoBlock.COLUMN_NUM = column;
		GeoBlock.ROW_HEIGHT = GeoBlock.HEIGHT/GeoBlock.ROW_NUM;
		GeoBlock.COLUMN_WIDTH = GeoBlock.WIDTH/GeoBlock.COLUMN_NUM;
	//	System.out.println("WIDTH:"+GeoBlock.WIDTH+"\tHEIGHT"+GeoBlock.HEIGHT);
	//	System.out.println("ROW:"+GeoBlock.ROW_NUM+"\tColumn:"+GeoBlock.COLUMN_NUM);
	//	System.out.println("width:"+GeoBlock.COLUMN_WIDTH+"\theight:"+GeoBlock.ROW_HEIGHT);
	}
	
	public static String getBlockIndex(double x, double y)//x表示横向上的距离(经度)，y表示纵向上的距离（纬度）
	{
		double xOffset=x-GeoBlock.LEFT;
		double yOffset=y-GeoBlock.TOP;//注意，这里减去的是较小的TOP值
		int xIndex=(int) (xOffset/COLUMN_WIDTH);
		if(xIndex>=COLUMN_NUM)
			xIndex=COLUMN_NUM-1;
		int yIndex=(int) (yOffset/ROW_HEIGHT);
		if(yIndex>=ROW_NUM)
			yIndex=ROW_NUM-1;
		String temp=xIndex+","+yIndex;
		return temp;
		
	}
	
	//根据数据点来设置栅格的参数
	public static void setStaticParameter(List<MyPoint> points, int row, int column){
		MyPoint first = points.get(0);
		GeoBlock.LEFT=first.x;
		GeoBlock.RIGHT=first.x;
		GeoBlock.TOP=first.y;
		GeoBlock.BOTTOM=first.y;
		for(MyPoint point: points){
			if(point.x<GeoBlock.LEFT)
				GeoBlock.LEFT=point.x;
			if(point.x>GeoBlock.RIGHT)
				GeoBlock.RIGHT=point.x;
			if(point.y<GeoBlock.TOP)
				GeoBlock.TOP=point.y;
			if(point.y>GeoBlock.BOTTOM)
				GeoBlock.BOTTOM=point.y;
		}
	//	System.out.println("Left:"+GeoBlock.LEFT+"\tRight:"+GeoBlock.RIGHT+"\tTop:"+GeoBlock.TOP+"\tBottom:"+GeoBlock.BOTTOM);
		GeoBlock.setParameter(row, column);
	}
	
	//根据数据点以及制定的行数和列数来获得栅格
	public static List<GeoBlock> getBlock(List<MyPoint> points, int row, int column){			
		
		List<String> tempList = GeoBlock.getBlockIndexes(points, row, column);
		List<GeoBlock> blocks=new ArrayList<GeoBlock>();
		
	/*	Map<String, Integer> blockmap=StringUtil.countFrequency(tempList);//这里是使用计数的方式来统计栅格的权重
		System.out.println("共有栅格："+blockmap.size());
		for(Map.Entry<String, Integer> entry: blockmap.entrySet())
		{
			GeoBlock tb=new GeoBlock(entry);
		//			System.out.println(tb);
		//	if(tb.weight>1)
			blocks.add(new GeoBlock(entry));
		}*/
		
		Map<String, Set<String>> blockWeight=new HashMap<String, Set<String>>();
		for(int i = 0; i < points.size(); ++i){
			MyPoint mp=points.get(i);
			Set<String> t=blockWeight.get(mp.geoBlockIndexString);
			if(null==t) {
				t=new HashSet<String>();
				blockWeight.put(mp.geoBlockIndexString, t);
			}
			t.add(mp.userId);
		}
		for(Map.Entry<String, Set<String>> entry: blockWeight.entrySet()){
			GeoBlock tb=new GeoBlock(entry, ",");
			blocks.add(tb);
		//	System.out.println(tb.weight);
		}
		System.out.println("删除一些之后："+blocks.size());
		return blocks;		
	}
	
	public static List<String> getBlockIndexes(List<MyPoint> points, int row, int column){
		setStaticParameter(points, row, column);
		
		List<String> tempList=new ArrayList<String>();
		for(MyPoint mp: points){
			String temp=GeoBlock.getBlockIndex(mp.x, mp.y);
			mp.geoBlockIndexString=temp;
			tempList.add(temp);
		}
		return tempList;
	}
	
	public static List<String> getBlockIndexesWithReadyParameters(List<MyPoint> points){
		List<String> tempList=new ArrayList<String>();
		for(MyPoint mp: points){
			String temp=GeoBlock.getBlockIndex(mp.x, mp.y);
			mp.geoBlockIndexString=temp;
			tempList.add(temp);
		}
		return tempList;
	}
	
	public static List<MyPoint> getMyPoints(List<GeoBlock> blocks){
		List<MyPoint> cps=new ArrayList<MyPoint>();
		for(GeoBlock gb: blocks){
			MyPoint mp=new MyPoint();
			mp.x=gb.xIndex;
			mp.y=gb.yIndex;
			mp.label=gb.xCoordinate+","+gb.yCoordinate;
			mp.setPointWeight(gb.weight);
			cps.add(mp);
		}
		return cps;
	}
	
	//进行测试
	public static void test1(){
		List<MyPoint> mps=new ArrayList<MyPoint>();
		mps.clear();
		mps.add(new MyPoint(0, 0, "1"));
		mps.add(new MyPoint(1, 1, "2"));
		mps.add(new MyPoint(1, 1, "2"));
		mps.add(new MyPoint(8, 8, "4"));
		mps.add(new MyPoint(9, 9, "5"));
		mps.add(new MyPoint(10, 10, "6"));
		List<GeoBlock> blocks=GeoBlock.getBlock(mps, 3, 3);
		for(int i=0; i<blocks.size(); ++i){
			System.out.println(blocks.get(i));
		}
	}
	
	//获得地理块的权重
	public static void draw3DHeight(){
		List<MyPoint> mps;
	//	mps = Photo.getPoints();//北京地区的数据点
		List<Photo> photos=GeoFilter.getAreaPhotos(GeoFilter.areaGuGongBig);
		System.out.println("当前选中的照片："+photos.size());
		mps=Photo.getPoints(photos);
		List<String> lines = getPointToBlockWeight(mps, 1275, 1062);
		FileUtil.NewFile("E:/MyProject/VS2010/Network_16_10_17/MyHeatmap/datanolog.txt", lines);
	}
	
	//根据数据点和行数以及列数获得栅格的权重
	public static List<String> getPointToBlockWeight(List<MyPoint> mps, int rowNum, int columnNum){
		List<GeoBlock> blocks=GeoBlock.getBlock(mps, rowNum, columnNum);
		List<String> lines=new ArrayList<String>();
		for(GeoBlock gb: blocks){
			String temp=gb.xIndex+","+gb.yIndex+","+gb.weight;//Math.log(gb.weight);
		//	System.out.println(temp);
			lines.add(temp);
		}
		return lines;
	}
	
	public static void main(String[] args)
	{
		test1();
	//	writeAreaPhotosGPS();
	//	draw3DHeight();
	}
}
