package trajectory;

import java.util.ArrayList;
import java.util.List;

import sciencecluster.MyPoint;
import entity.Photo;

public class GeoTrajectoryPhoto
{
	public String id="";
	public List<Photo> photos;
	
	public GeoTrajectoryPhoto(String idPar, List<Photo> ps){
		this.id=idPar;
		this.photos=ps;
	}
	
	//在这里更新了数据点的用户id，因为不想改变照片的用户id
	public GeoTrajectory getGeoTrajectory(){
		List<MyPointWithTime> mpts=new ArrayList<MyPointWithTime>();
		for(Photo p: this.photos){
			MyPointWithTime mpt=new MyPointWithTime(p);
			mpt.point.userId=this.id;
			mpts.add(mpt);
		}
		return new GeoTrajectory(this.id, mpts);
	}
	
	public static List<GeoTrajectory> getGeoTrajectories(List<GeoTrajectoryPhoto> tps){
		List<GeoTrajectory> tras=new ArrayList<GeoTrajectory>();
		for(GeoTrajectoryPhoto tp: tps){
			tras.add(tp.getGeoTrajectory());
		}
		return tras;
	}
	
	@Override
	public String toString(){
		StringBuilder sb=new StringBuilder(this.id+"\n");
		for(int i=0; i<this.photos.size(); ++i){
			Photo mp=this.photos.get(i);
			sb.append(","+mp.userId+","+mp.longitude+","+mp.latitude);
		}
		return sb.toString();
	}
	
	public static void main(String[] args){
	//	test1();
	//	getKeyTrajectory(GeoFilter.areaBeijing);
	//	testGetTrajectorysFromPhotosList();//对函数进行测试
	}
}
