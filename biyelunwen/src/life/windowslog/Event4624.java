package life.windowslog;

public class Event4624 extends EventLog
{
	public int logonType;
	public String logonTypeString;
	
	public static String eventDescription="用户登录";

	public Event4624(String[] content)
	{
		super(content);
	}

	@Override
	public String getMessage()
	{
		String temp=this.getBasicString()+eventDescription;
		temp+="：--->"+this.logonTypeString+"("+this.logonType+")";
		return temp;
	}
	
	@Override
	public boolean isSelected(){
		if(this.logonType==5)
			return false;
		return true;
	}
	
	public void setLogonTypeString(){
		String temp="";
		switch(this.logonType){
		case 0:
			temp="系统启动"+EventLog.getStartupString();
			break;
		case 2:
			temp="本地键盘显示器交互式登录";
			break;
		case 3:
			temp="网络登录，多为从网络中的其他位置连接到此计算机的共享文件夹，我电脑开机的时候也有";
			break;
		case 5:
			temp="服务启动";
			break;
		case 10:
			temp="远程桌面、终端登录";
			break;
			default:
				temp="未知";
		}
		this.logonTypeString=temp;
	}

	@Override
	public void setSelfContent()
	{
		String[] ss=this.description.split("\n");
		for(String s: ss){
			if(s.startsWith("登录类型")){
				this.logonType=	Integer.parseInt(s.substring(s.indexOf(":")+1).trim());
				this.setLogonTypeString();
				break;
			}
		}
	}

}
