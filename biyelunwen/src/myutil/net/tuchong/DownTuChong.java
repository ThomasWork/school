package myutil.net.tuchong;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import myutil.multithreads.MyThread;
import myutil.multithreads.ProcessUrl;
import myutil.net.HttpHelper;

import org.jsoup.nodes.Document;

public class DownTuChong
{
	
	public static void getUsers(){
		Set<String> urls=new HashSet<String>();
		String first="https://shelltown.tuchong.com/";
		
		urls.add(first);
		
		
		Queue<String> q=new LinkedList<String>();
		
		q.offer(first);
		while(!q.isEmpty()){
			String cur=q.poll();
			TuChongUser tcu=new TuChongUser(cur);
			tcu.setFollowerUrl();
			tcu.setFollowingUrl();
			for(String url: tcu.followingUrl)//关注列表
			{
				if(!urls.contains(url))//如果没有包含
				{
					urls.add(url);
					q.offer(url);
				}
			}
			for(String url: tcu.followersUrl){
				if(!urls.contains(url))//如果没有包含
				{
					urls.add(url);
					q.offer(url);
				}
			}
			System.out.println("setSize:"+urls.size()+",queueSize:"+q.size());
		//	if(q.size()>300000)
		//		break;
		}
		List<String> downUrls=new ArrayList<String>(q);
		MyThread.processMultiStage(downUrls, new ProcessUrl(){
			@Override
			public void ProcessUrl(String url)
			{
			}			
		});
	}

	public static void main(String[] args)
	{
		getUsers();
	}

}
