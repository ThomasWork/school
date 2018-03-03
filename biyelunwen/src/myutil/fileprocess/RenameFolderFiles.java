package myutil.fileprocess;

import java.io.File;
import java.util.List;

public abstract class RenameFolderFiles {

	abstract boolean isSelected(String name);
	abstract String getNewName(String init);
	
	public static String addLastString(String source, String add){
		if(!source.endsWith(add))
			source+=add;
		return source;
	}
	
	public static void test1(String folder, RenameFolderFiles rff){
		folder=addLastString(folder, "/");
		List<String> paths=FileUtil.getFolderFilesPath(folder);
		for(String path: paths){
		//	System.out.println(path);
			String name=path.substring(path.lastIndexOf("/")+1);
			if(rff.isSelected(name)){
				String newName=rff.getNewName(name);
				String newPath=folder+"new/"+newName;
				FileUtil.copyFile(path, newPath);
			}
		}
	}
	
	
	public static void main(String[] args) {
		test1("H:/单田芳/单田芳 小五义 全400回", new RenameFolderFiles(){

			@Override
			boolean isSelected(String name) {
				if(name.startsWith("[]"))
					return false;
				return true;
			}

			@Override
			String getNewName(String init) {
				return "[]小五义"+init;
			}
			
		});
	}

}
