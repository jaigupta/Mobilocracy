package android.jai.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class BrowseMap extends MapActivity{

	private MapView mapView;
	private MapController mapController;
	private LocationManager locationManager;
	private GeoUpdateHandler geoHandler;
	List<Overlay> myoverlays;
	private MarkOverlay markers;
	private Drawable myicon;
	JSONObject lastResult;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showmap);

		// create a map view
		RelativeLayout linearLayout = (RelativeLayout) findViewById(R.id.mainlayout);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setStreetView(true);
		mapController = mapView.getController();
		geoHandler = new GeoUpdateHandler(mapController);
		mapController.setZoom(10); // Zoom 1 is world view
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, geoHandler);
		if(DataUploader.geocode[0]!=0.0 || DataUploader.geocode[1] != 0.0)
		{
			geoHandler.changeOnMove = false;
			mapController.animateTo(new GeoPoint((int)(DataUploader.geocode[0]*1000000), (int)(DataUploader.geocode[1]*1000000) ));
		}
		myoverlays = mapView.getOverlays();
		myicon = this.getResources().getDrawable(R.drawable.smoke3);
		markers = new MarkOverlay(myicon);
		myoverlays.add(markers);
		mapView.postInvalidate();
	}

	public boolean onKeyDown(int keyCode, KeyEvent e){
        if(keyCode == KeyEvent.KEYCODE_S){
            mapView.setSatellite(!mapView.isSatellite());
            return true;
        } else if(keyCode == KeyEvent.KEYCODE_T){
            mapView.setTraffic(!mapView.isTraffic());
        }
        return(super.onKeyDown(keyCode, e));
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public class MarkOverlay extends Overlay
	{
		Drawable mark;
		MarkOverlay(Drawable mark)
		{
			super();
			this.mark = mark;
		}
		public void draw(Canvas canvas, MapView mapv, boolean shadow){
	        super.draw(canvas, mapv, shadow);

	        Paint   mPaint = new Paint();
	        mPaint.setDither(true);
	        mPaint.setColor(Color.RED);
	        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
	        mPaint.setStrokeJoin(Paint.Join.ROUND);
	        mPaint.setStrokeCap(Paint.Cap.ROUND);
	        mPaint.setStrokeWidth(2);
	        GeoPoint center = mapv.getMapCenter();
	        int latspan = mapv.getLatitudeSpan();
	        int longspan = mapv.getLongitudeSpan();
	        Path path = new Path();
	        HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			try{
				String encodedURI = Identity.rootUrl
					+ "markers"
					+ "?lat_min="
					+ (center.getLatitudeE6()-latspan)
					+ "&lat_max="
					+ (center.getLatitudeE6()+latspan)
					+ "&long_min="
					+ (center.getLongitudeE6()-longspan)
					+ "&long_max="
					+ (center.getLongitudeE6()+longspan);
				request.setURI(new URI(encodedURI));
				HttpResponse response = client.execute(request);
				InputStream ips = response.getEntity().getContent();
				BufferedReader buf = new BufferedReader(
						new InputStreamReader(ips, "UTF-8"));
				StringBuilder sb = new StringBuilder();
				String s;
				while (true) {
					s = buf.readLine();
					if (s == null || s.length() == 0)
						break;
					sb.append(s);
	
				}
				buf.close();
				ips.close();
				Log.d("BrowseMap.java", "Server Response:"+sb.toString());
				JSONObject root = new JSONObject(sb.toString());
				int count = root.getInt("count");
				JSONArray resArray = root.getJSONArray("result");
				Projection projection = mapView.getProjection();
				for(int i=0;i<count;i++)
				{
					JSONObject j = (JSONObject) resArray.get(i);
					GeoPoint p = new GeoPoint(j.getInt("latitude"), j.getInt("longitude"));
					Point p1 = new Point();
					projection.toPixels(p, p1);
					path.addCircle(p1.x, p1.y, 5, Direction.CCW);
				}
				lastResult = root;
		        canvas.drawPath(path, mPaint);
			}catch(Exception e){e.printStackTrace();}
	    }
		
		public boolean onTouchEvent(MotionEvent event, MapView mapView) 
	    {   
	        //---when user lifts his finger---
	        if (event.getAction() == 1) { 
	        	Projection projection = mapView.getProjection();
	        	int x = (int)event.getX();int y = (int)event.getY();
	            try {
            		int count = lastResult.getInt("count");
            		JSONArray resArray = lastResult.getJSONArray("result");
            		Point p = new Point();
            		for(int i=0;i<count;i++)
            		{
            			JSONObject j = (JSONObject) resArray.get(i);
            			int lati = j.getInt("latitude");
            			int longi = j.getInt("longitude");
            			projection.toPixels(new GeoPoint(lati,longi), p);
            			if(Math.pow(x-p.x,2) + Math.pow(y-p.y,2) < 25)
            			{
            				Toast.makeText(getBaseContext(), j.getString("title"), Toast.LENGTH_SHORT).show();
            			}
            		}
	            }
	            catch (Exception e) {
//	                Log.e("ShowMap.java", e.getMessage());
	            }   
	            return true;
	        }
	        else                
	            return false;
	    }  
	}
}
