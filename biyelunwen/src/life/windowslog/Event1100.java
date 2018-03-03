package life.windowslog;

public class Event1100 extends EventLog
{
	public static String eventDescription="事件日志服务关闭"+EventLog.getShutdownString();

	public Event1100(String[] content)
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
