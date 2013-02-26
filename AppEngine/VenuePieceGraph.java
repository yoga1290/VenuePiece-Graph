import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class VenuePieceGraph 
{
	private static String 	AppID="QZPL2PBSUACGD50DLA1RTOJTUAUD5YE1DN1F53VSUFG44RHW",
			AppSecret="*******",
			REDIRECT_URI="http://yoga1290.appspot.com/VenuePieceGraph/oauth/foursquare/callback/";
	public static void newUser(String access_token) throws Exception
	{
			JSONObject userInfo=Foursquare.getUserInfo(access_token).getJSONObject("response").getJSONObject("user");
			String fsqid=userInfo.getString("id");
			
			Entity member=null;
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			try{
				member=datastore.get(KeyFactory.createKey("VenuePieceGraph", fsqid));
			}catch(Exception e){
				member=new Entity("VenuePieceGraph", fsqid);
				member.setProperty("access_token", access_token);
				datastore.put(member);
			}
	}
	public static void doGet(HttpServletRequest req, HttpServletResponse resp)
	{
		try{
			/* 
	https://foursquare.com/oauth2/authenticate?client_id=QZPL2PBSUACGD50DLA1RTOJTUAUD5YE1DN1F53VSUFG44RHW&response_type=code&redirect_uri=http://yoga1290.appspot.com/VenuePieceGraph/oauth/foursquare/callback/
			    	    */
				if(req.getRequestURI().equals("/VenuePieceGraph/oauth/foursquare/callback/"))
				{
					String code=req.getParameter("code");
					String access_token=Foursquare.getAccessToken(AppID, AppSecret,REDIRECT_URI, code);
					
					newUser(access_token);
					String res=readFromURL("http://yoga1290.appspot.com/VenuePieceGraph.html");
					res=res.replace("accessToken=null", "accessToken=\""+access_token+"\"");
					resp.getWriter().println(
							res
							);
				}
			
			}catch(Exception e)
			{
				e.printStackTrace();
				try{resp.getWriter().println("exception="+e.getMessage());}catch(Exception e2){}
			}
	}
	
	
	public static String readFromURL(String rURL)
	{
		String res="";
		try
		{	
			URL url = new URL(rURL);
	
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        InputStream in=connection.getInputStream();
	        byte buff[]=new byte[in.available()];
            int ch;
            while((ch=in.read(buff))!=-1)
            		res+=new String(buff,0,ch);
		}catch(Exception e)
		{
			return e.getMessage();
		}
		return res;		
	}
}
