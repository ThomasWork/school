package life.windowslog;

public class Event5024 extends EventLog
{
	public static String eventDescription="防火墙服务成功启动";

	public Event5024(String[] content)
	{
		super(content);
	}

	@Override
	public String getMessage()
	{
		String temp=this.getBasicString()+eventDescription;
		return temp;
	}

	@Override
	public void setSelfContent()
	{
	}

}
