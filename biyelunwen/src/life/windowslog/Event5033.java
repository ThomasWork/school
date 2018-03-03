package life.windowslog;

public class Event5033 extends EventLog
{
	public static String eventDescription="防火墙驱动程序成功启动";

	public Event5033(String[] content)
	{
		super(content);
		// TODO Auto-generated constructor stub
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
