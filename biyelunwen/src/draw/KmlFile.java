package draw;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import sciencecluster.MyPoint;
import trajectory.GeoTrajectory;
import trajectory.MyPointWithTime;
import entity.Photo;
import myutil.fileprocess.FileUtil;

public class KmlFile
{
	public static String saveFolder="G:/Flickr/kml/";
	
	public double latitude;
	public double longitude;
	public String label;
	public String file;
	public String description;
	
	public KmlFile(){
		
	}
	
	public KmlFile(double lon, double lat){
		this.longitude=lon;
		this.latitude=lat;
	}
	
	public static void writeKmlPath(List<List<KmlFile>> lines, String saveName){
		String savePath=KmlFile.saveFolder+saveName+".kml";
		String header="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+"\n"+"<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">"
				+"\n"+"<Document>"
				+"\n"+"<name>"+saveName+"</name>"
				+"\n"+"<Style id=\"s_ylw-pushpin_hl\">"
				+"\n"+"<IconStyle>"
				+"\n"+"<scale>1.3</scale>"
				+"\n"+"<Icon>"
				+"\n"+"<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"
				+"\n"+"</Icon>"
				+"\n"+"<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"
				+"\n"+"</IconStyle>"
				+"\n"+"<LineStyle>"
				+"\n"+"<color>7f0000ff</color>"//ff0000ff
				+"\n"+"</LineStyle>"
				+"\n"+"</Style>"
				+"\n"+"<StyleMap id=\"m_ylw-pushpin\">"
				+"\n"+"<Pair>"
				+"\n"+"<key>normal</key>"
				+"\n"+"<styleUrl>#s_ylw-pushpin</styleUrl>"
				+"\n"+"</Pair>"
				+"\n"+"<Pair>"
				+"\n"+"<key>highlight</key>"
				+"\n"+"<styleUrl>#s_ylw-pushpin_hl</styleUrl>"
				+"\n"+"</Pair>"
				+"\n"+"</StyleMap>"
				+"\n"+"<Style id=\"s_ylw-pushpin\">"
				+"\n"+"<IconStyle>"
				+"\n"+"<scale>1.1</scale>"
				+"\n"+"<Icon>"
				+"\n"+"<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"
				+"\n"+"</Icon>"
				+"\n"+"<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"
				+"\n"+"</IconStyle>"
				+"\n"+"<LineStyle>"
				+"\n"+"<color>ff0000ff</color>"
				+"\n"+"</LineStyle>"
				+"\n"+"</Style>"
				+"\n"+"<Folder>"
				+"\n"+"<name>路径测试</name>"
				+"\n"+"<open>1</open>";
		StringBuffer content=new StringBuffer();
		for(int i=0; i<lines.size(); ++i)
		{
			List<KmlFile> points=lines.get(i);
			String temp="<Placemark>"
					+ "\n"+"<name>"+"MyPlaces"+"</name><styleUrl>#m_ylw-pushpin</styleUrl>"
					+ "\n"+"<LineString><tessellate>1</tessellate><coordinates>";
			content.append(temp);
			for(int j=0; j<points.size(); ++j){
				content.append(points.get(j).longitude+","+points.get(j).latitude+","+"0 ");
			}
			temp="\n"+"</coordinates></LineString></Placemark>"
					+"\n";
			content.append(temp);
		//	System.out.println(img.path);
		}
		String body=header+content+"</Folder></Document></kml>";
		FileUtil.NewFile(savePath, body);
	}
	
	public static void writeKmlFile(List<KmlFile> places, String saveName){
		String savePath=KmlFile.saveFolder+saveName+".kml";
		String header="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+"\n"+"<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">"
				+"\n"+"<Document>"
				+"\n"+"<name>"+saveName+"</name>"
				+"\n"+"<Style id=\"s_ylw-pushpin_hl\">"
				+"\n"+"<IconStyle>"
				+"\n"+"<color>ff0000ff</color>"//选中的时候的颜色
				+"\n"+"<scale>2</scale>"//选中时候的大小
				+"\n"+"<Icon><href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href></Icon>"
				+"\n"+"<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"
				+"\n"+"</IconStyle><LabelStyle><color>ff7f00ff</color></LabelStyle></Style>"//标签的特点
				+"\n"+"	<Style id=\"s_ylw-pushpin\">"
						+ "\n"+"<IconStyle>"
						+ "\n"+"<color>ff0000ff</color>"//正常展示的颜色
						+ "\n"+"<Icon>"
						+ "\n"+"<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"
						+ "\n"+"</Icon>"
						+ "\n"+"<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"
								+ "\n"+"</IconStyle>"
								+ "\n"+"<LabelStyle>"
								+ "\n"+"<color>ff7f00ff</color>"
								+ "\n"+"</LabelStyle>"
								+ "\n"+"</Style>"
								+ "\n"+"<StyleMap id=\"m_ylw-pushpin\">"
								+ "\n"+"<Pair>"
								+ "\n"+"<key>normal</key>"
								+ "\n"+"<styleUrl>#s_ylw-pushpin</styleUrl>"
								+ "\n"+"</Pair>"
								+ "\n"+"<Pair>"
								+ "\n"+"<key>highlight</key>"
								+ "\n"+"<styleUrl>#s_ylw-pushpin_hl</styleUrl>"
								+ "\n"+"</Pair>"
								+ "\n"+"</StyleMap>";
		StringBuffer content=new StringBuffer();
		for(int i=0; i<places.size(); ++i)
		{
			KmlFile kf=places.get(i);
			String temp="<Placemark>"
					+ "\n"+"<name>"+kf.label+"</name>"
				//	+ "\n"+"<description><![CDATA[<img src=\"file:///"+photo.path+"\" height=\"400\" width=\"400\" />]]></description>"
					+ "\n"+"<description>"+kf.description+"</description>"
					+ "\n"+"<LookAt>"
					+ "\n"+"<longitude>"+kf.longitude+"</longitude>"
					+ "\n"+"<latitude>"+kf.latitude+"</latitude>"
					+ "\n"+"<altitude>0</altitude>"
					+ "\n"+"<heading>-6.282253870709508e-009</heading>"
					+ "\n"+"<tilt>44.90394537463443</tilt>"
					+ "\n"+"<range>1000.328551370916</range>"
					+ "\n"+"<gx:altitudeMode>relativeToSeaFloor</gx:altitudeMode>"
					+ "\n"+"</LookAt>"
					+ "\n"+"<styleUrl>#m_ylw-pushpin</styleUrl>"
					+ "\n"+"<Point>"
					+ "\n"+"<gx:drawOrder>1</gx:drawOrder>"
					+ "\n"+"<coordinates>"+kf.longitude+","+kf.latitude+",0</coordinates>"
					+ "\n"+"</Point>"
					+ "\n"+"</Placemark>"
					+"\n";
			content.append(temp);
		//	System.out.println(img.path);
		}
		String body=header+content+"</Document></kml>";
	//	System.out.println(body);
		FileUtil.NewFile(savePath, body);
	}
	
	public static void writeClusterResult(String saveName, List<MyPoint> mps){
		List<KmlFile> kfs=new ArrayList<KmlFile>();
		for(MyPoint p: mps){
			KmlFile kf=new KmlFile();
			kf.label=p.clusterId;
			String [] ss=p.label.split(",");
			kf.longitude=Double.parseDouble(ss[0]);
			kf.latitude=Double.parseDouble(ss[1]);
			kf.description=p.x+","+p.y;
			kfs.add(kf);
		}
		KmlFile.writeKmlFile(kfs, saveName);
	}
	
	public static void writeMyPoint(String saveName, List<MyPoint> mps){
		List<KmlFile> kfs=getKmlPointsFromMyPoints(mps);
		KmlFile.writeKmlFile(kfs, saveName);
	}
	
	public static List<KmlFile> getKmlPointsFromMyPoints(List<MyPoint> mps){
		List<KmlFile> kfs=new ArrayList<KmlFile>();
		for(MyPoint p: mps){
			KmlFile kf=new KmlFile();
			kf.label=p.label;
			kf.longitude=p.x;
			kf.latitude=p.y;
			kf.description="";
			kfs.add(kf);
		}
		return kfs;
	}
	
	public static void writeTrajectories(List<GeoTrajectory> tras, String saveName){
		List<List<KmlFile>> lines=new ArrayList<List<KmlFile>>();
		for(GeoTrajectory tra: tras){
			lines.add(KmlFile.getKmlPointsFromMyPoints(MyPointWithTime.getMyPointList(tra.points)));
		}
		KmlFile.writeKmlPath(lines, saveName);
	}
	
	public static void writeTrajectoriesPoints(List<GeoTrajectory> tras, String saveName){
		List<KmlFile> kfs=new ArrayList<KmlFile>();
		for(GeoTrajectory tra: tras){
			kfs.addAll(KmlFile.getKmlPointsFromMyPoints(MyPointWithTime.getMyPointList(tra.points)));
		}
		KmlFile.writeKmlFile(kfs, saveName);
	}
	
	//从标注的kml文件中读取信息
	public static List<KmlFile> loadTagFile(String path)
	{
		List<KmlFile> places=new ArrayList<KmlFile>();
		try
		{
			Document doc=Jsoup.parse(new File(path), "utf-8");
		//	System.out.println(doc);
			Elements elems=doc.select("placemark");
			for(Element elem: elems)
			{
				String name=elem.select("name").first().text();
			//	System.out.println(name);
				String coordinates=elem.select("coordinates").first().text();
			//	System.out.println(coordinates);
			//	String description=elem.select("description").first().text();
			//	System.out.println(description+"\n");
				KmlFile kf=new KmlFile();
				kf.label=name;
				String[] ss=coordinates.split(",");
				kf.longitude=Double.parseDouble(ss[0]);
				kf.latitude=Double.parseDouble(ss[1]);
				places.add(kf);
			}
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return places;
	}
	
	public static List<MyPoint> getPointsFromKml(String path){
		List<KmlFile> places=KmlFile.loadTagFile(path);
		List<MyPoint> ps=new ArrayList<MyPoint>();
		for(KmlFile kf: places){
			MyPoint mp=new MyPoint();
			mp.x=kf.longitude;
			mp.y=kf.latitude;
			mp.label=kf.label;
			ps.add(mp);
		}
		return ps;
	}
	
	public static void testLines(){
		List<KmlFile> l1=new ArrayList<KmlFile>();
		l1.add(new KmlFile(116.3020662120154, 40.05859498214198));
		l1.add(new KmlFile(116.4052032992879, 40.05502726970099));
		l1.add(new KmlFile(116.4367430547336,40.02225533371917));
		
		List<KmlFile> l2=new ArrayList<KmlFile>();
		l2.add(new KmlFile(116.3065882582666,40.0989508915672));
		l2.add(new KmlFile(116.4159728053108,40.08036125441734));
		l2.add(new KmlFile(116.4655922981152,40.03259911496554));
		
		List<List<KmlFile>> lines=new ArrayList<List<KmlFile>>();
		lines.add(l1);
		lines.add(l2);
		KmlFile.writeKmlPath(lines, "testlines");
	}
	
	public static void main(String[] args){
	//	List<KmlFile> places=loadTagFile("C:/Users/Admin/Desktop/北京景点.kml");
	//	KmlFile.writeKmlFile(places, "test.kml");
		testLines();
	}
}
