package se.sandos.android.flickrcheck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.http.HttpParameters;
import se.sandos.android.flickrcheck.json.PhotoInfo;
import se.sandos.android.flickrcheck.json.PhotoSearch;
import se.sandos.android.flickrcheck.json.PhotoSearch.Photos;
import se.sandos.android.flickrcheck.json.PhotoSetList;
import se.sandos.android.flickrcheck.json.PhotoSetList.PhotoSets;
import se.sandos.android.flickrcheck.json.Sizes;
import se.sandos.android.flickrcheck.json.Sizes.Size.SizeLine;
import se.sandos.android.flickrcheck.json.Stat;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.util.Log;

import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONObjectException;

public class FlickrApi {
	private static final String API_KEY    = "a03761049a0bfcd06fecfc3df77d553e";
	private static final String API_SECRET = "3d87a7efc4232623";
	
	private OAuthProvider provider;
	private OAuthConsumer oac;
	
	private static final String USERNAME     = "username";
	private static final String USER_NSID    = "user_nsid";
	private static final String FULLNAME     = "fullname";
	private static final String ACCESS_TOKEN = "AccessToken";
	private static final String TOKEN_SECRET = "TokenSecret";
	
	private static final String LOG_TAG = "flickr";
	
	private String username;
	private String user_nsid;
	private String fullname;
	
	private boolean invalidTokens = false;
	
	public FlickrApi() {
        oac = new DefaultOAuthConsumer(API_KEY, API_SECRET);
        provider = new CommonsHttpOAuthProvider(
                "https://www.flickr.com/services/oauth/request_token", 
                "https://www.flickr.com/services/oauth/access_token",
                "https://www.flickr.com/services/oauth/authorize");
	}
	
	public Intent login() throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
		String authUrl = provider.retrieveRequestToken(oac, "flickrcheck://flickrcheck.com");
		Log.w(LOG_TAG, "got auth URL: " + authUrl);
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(authUrl + "&perms=write"));
		return i;
	}
	
	public void retrieveAccessToken(String t) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
		provider.retrieveAccessToken(oac, t);
		
		HttpParameters rp = provider.getResponseParameters();
		for(String key : rp.keySet()) {
			if(key.equals("username")) {
				username = rp.get(key).first();
			} else if(key.equals("user_nsid")) {
				user_nsid = rp.get(key).first();
			} else if(key.equals("fullname")) {
				fullname = rp.get(key).first();
			}
		}
	}
	
	public boolean isOk() {
		return oac.getToken() != null && !oac.getToken().isEmpty() &&
				oac.getTokenSecret() != null && !oac.getTokenSecret().isEmpty();
	}
	
	public void sign(URLConnection conn) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		oac.sign(conn);
	}
	
	public boolean hasTokens(SharedPreferences prefs) {
//		return prefs.contains(FULLNAME) && prefs.contains(USER_NSID) && prefs.contains(USERNAME) &&
//				prefs.contains(ACCESS_TOKEN) && prefs.contains(TOKEN_SECRET);
		return prefs.contains(ACCESS_TOKEN) && prefs.contains(TOKEN_SECRET);
	}
	
	public void setTokens(String accessToken, String tokenSecret) {
		oac.setTokenWithSecret(accessToken, tokenSecret);
	}
	
	public void readTokens(SharedPreferences prefs) {
		oac.setTokenWithSecret(prefs.getString("AccessToken", ""), prefs.getString("TokenSecret", ""));
		fullname =  prefs.getString(FULLNAME, "");
		username =  prefs.getString(USERNAME, "");
		user_nsid = prefs.getString(USER_NSID, "");
	}
	
	public void storeTokens(SharedPreferences prefs) {
		Editor edit = prefs.edit();

		edit.putString(USERNAME, username);
		edit.putString(USER_NSID, user_nsid);
		edit.putString(FULLNAME, fullname);
		edit.putString(ACCESS_TOKEN, oac.getToken());
		edit.putString(TOKEN_SECRET, oac.getTokenSecret());
		edit.commit();
	}
	
	public PhotoSets getPhotoSets() throws IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		URL url = new URL("https://api.flickr.com/services/rest/?method=flickr.photosets.getList&format=json&api_key=" + API_KEY);
		URLConnection conn = url.openConnection();
		sign(conn);

		StringBuffer input = logCall(conn, false);
		
		PhotoSetList list = JSON.std.beanFrom(PhotoSetList.class, input.toString().substring(14, input.length()-1));
		
//		for(PhotoSet set : list.photosets.photoset) {
			//Log.w("majs", "Album: " + set.title._content);
//		}
		
		return list.photosets;
	}
	
	public String getVideoUrl(String photoid) throws IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		String u = "https://api.flickr.com/services/rest/?method=flickr.photos.getSizes&format=json&api_key=" + API_KEY +"&photo_id=" + photoid;
		URL url = new URL(u);

		URLConnection conn = url.openConnection();
		sign(conn);
		
		StringBuffer input = logCall(conn, false);

		Sizes sizes = JSON.std.beanFrom(Sizes.class, input.toString().substring(14, input.length()-1));
		Map<String, SizeLine> sizeLines = sizes.sizes.getSizeLines();
		for(String key : sizeLines.keySet()) {
			Log.w(LOG_TAG, key + "|" + sizeLines.get(key).source);
		}
		
		return sizeLines.get("720p").source;
	}
	public String getOrigImgUrl(String photoid) throws IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		String u = "https://api.flickr.com/services/rest/?method=flickr.photos.getSizes&format=json&api_key=" + API_KEY +"&photo_id=" + photoid;
		URL url = new URL(u);

		URLConnection conn = url.openConnection();
		sign(conn);

		StringBuffer input = logCall(conn, false);


		Sizes sizes = JSON.std.beanFrom(Sizes.class, input.toString().substring(14, input.length()-1));
		if(sizes == null || sizes.sizes == null) {
			Log.w(LOG_TAG, "No reply from sizes");
			return null;
		}
		Map<String, SizeLine> sizeLines = sizes.sizes.getSizeLines();
//		for(String key : sizeLines.keySet()) {
//			Log.w(LOG_TAG, key + "|" + sizeLines.get(key).source);
//		}

		return sizeLines.get("Original").source;
	}

	private boolean success(StringBuffer sb) throws JSONObjectException, IOException {
		try {
			Stat res = JSON.std.beanFrom(Stat.class, sb.toString().substring(14, sb.length()-1));
			
			if(res.stat.equals("fail")) {
				if(res.code == 98) {
					invalidTokens = true;
				}
				return false;
			}
			
			Log.w(LOG_TAG, "Status code: " + res.stat + "|" + res.code + "|" + res.message);
			
			return true;
		} catch(Throwable e) {
			return true;
		}
	}
	
	public boolean invalidTokens() {
		return invalidTokens;
	}
	
	public Photos search(String text) throws IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		String u = "https://api.flickr.com/services/rest/?method=flickr.photos.search&format=json&api_key=" + API_KEY + "&user_id=me";
		if(text != null && !text.isEmpty()) {
			u += "&text=" + text;
		}
		URL url = new URL(u);
		URLConnection conn = url.openConnection();
		sign(conn);

		StringBuffer input = logCall(conn, false);
		
		//Failure looks like:
		//jsonFlickrApi({"stat":"fail", "code":98, "message":"Invalid auth token"})
		if(!success(input)) {
			return null;
		}
		
		PhotoSearch search = JSON.std.beanFrom(PhotoSearch.class, input.toString().substring(14, input.length()-1));

		return search.photos;
	}

	public PhotoInfo getPhotoInfo(String id) throws IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		String u = "https://api.flickr.com/services/rest/?method=flickr.photos.getInfo&format=json&api_key=" + API_KEY + "&photo_id=" + id;
		URL url = new URL(u);
		URLConnection conn = url.openConnection();
		sign(conn);
		
		StringBuffer input = logCall(conn, false);
		
		PhotoInfo info = JSON.std.beanFrom(PhotoInfo.class, input.toString().substring(14, input.length()-1));
		
		return info;
	}
	
	private StringBuffer logCall(URLConnection uc, boolean print) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
		StringBuffer input = new StringBuffer();
		String s;
		while((s = br.readLine()) != null) {
			input.append(s);
		}
		if(print) {
			Log.w(LOG_TAG, input.toString());
		}
		br.close();
		
		return input;
	}
}
