/**
 * 
 */
package android.jai.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author jai
 * 
 */
public class Home extends Activity {
	TextView emailid, nameid, addressid, detailid;
	Context Backup;
	Handler handler;

	public void onCreate(Bundle state) {
		super.onCreate(state);
		Identity.storeIdentity(this);
		setContentView(R.layout.home);
		emailid = (TextView) findViewById(R.id.user_email);
		emailid.setText(Identity.email);
		nameid = (TextView) findViewById(R.id.user_name);
		nameid.setText(Identity.name);
		addressid = (TextView) findViewById(R.id.user_address);
		addressid.setText(Identity.address);
		detailid = (TextView) findViewById(R.id.user_detail);
		detailid.setText(Identity.detail);
		handler = new Handler();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		if (emailid.getText().toString().compareTo(Identity.email) != 0
				|| nameid.getText().toString().compareTo(Identity.name) != 0
				|| addressid.getText().toString().compareTo(Identity.address) != 0
				|| detailid.getText().toString().compareTo(Identity.detail) != 0) {
			Thread t = new Thread() {
				public void run() {
					try {
						HttpClient client = new DefaultHttpClient();
						HttpGet request = new HttpGet();
						String encodedURI = Identity.rootUrl
								+ "login_ci/update"
								+ "?id="
								+ Identity.id
								+ "&pass="
								+ Identity.pass
								+ "&email="
								+ URLEncoder.encode(emailid.getText()
										.toString().trim())
								+ "&name="
								+ URLEncoder.encode(nameid.getText().toString()
										.trim())
								+ "&address="
								+ URLEncoder.encode(addressid.getText()
										.toString().trim())
								+ "&detail="
								+ URLEncoder.encode(detailid.getText()
										.toString().trim());
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
						JSONObject root = new JSONObject(sb.toString());
						int result;
						try {
							result = (Integer) root.get("result");
						} catch (Exception e) {
							result = Integer.parseInt((String) root
									.get("result"));
						}
						if (result == 0) {
							String error = (String) root.get("error");
							showToast("Error:" + error, Toast.LENGTH_LONG);
						} else {
							showToast("Data Updated successfully..",
									Toast.LENGTH_SHORT);
							Identity.email = emailid.getText().toString()
									.trim();
							Identity.name = nameid.getText().toString().trim();
							Identity.address = addressid.getText().toString()
									.trim();
							Identity.detail = detailid.getText().toString()
									.trim();
						}
					} catch (URISyntaxException e) {
						showToast("Error: Please recheck data",
								Toast.LENGTH_LONG);
						e.printStackTrace();
					} catch (IOException e) {
						showToast("Error: Error in Connection.",
								Toast.LENGTH_LONG);
						e.printStackTrace();
					} catch (JSONException e) {
						showToast("Error: Invalid Input", Toast.LENGTH_LONG);
						e.printStackTrace();
					}
				}
			};
			t.start();
		}
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
