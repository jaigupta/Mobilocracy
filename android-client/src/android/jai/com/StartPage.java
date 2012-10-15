package android.jai.com;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;

public class StartPage extends Activity {
	Handler handler;
	private static final String TAG = "Start Page";
	protected int TAKE_PICTURE = 101;
	GeoPoint snapLoc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler();
		setContentView(R.layout.startpage);
		Button startCapture = (Button) findViewById(R.id.startCaptureBtn);
		startCapture.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent("android.jai.com.CameraDemo"));
			}
		});
		Button startCapture2 = (Button) findViewById(R.id.startCaptureBtn2);
		startCapture2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ActivityList.setCurActivity(ActivityList.CAMERA);
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				startActivityForResult(intent, 0);
			}
		});
		Button loadImage = (Button) findViewById(R.id.loadFile);
		loadImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ActivityList.setCurActivity(ActivityList.BROWSER);
				Intent intent = new Intent("android.jai.com.AndroidExplorer");
				startActivityForResult(intent, 0);
			}
		});
		Button setLoc = (Button) findViewById(R.id.setLocBtn);
		setLoc.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent("android.jai.com.ShowMap"));
			}
		});
		
		Spinner ampmSpinner = (Spinner) findViewById(R.id.spinner4);
		ArrayAdapter<CharSequence> ampmChoice = ArrayAdapter
				.createFromResource(this, R.array.am_pm,
						android.R.layout.simple_spinner_item);
		ampmChoice
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ampmSpinner.setAdapter(ampmChoice);
		
		EditText title_edit = (EditText)findViewById(R.id.titleBox);
		title_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus == false)
				{
					EditText title_edit = (EditText)v;
					DataUploader.title = title_edit.getText().toString();
				}
				
			}
		});
		
		EditText detail_edit = (EditText)findViewById(R.id.detailBox);
		detail_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus == false)
				{
					EditText detail_edit = (EditText)v;
					DataUploader.detail = detail_edit.getText().toString();
				}
				
			}
		});
		
		Button uploadBtn = (Button) findViewById(R.id.uploadBtn);
		uploadBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(){
					public void run(){
						DataUploader.upload();
					}
				}.start();
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		TextView t_long = (TextView)findViewById(R.id.longitude_show);
		TextView t_lat  = (TextView)findViewById(R.id.latitude_show);
		t_lat.setText(String.format("%.2f", DataUploader.geocode[0]));
		t_long.setText(String.format("%.2f", DataUploader.geocode[1]));
		TextView addr  = (TextView)findViewById(R.id.addr_show);
		addr.setText(DataUploader.locAddr);
	}

	@Override
	public void onActivityResult(final int requestCode, final int resultCode,
			final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		final ImageView showImage = (ImageView) findViewById(R.id.selectedImageView);
		Thread t = new Thread() {
			public void run() {
				String fileName = System.currentTimeMillis() + "_bmic.jpg";
				switch (ActivityList.curActivity) {
				case ActivityList.CAMERA:
					if (resultCode == RESULT_CANCELED) {
						showToast("camera cancelled", 10000);
						return;
					}

					// lets check if we are really dealing with a picture

					if (requestCode == 0 && resultCode == RESULT_OK) {

						Bundle extras = data.getExtras();
						Bitmap b = (Bitmap) extras.get("data");
						setContentView(R.layout.startpage);
						File sdCard = Environment.getExternalStorageDirectory();
						File file = new File(sdCard, fileName);
						FileOutputStream fos = null;
						try {
							file.createNewFile();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						try {
							fos = new FileOutputStream(file);

						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						b.compress(CompressFormat.JPEG, 95, fos);
						String timestamp = Long.toString(System
								.currentTimeMillis());
						MediaStore.Images.Media.insertImage(
								getContentResolver(), b, timestamp, timestamp);
					}
					break;
				case ActivityList.BROWSER:
					if (resultCode == RESULT_CANCELED) {
						showToast("No File selected cancelled", 10000);
						return;
					} else {
						if (data != null) {
							Bundle extras = data.getExtras();
							if (extras != null) {
								String s = (String) extras
										.getString("filePath");
								fileName = s;
								try {
									Bitmap myBitmap = BitmapFactory
											.decodeFile(fileName);
									MediaStore.Images.Media.insertImage(
											getContentResolver(), myBitmap,
											"BMIC image", "Image for bmic");

								} catch (Exception e) {
									showToast("Image does not exist!",
											Toast.LENGTH_SHORT);
								}
							} else {
								Log.d(TAG, "Extras is null");
							}
						} else {
							Log.d(TAG, "Data is null");
						}
					}
				}
				File imageFile = new File(fileName);
				try {
					if (!imageFile.exists()) {
						showToast("Image does not exist!", Toast.LENGTH_SHORT);
						return;
					} else if (!imageFile.canRead()) {
						showToast("Image cannot be read!", Toast.LENGTH_SHORT);
						return;
					}
				} catch (Exception e) {
					showToast("Not a valid Image File!", Toast.LENGTH_SHORT);
					return;
				}
				DataUploader.uploadImageUri = fileName;
				final Bitmap myBitmap = getPreview(DataUploader.uploadImageUri,
						showImage.getWidth());
				ExifInterface exif;
				try {
					exif = new ExifInterface(DataUploader.uploadImageUri);
					String dateTime = exif
							.getAttribute(ExifInterface.TAG_DATETIME);
					try {
						if (dateTime == null) {
							showToast("No Date time Information found!",
									Toast.LENGTH_LONG);
							throw new Exception("No Date Found");
						} else {
							String[] dt = dateTime.split(" ");
							String[] date = dt[0].split(":");
							String[] time = dt[1].split(":");
							DataUploader.yyyy = Integer.parseInt(date[0]);
							DataUploader.mm = Integer.parseInt(date[1]);
							DataUploader.dd = Integer.parseInt(date[2]);
							DataUploader.hr = Integer.parseInt(time[0]);
							DataUploader.min = Integer.parseInt(time[1]);
						}
					} catch (Exception e) {
						Calendar cal = Calendar.getInstance();
						DataUploader.yyyy = cal.get(Calendar.YEAR);
						DataUploader.mm = cal.get(Calendar.MONTH) + 1;
						DataUploader.dd = cal.get(Calendar.DATE);
						DataUploader.hr = cal.get(Calendar.HOUR_OF_DAY);
						DataUploader.min = cal.get(Calendar.MINUTE);
					}
					String orientation = exif
							.getAttribute(ExifInterface.TAG_ORIENTATION);
					if (orientation == null) {
						orientation = "vertical";
					}
					exif.getLatLong(DataUploader.geocode);
						
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				StartPage.this.runOnUiThread(new Runnable() {
					public void run() {
						showImage
								.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
						showImage.setImageBitmap(myBitmap);

						CharSequence[] yearList = new CharSequence[10];
						for (int i = 0; i < 10; i++) {
							yearList[i] = Integer.toString(DataUploader.yyyy
									- i);
						}
						Spinner yearChoiceSpinner = (Spinner) findViewById(R.id.spinnerYear);
						ArrayAdapter<CharSequence> yearChoice = new ArrayAdapter<CharSequence>(
								getApplicationContext(),
								android.R.layout.simple_spinner_item, yearList);
						yearChoice
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						yearChoiceSpinner.setAdapter(yearChoice);
						yearChoiceSpinner.setSelection(0);

						CharSequence[] monList = new CharSequence[12];
						for (int i = 0; i < 12; i++) {
							monList[i] = Integer.toString(i + 1);
						}
						Spinner monChoiceSpinner = (Spinner) findViewById(R.id.spinnerMonth);
						ArrayAdapter<CharSequence> monChoice = new ArrayAdapter<CharSequence>(
								getApplicationContext(),
								android.R.layout.simple_spinner_item, monList);
						monChoice
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						monChoiceSpinner.setAdapter(monChoice);
						monChoiceSpinner.setSelection(DataUploader.mm - 1);

						CharSequence[] dayList = new CharSequence[31];
						for (int i = 0; i < 31; i++) {
							dayList[i] = Integer.toString(i + 1);
						}
						Spinner dayChoiceSpinner = (Spinner) findViewById(R.id.spinnerDay);
						ArrayAdapter<CharSequence> dayChoice = new ArrayAdapter<CharSequence>(
								getApplicationContext(),
								android.R.layout.simple_spinner_item, dayList);
						dayChoice
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						dayChoiceSpinner.setAdapter(dayChoice);
						dayChoiceSpinner.setSelection(DataUploader.dd - 1);

						EditText t = (EditText) findViewById(R.id.timeHour);
						t.setText(Integer.toString(DataUploader.hr % 12));
						if (DataUploader.hr >= 12) {
							Spinner am_pm = (Spinner) findViewById(R.id.spinner4);
							am_pm.setSelection(1);
						}
						EditText t2 = (EditText) findViewById(R.id.timeMin);
						t2.setText(Integer.toString(DataUploader.min));
						
						TextView t_long = (TextView)findViewById(R.id.longitude_show);
						TextView t_lat  = (TextView)findViewById(R.id.latitude_show);
						t_lat.setText(Float.toString(DataUploader.geocode[0]));
						t_long.setText(Float.toString(DataUploader.geocode[1]));
					}
				});
			}
		};
		t.start();

	}

	Bitmap getPreview(String uri, int width) {
		File image = new File(uri);

		BitmapFactory.Options bounds = new BitmapFactory.Options();
		bounds.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(image.getPath(), bounds);
		if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
			return null;

		int originalSize = bounds.outWidth;

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = originalSize / (width);
		return BitmapFactory.decodeFile(image.getPath(), opts);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.settings_menu_item:
			startActivity(new Intent("android.jai.com.Home"));
			break;
		case R.id.exit_menu_item:
			finish();
			break;
		}
		return true;
	}

	public void showToast(final String toastMessage, final int duration) {
		handler.post(new Runnable() {
			public void run() {
				Toast.makeText(getApplicationContext(), toastMessage, duration)
						.show();
			}
		});
	}
}
