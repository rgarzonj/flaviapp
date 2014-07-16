package com.vanfides.flavi;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class CallAPI extends AsyncTask<String, Integer, Boolean> {
	private static final String TAG = "CallAPI";
//	private static final String SERVER_URL = "http://192.168.26.33:3000/api/positions";
	private static final String SERVER_URL = "http://192.168.26.78:8000/positions/";


	@Override
	protected Boolean doInBackground(String... params) {
		// TODO Auto-generated method stub
		boolean resul = true;

		HttpClient httpClient = new DefaultHttpClient();

		HttpPost post = new
				HttpPost(SERVER_URL);

		post.setHeader("content-type", "application/json");

		try
		{
			//Construimos el objeto cliente en formato JSON
			JSONObject dato = new JSONObject();

			dato.put("objType", params[0]);
			dato.put("deviceID", params[1]);
			dato.put("platform", params[2]);
			dato.put("longitude", params[3]);
			dato.put("latitude", params[4]);
			dato.put("fixTime",params[5]);
			dato.put("speed", params[4]);
			dato.put("bearing",params[5]);
			Log.d(TAG,params[5]);
			//Longitude Latitude for PointField in GeoDjango
			dato.put("geo",	"POINT(" + params[3] + " " + params[4] + ")");

			StringEntity entity = new StringEntity(dato.toString());
			post.setEntity(entity);

			HttpResponse resp = httpClient.execute(post);
			String respStr = EntityUtils.toString(resp.getEntity());
			Log.d(TAG,respStr);
			String validResponse = "deviceID\": \"" + params[1];
			Log.d(TAG,validResponse);
			if(!respStr.contains(validResponse))
				resul = false;
		}
		catch(Exception ex)
		{
			Log.e(TAG,"Error!", ex);
			resul = false;
		}
		Log.d(TAG,"returning " + resul);
		return resul;
	}


}
