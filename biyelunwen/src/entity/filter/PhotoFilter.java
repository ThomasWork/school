package entity.filter;

import java.util.ArrayList;
import java.util.List;

import entity.Photo;
import entity.filter.TimeFilter.TimeType;

/**
 * 这个类的主要作用是用职责链对类进行过滤
 * @author Admin
 *
 */

public abstract class PhotoFilter
{
	protected PhotoFilter nextFilter;
	
	public PhotoFilter(){
		this.nextFilter = null;
	}
	
	protected abstract boolean isValid(Photo p);
	
	public void setNextFilter(PhotoFilter pf){
		this.nextFilter=pf;
	}
	
	public boolean getValid(Photo p){
		if(this.isValid(p)) {//如果本过滤器通过
			if(null == this.nextFilter)//如果没有后续判断
				return true;
			else
				return this.nextFilter.isValid(p);//进行后续判断
		}
		return false;
	}
	
	public static PhotoFilter getFilter(TimeType tt, GeoFilter.Area area){
		PhotoFilter time = new TimeFilter(tt);
		PhotoFilter geo = new GeoFilter(area);
		geo.setNextFilter(time);
	//	time.setNextFilter(geo);
	//	return time;
		return geo;
	}
	
	public static PhotoFilter getFilter(TimeType tt){
		PhotoFilter time=new TimeFilter(tt);
		return time;
	}
	
	public static List<Photo> getFilteredPhotos(PhotoFilter pf){
		List<Photo> photos = Photo.getPhotos(Photo.photoBasicInfoPath);
		return filterPhotos(photos, pf);
	}
	
	public static List<Photo> filterPhotos(List<Photo> photos, PhotoFilter pf){
		List<Photo> selected=new ArrayList<Photo>();
		for(Photo p: photos){
			if(pf.getValid(p))
			//	System.out.println(p.dateTaken);
				selected.add(p);
		}
		System.out.println("从"+photos.size()+"中过滤出："+selected.size()+"张照片");
		return selected;
	}
	
	public static void getPhotosTest1(){
		TimeFilter.TimeType tt = new TimeFilter.HourFilter(20, 12);
	//	tt=TimeFilter.allDay;
		PhotoFilter pf=getFilter(tt, GeoFilter.areaGuGongNeiBu);
		List<Photo> photos=getFilteredPhotos(pf);
		System.out.println(photos.size());
	}
	
	public static void getPhotosTest(){
		List<Photo> input = GeoFilter.getAreaPhotos(GeoFilter.areaGuGongNeiBu);
	//	input=Photo.getPhotos();
		System.out.println("size:"+input.size());
		for(int i=23; i<24; ++i){
			TimeFilter.TimeType tt=new TimeFilter.HourFilter(i, i);
			TimeFilter tf=new TimeFilter(tt);
			List<Photo> get=TimeFilter.filterPhoto(input, tf);
			System.out.println(get.size());	
			for(Photo p: get){
				System.out.println(p.id);
			}
		}
	}

	public static void main(String[] args)
	{
	//	getPhotos();
		getPhotosTest();
	}
}
