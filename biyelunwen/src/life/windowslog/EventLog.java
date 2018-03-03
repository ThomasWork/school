package life.windowslog;

import java.util.Date;
import java.util.List;

import myutil.DateUtil;
import myutil.fileprocess.FileUtil;

public abstract class EventLog
{
	public int eventId;
	public Date datetime;
	public String description;
	
	public EventLog(String[] content){
		this.datetime=DateUtil.getDate(content[1], DateUtil.sdfLeft);
		this.eventId=Integer.parseInt(content[3]);
		this.description=content[5];
		this.setSelfContent();
	}
	
	//获取需要的信息
	public abstract String getMessage();
	//设置自己的信息
	public abstract void setSelfContent();
	
	public boolean isSelected(){
		return true;
	}
	
	public String getBasicString(){
		String temp=DateUtil.sdfHMS.format(this.datetime)+":";//+","+this.eventId;
		return temp;
	}
	
	public static String getShutdownString(){
		return "------------关机";
	}
	
	public static String getStartupString(){
		return "------------开机";
	}
	
	public static EventLog getEventLog(int id, String[] ss){
		EventLog el=null;
		switch(id){
		case 1100:
			el=new Event1100(ss);
			break;
		case 4647:
			el=new Event4647(ss);
			break;
		case 4672:
			el=new Event4672(ss);
			break;
		case 4624:
			el=new Event4624(ss);
			break;
		case 5024:
			el=new Event5024(ss);
			break;
		case 5033:
			el=new Event5033(ss);
			break;
		case 5056:
			el=new Event5056(ss);
			break;
		case 4648:
			el=new Event4648(ss);
			break;
		case 4902:
			el=new Event4902(ss);
			break;
		case 4608:
			el=new Event4608(ss);
			break;
			default:
		}
		return el;
	}

	public static void test1(){
		List<String[]> lines=FileUtil.ReadCSV("C:/Users/Admin/Desktop/11allday.csv", true);
		for(int i=lines.size()-1; i>=0; --i){
			String[] ss=lines.get(i);
			int id=Integer.parseInt(ss[3]);
			EventLog el=getEventLog(id, ss);
			if(el.isSelected())
				System.out.println(el.getMessage());
		}
	}
	
	public static void main(String[] args)
	{
		test1();
	}

}
