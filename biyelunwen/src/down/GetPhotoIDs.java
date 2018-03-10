package down;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import myutil.DateUtil;
import myutil.DateUtil.DateField;
import myutil.StringUtil;
import myutil.fileprocess.FileUtil;
import myutil.multithreads.MyThread;
import myutil.multithreads.ProcessUrl;
import myutil.net.HttpHelper;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.SearchParameters;

public class GetPhotoIDs
{
	public static String minimum_longitude = "115.2";
	public static String minimum_latitude = "39.2";
	public static String maximum_longitude = "117.4";
	public static String maximum_latitude = "41.6";
	
	public static int PerPage = 250;
	
	public static SearchParameters getSearchParameters(Date start, Date end) {
		SearchParameters searchParameters = new SearchParameters();
        
        searchParameters.setHasGeo(true);
        searchParameters.setAccuracy(com.flickr4java.flickr.Flickr.ACCURACY_STREET);
        searchParameters.setBBox(minimum_longitude, minimum_latitude, maximum_longitude, maximum_latitude);
        
        searchParameters.setMinTakenDate(start);
        searchParameters.setMaxTakenDate(end);
        searchParameters.setSort(com.flickr4java.flickr.photos.SearchParameters.DATE_TAKEN_ASC);
        
        return searchParameters;
	}
	
	public static PhotoList<Photo> getPhotos(Date start, Date end, int page) {
		Flickr flickr = new Flickr(DownPhotoInfo.apiKey, DownPhotoInfo.apiSecret, new REST());
		PhotosInterface iface = flickr.getPhotosInterface();
        PhotoList<Photo> temp = null;
		try
		{
			temp = iface.search(getSearchParameters(start, end), -1, page);
		} catch (Exception e)
		{
			System.out.println("get page failed. " + start.toString() + ", " + end.toString());
			try
			{
				Thread.sleep(3000);
			} catch (InterruptedException e1)
			{
				e1.printStackTrace();
			}
			System.out.println(e);
		}
        return temp;
	}
	
	public static void SavePhotos(String savePath, PhotoList<Photo> photos) {
		List<String> ids = new ArrayList<String>();
        for (int i = 0; i < photos.size(); i += 1) {
        	ids.add(photos.get(i).getId());
        }
        FileUtil.NewFile(savePath, ids);
	}
	
	public static void checkAndGetPhotos(String savePath, Date start, Date end, int page) {
		if (FileUtil.checkExist(savePath)) {
			return;
		}
		PhotoList<Photo> temp;
		temp = getPhotos(start, end, page);
		if (null == temp) {
			return;
		}
		SavePhotos(savePath, temp);
	}
	
	public static void DownloadOneDay(String key, String start, String end) throws FlickrException {
		String keyFile = entity.Photo.photoTempDir + key + ".txt";
		String prefix = entity.Photo.photoIDsDir + key + "_";
		Date startDate = DateUtil.getDate(start, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		Date endDate = DateUtil.getDate(end, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

	//	System.out.println(start + ", " + start.toString());
	//	System.out.println(end + ", " + end.toString());
		int pages = 0;
		if (FileUtil.checkExist(keyFile)) {
			List<String> lines = FileUtil.getLinesFromFile(keyFile);
			pages = Integer.parseInt(lines.get(0));
		} else {
	        PhotoList<Photo> temp = getPhotos(startDate, endDate, -1);
	        if (null == temp) {
	        	return;
	        }
	        List<String> info = new ArrayList<String>();
	        pages = temp.getPages();
	        info.add(temp.getPages() + "");
	        info.add(temp.getTotal() + "");
	        info.add(temp.getPerPage() + "");
	        FileUtil.NewFile(keyFile, info);
	        SavePhotos(prefix + "1.txt", temp);
		}
		for (int i = 2; i <= pages; i += 1) {
			checkAndGetPhotos(prefix + i + ".txt", startDate, endDate, i);
		}
	}
	
	public static void testDownloadOneDay() throws FlickrException {
		String day = "2017-10-12";
		String start = "2017-10-10" + " 00:00:00";
		String end = "2017-10-31" + " 23:59:59";
		Date date = DateUtil.getDate(day, DateUtil.sdfDay);
		DownloadOneDay(day, start, end);
	}
	
	public static List<String> getDaysStr() {
		Date date = DateUtil.getDate("2007-12-31", DateUtil.sdfDay);
		Date now = new Date();
		List<String> days = new ArrayList<String>();
		while (true) {
			date = DateUtil.dateUpdate(date, DateField.dayOfYear, 1);
			if (DateUtil.getDateDisHour(date, now) > 0) {
				break;
			}
			days.add(DateUtil.sdfDay.format(date));
		}
		return days;
	}

	public static void downPhotosMultiThreads() throws FlickrException {
		List<String> ids= getDaysStr();
		System.out.println("需要下载" + ids.size());
		MyThread.processMultiStage(ids, new ProcessUrl(){
			@Override
			public void ProcessUrl(String url)
			{
			//	System.out.println(url);
			//	return;
				String start = url + " 00:00:00";
				String end = url + " 23:59:59";
				try
				{
					DownloadOneDay(url, start, end);
				} catch (FlickrException e)
				{
					System.out.println("download " + url + "failed");
					System.out.println(e);
				}
			}
		});
	}
	
	public static void downPhotosOneThreads() throws FlickrException {
		List<String> ids= getDaysStr();
		System.out.println("需要下载" + ids.size());
		for (int i = 0; i < ids.size(); i += 1) {
			String url = ids.get(i);
			String start = url + " 00:00:00";
			String end = url + " 23:59:59";
			DownloadOneDay(url, start, end);
		}
	}

	//合并一个文件夹里面的id
	public static void mergePhotoIds(){
		List<String> id1= FileUtil.getFolderFilesLines(entity.Photo.photoIDsDir);
		List<String> id2=StringUtil.getUnique(id1);
		FileUtil.NewFile(entity.Photo.pidPath, id2);
		System.out.println(id1.size() + " -> " + id2.size());
	}

	public static void main(String[] args) throws FlickrException, ParseException
	{
	//	downPhotosMultiThreads();
	//	testDownloadOneDay();
		mergePhotoIds();
	}
}