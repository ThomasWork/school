package entity.filter;

import java.util.ArrayList;
import java.util.List;

import sciencecluster.MyPoint;
import entity.Photo;
import myutil.fileprocess.FileUtil;

public class GeoFilter extends PhotoFilter
{
//	public static Area areaGuGong=new Area(116.3854198791183, 116.3959208474785, 39.92299482093005, 39.91183241605992);//故宫
	public static Area areaBeijingLong = new Area("BeijingLong", 115.7, 117.4, 41.6, 39.4);
	public static Area areaBeijing = new Area("Beijing", 115.406, 117.502, 41.044, 39.434);
	public static Area areaBeijingNeibu = new Area("BeijingNeibu", 116.2227033532268, 116.6538594876797, 40.10000945896522, 39.86313944454666);
	public static Area areaGuGongBig = new Area("GuGongBig", 116.3840, 116.3970, 39.9230, 39.9110);
	public static Area areaGuGongNeiBu = new Area("GuGongNeiBu", 116.3866, 116.3950, 39.9208, 39.9126);
	public static Area areaYiHeYuan = new Area("YiHeYuan", 116.2567640662166, 116.278013963262, 40.00338408656999, 39.9802376172739);//颐和园
	public static Area areaWorld = new Area("world", -180, 180, 90, -90);
	
	static{
		areaBeijing.setClusterParameter(3400, 3400, 4, 20);
		areaBeijingNeibu.setClusterParameter(3400, 3400, 10, 10);
		areaGuGongNeiBu.setClusterParameter(340, 340, 10, 10);
		areaYiHeYuan.setClusterParameter(200, 200, 10, 10);
		areaWorld.setClusterParameter(1000, 2000, 5, 1);//赤道4万公里
	}
	
	public Area area;
	public GeoFilter(Area areaPar){
		this.area=areaPar;
		System.out.println("区域限制："+areaPar.name);
	}
	
	
	@Override
	protected boolean isValid(Photo p)
	{
		return GeoFilter.isPhotoInRect(p, this.area.left, this.area.right, this.area.top, this.area.bottom);
	}
	
	public static class Area {
		public double left, right, top, bottom;
		
		public String name;
		
		public int row;
		public int column;
		public int clusterR;
		public int clusterNum;
		
		public Area(String namePar, double leftPar, double rightPar, double topPar, double bottomPar){
			this.name=namePar;
			this.left=leftPar;
			this.right=rightPar;
			this.top=topPar;
			this.bottom=bottomPar;
		}
		
		public void setClusterParameter(int rowPar, int columnPar, int clusterRPar, int clusterNumPar)
		{
			this.row=rowPar;
			this.column=columnPar;
			this.clusterR=clusterRPar;
			this.clusterNum=clusterNumPar;
		}
		
		public void showClusterParameter(){
			System.out.println("范围高度："+this.getAreaHeight()+"\t范围高度："+
					this.getAreaWidth()+"\t栅格高度："+this.getAreaHeight()/row+"\t栅格宽度："+this.getAreaWidth()/column);
			System.out.println("聚类半径："+this.clusterR+"\t聚类个数："+this.clusterNum);
		}
		
		public double getAreaWidth(){
			MyPoint mp1=new MyPoint(this.left, this.top);//左上角
			MyPoint mp2=new MyPoint(this.right, this.top);//右上角
			return MyPoint.MyPointGPSDistance.getMyPointDistance(mp1, mp2);
		}
		
		public double getAreaHeight(){
			MyPoint mp1=new MyPoint(this.left, this.top);//左上角
			MyPoint mp2=new MyPoint(this.left, this.bottom);//左下角
			return MyPoint.MyPointGPSDistance.getMyPointDistance(mp1, mp2);
		}
	}
	
	public static boolean isPhotoInArea(Photo p, Area area) {
		return GeoFilter.isPhotoInRect(p, area.left, area.right, area.top, area.bottom);
	}

	public static boolean isPhotoInRect(Photo p, double left, double right, double top, double bottom){
		if(p.longitude >= left && p.longitude <= right && p.latitude >= bottom && p.latitude <= top)
			return true;
		return false;
	}
	//得到处于方框中的照片
	public static List<Photo> getPhotosInRect(List<Photo> source, double left, double right, double top, double bottom){
		List<Photo> photos=new ArrayList<Photo>();
		for(int i=0; i<source.size(); ++i){
			Photo p=source.get(i);
			if(isPhotoInRect(p, left, right, top, bottom))
				photos.add(p);
		}
		return photos;
	}
	
	public static List<Photo> getPhotosInArea(List<Photo> photos, Area a){
	//	System.out.println(a.left+","+a.right+","+a.top+","+a.bottom);
		System.out.println("photo num: " + photos.size() + ", filter: " + a.name);
		return GeoFilter.getPhotosInRect(photos, a.left, a.right, a.top, a.bottom);
	}
	
	public static void main(String[] args){
	}
}
