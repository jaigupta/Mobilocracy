package android.jai.com;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class ShowMap extends MapActivity {
	
	private MapController mapController;
	private MapView mapView;
	private LocationManager locationManager;
	private GeoUpdateHandler geoHandler;
	List<Overlay> myoverlays;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.showmap); // bind the layout to the activity

		// create a map view
		RelativeLayout linearLayout = (RelativeLayout) findViewById(R.id.mainlayout);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setStreetView(true);
		mapController = mapView.getController();
		geoHandler = new GeoUpdateHandler(mapController);
		mapController.setZoom(14); // Zoom 1 is world view
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, new GeoUpdateHandler(mapController));
		if(DataUploader.geocode[0]!=0.0 || DataUploader.geocode[1] != 0.0)
		{
			geoHandler.changeOnMove = false;
			mapController.animateTo(new GeoPoint((int)(DataUploader.geocode[0]*1000000), (int)(DataUploader.geocode[1]*1000000) ));
		}
		myoverlays = mapView.getOverlays();
		myoverlays.add(new CenterShow());
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		GeoPoint loc = mapView.getMapCenter();
		DataUploader.geocode[0] = loc.getLatitudeE6()/1000000.00f;
		DataUploader.geocode[1] = loc.getLongitudeE6()/1000000.00f;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
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
      

	public class CenterShow extends Overlay
	{
		public void draw(Canvas canvas, MapView mapv, boolean shadow){
	        super.draw(canvas, mapv, shadow);

	        Paint   mPaint = new Paint();
	        mPaint.setDither(true);
	        mPaint.setColor(Color.BLACK);
	        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
	        mPaint.setStrokeJoin(Paint.Join.ROUND);
	        mPaint.setStrokeCap(Paint.Cap.ROUND);
	        mPaint.setStrokeWidth(2);
	        Path path = new Path();
	        Point c = new Point();
	        Projection projection = mapView.getProjection();
	        projection.toPixels(mapView.getMapCenter(),c);
	        
	        int cx = c.x; int cy = c.y;
	        path.moveTo(cx-4, cy-4);
	        path.lineTo(cx+4, cy+4);
	        path.moveTo(cx+4, cy-4);
	        path.lineTo(cx-4, cy+4);
	        canvas.drawPath(path, mPaint);
	    }
		
		public boolean onTouchEvent(MotionEvent event, MapView mapView) 
	    {   
	        //---when user lifts his finger---
	        if (event.getAction() == 1) {                
	            GeoPoint p = mapView.getMapCenter();
	            Geocoder geoCoder = new Geocoder(
	                getBaseContext(), Locale.getDefault());
	            try {
	                List<Address> addresses = geoCoder.getFromLocation(
	                    p.getLatitudeE6()  / 1E6, 
	                    p.getLongitudeE6() / 1E6, 1);

	                String add = "";
	                if (addresses.size() > 0) 
	                {
	                    for (int i=0; i<addresses.get(0).getMaxAddressLineIndex(); 
	                         i++)
	                       add += addresses.get(0).getAddressLine(i) + "\n";
	                }

	                Toast.makeText(getBaseContext(), add, Toast.LENGTH_SHORT).show();
	                DataUploader.locAddr = add;
	            }
	            catch (IOException e) {
	                Log.e("ShowMap.java", e.getMessage());
	                DataUploader.locAddr = "";
	            }   
	            return true;
	        }
	        else                
	            return false;
	    }        
	}
}