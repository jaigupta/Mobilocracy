package android.jai.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class main extends Activity {
	/** Called when the activity is first created. */
	Handler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		Identity.loadIdentity(this);
		final Activity IdentitySaver = this;
		handler = new Handler();
		Thread t = new Thread() {
			public void run() {
				try {
					HttpClient client = new DefaultHttpClient();
					HttpGet request = new HttpGet();
					if (Identity.valid == false)
						request.setURI(new URI(Identity.rootUrl + "login_ci"));
					else
						request.setURI(new URI(Identity.rootUrl
								+ "login_ci?id=" + Identity.id + "&pass="
								+ Identity.pass));
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
					int id;
					try {
						id = (Integer) root.get("id");
					} catch (Exception e) {
						id = Integer.parseInt((String) root.get("id"));
					}
					String pass = (String) root.get("pass");
					int verified;
					try {
						verified = (Integer) root.get("verified");
					} catch (Exception e) {
						verified = Integer.parseInt((String) root
								.get("verified"));
					}
					if (Identity.valid == false) {
						Identity.id = id;
						Identity.pass = pass;
					}
					Identity.email = (String) root.get("email");
					Identity.valid = verified == 0 ? false : true;
					Identity.name = (String) root.get("name");
					Identity.address = (String) root.get("address");
					Identity.detail = (String) root.get("detail");
					Identity.storeIdentity(IdentitySaver);
					startActivity(new Intent("android.jai.com.ChoicePage"));
					finish();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
					showToast(
							"Could not Connect to Internet.\n Check your network settings.\n Using Default settings.",
							Toast.LENGTH_LONG);
					startActivity(new Intent("android.jai.com.StartPage"));
					finish();
				} catch (JSONException e) {
					e.printStackTrace();
				} finally {
				}
			}
		};
		t.start();
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