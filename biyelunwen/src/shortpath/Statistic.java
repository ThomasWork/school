package shortpath;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import shortpath.Path.PathType;

public class Statistic
{
	public static void showPathTypeMap(Map<PathType, List<Path>> map){
		for(Entry<PathType, List<Path>> entry: map.entrySet()){
			System.out.println(entry.getKey().toString()+":"+entry.getValue().size());
		}
	}
	
	public static void test1(){
		String path="C:/Users/Admin/Desktop/netlength/facebook/out - 副本.txt";
	//	path="C:/Users/Admin/Desktop/netlength/ca-GrQc/out - 副本.txt";
		List<Path> paths=Path.loadPath(path);
		System.out.println(paths.size());
		Map<PathType, List<Path>> map=Path.getTypeMap(paths);
		showPathTypeMap(map);
		Path.getAverageReachToReach(map.get(PathType.reachToReach));
	}
	

	public static void main(String[] args)
	{
		test1();
	}

}
