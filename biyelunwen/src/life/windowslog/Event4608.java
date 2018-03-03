package life.windowslog;

public class Event4608 extends EventLog
{
	public static String eventDescription="系统正在启动"+EventLog.getStartupString();

	public Event4608(String[] content)
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
