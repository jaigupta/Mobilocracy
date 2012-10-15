package android.jai.com;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

public class CameraDemo extends Activity {
	private static final String TAG = "CameraDemo";
	Camera camera;
	Preview preview;
	Button buttonClick;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.campreview);

		preview = new Preview(this);
		((FrameLayout) findViewById(R.id.preview)).addView(preview);

		buttonClick = (Button) findViewById(R.id.buttonClick);
		buttonClick.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				preview.camera.takePicture(shutterCallback, rawCallback,
						jpegCallback);
			}
		});

		Log.d(TAG, "onCreate'd");
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			Log.d(TAG, "onShutter'd");
		}
	};

	/** Handles data for raw picture */
	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken - raw");
		}
	};

	/** Handles data for jpeg picture */
	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(final byte[] data, final Camera camera) {
			Thread t = new Thread() {
				public void run() {
					FileOutputStream outStream = null;
					String path = "/sdcard/" + System.currentTimeMillis()
							+ "_bmic.jpg";
					try {
						outStream = new FileOutputStream(path);
						outStream.write(data);
						outStream.close();
						DataUploader.uploadImageUri = path;
					} catch (FileNotFoundException e) {
						Log.d(TAG, "File Not Found Exception");
						e.printStackTrace();
					} catch (IOException e) {
						Log.d(TAG, "IO Exception");
						e.printStackTrace();
					}

				}
			};
			t.start();
			Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
			Log.d(TAG, "onPictureTaken - jpeg");
			finish();
		}
	};

}
