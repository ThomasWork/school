package entity.filter;

import java.util.ArrayList;
import java.util.List;

import trajectory.GeoTrajectory;
import trajectory.UserGeoTrajectory;
import entity.Photo;
import entity.User;

public class UserFilter
{
	//选择在区域中的用户
	public static List<User> selectUsers(GeoFilter.Area area){
		List<User> users=User.getUsersWithPhotos();
		List<User> selected=new ArrayList<User>();
		for(User u: users){
			u.selected=GeoFilter.getPhotosInArea(u.photosList, area);//选取在范围内的用户
			if(u.selected.size()<1)//如果数量太少则跳过
				continue;
			u.photosList=u.selected;
			selected.add(u);
		}
		System.out.println("选择之前的用户数："+users.size()+"\t选择之前的照片数："+ User.countUserPhotos(users)
				+ "\t选中用户数："+selected.size()+"\t选中用户照片数："+ User.countUserPhotos(selected));
		return selected;
	}
	
	public static List<User> getLocalUsersWithSourceLocation(){
		List<User> selected=new ArrayList<User>();
		List<User> users=User.getUsersWithPhotos();
		for(User u: users){
			if(!u.sourceLocation.equals("unknown"))
		//	if(u.isBeijing)
				selected.add(u);
		}
		return selected;
	}
	
	public static void testLocalUsers(){
		List<User> users= getLocalUsersWithSourceLocation();
		for(User u: users){
			List<GeoTrajectory> tras=UserGeoTrajectory.getTrajectorysFromUser(u, 24);
		}
	}
	
	public static List<Photo> getUsersPhotos(List<User> users){
		List<Photo> ps=new ArrayList<Photo>();
		for(User u: users)
			ps.addAll(u.photosList);
		System.out.println("从"+users.size()+"名用户中获得"+ps.size()+"张照片");
		return ps;
	}
	
	public static void main(String[] args){
	}
}
