package life.windowslog;

public class Event4672 extends EventLog
{
	public static String eventDescription="为用户分配管理员权限";

	public Event4672(String[] content)
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
	
	@Override
	public boolean isSelected(){
		return false;
	}

}
