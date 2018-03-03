package down;

import java.util.List;

import myutil.multithreads.MyThread;
import myutil.net.HttpHelper;
import myutil.net.tuchong.TuChongUser;
import entity.Photo;

public class DownXML extends MyThread
{
	public DownXML(int idPar, List<String> urispar){
		super(idPar, urispar);
	}
	
	public static void downTuChongShouYe(String url){
		TuChongUser tcu=new TuChongUser(url);
		tcu.getShouYe();
	}

	@Override
	public void mainFunc(String uri)
	{
		// TODO Auto-generated method stub
		
	}
}
