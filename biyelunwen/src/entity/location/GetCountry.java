package entity.location;

import java.util.ArrayList;
import java.util.List;

import myutil.fileprocess.FileUtil;

public abstract class GetCountry
{
	protected GetCountry next;
	
	public static GetCountry getCountry=new GetLast(GetLast.countryMaps);
	public static GetCountry getWordPart=new GetLast(GetLast.worldPartMaps);
	
	public GetCountry(){
		this.next=null;
	}
	
	public void setNext(GetCountry gc){
		this.next=gc;
	}
	
	abstract String getCountryInner(String content);
	
	public String getCountry(String content){
		String temp=this.getCountryInner(content);
		if(null==temp && null!=this.next)
			temp=this.next.getCountry(content);
		return temp;
	}
	
	public static class GetLast extends GetCountry{

		public static List<MultiToOne> countryMaps=null;
		public static List<MultiToOne> worldPartMaps=null;
		static{
			countryMaps=MultiToOne.getMulti("./src/entity/location/countrymap.txt");
			worldPartMaps=MultiToOne.getMulti("./src/entity/location/worldpart.txt");
			
		//	for(MultiToOne mto: countryMaps)
			//	System.out.println(mto);
		//	for(MultiToOne mto: worldPartMaps)
		//		System.out.println(mto);
		}
		
		public List<MultiToOne> maps=null;
		
		public GetLast(List<MultiToOne> mapsPar){
			this.maps=mapsPar;
		}
		
		@Override
		String getCountryInner(String content)
		{
			if(null==content)
				return "未知";
			int index=content.lastIndexOf(",");
			if(-1<index)
				index+=1;
			else
				index=0;
			String location=content.substring(index).trim();
	//		System.out.println(content+"\t\t"+location);
			if(location.matches("[a-zA-Z]+"))//全是英文字母，且至少一个
				location=location.toLowerCase();
			String result=null;
			for(int i=0; i<this.maps.size(); ++i){
				result=this.maps.get(i).getValue(location);
				if(null!=result)//如果找到则退出
					break;
			}
			return result;
		}
	}
	
	public static class MultiToOne{
		List<String> keys;
		String value;
		public MultiToOne(String valuePar, List<String> keysPar){
			this.keys=keysPar;
			this.value=valuePar;
		}
		
		public MultiToOne(List<String> keysPar, String valuePar){
			this.keys=keysPar;
			this.value=valuePar;
		}
		
		public String getValue(String key){
			for(int i=0; i<this.keys.size(); ++i){
				if(this.keys.get(i).equals(key))
					return value;
			}
			return null;
		}
		
		@Override
		public String toString(){
			StringBuilder sb=new StringBuilder(this.value);
			for(int i=0; i<this.keys.size(); ++i){
				sb.append(","+this.keys.get(i));
			}
			return sb.toString();
		}
		
		public static List<MultiToOne> getMulti(String path){
			List<String> lines=FileUtil.getLinesFromFile(path);
			List<MultiToOne> maps=new ArrayList<MultiToOne>();
			for(String line: lines){
				String[] ss=line.split(",");
				List<String> keys=new ArrayList<String>();
				for(int i=1; i<ss.length; ++i)//这里从1开始
					keys.add(ss[i]);
				maps.add(new MultiToOne(ss[0], keys));
			}
			return maps;
		}
	}
}
