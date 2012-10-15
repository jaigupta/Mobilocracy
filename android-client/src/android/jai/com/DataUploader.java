package android.jai.com;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

public class DataUploader {
	public static String uploadImageUri;
	public static int dd = 0, mm = 0, yyyy = 0, hr = 0, min = 0;
	public static float geocode[] = new float[2];
	public static String locAddr = "";
	public static String title = "";
	public static String detail = "";
	public static void upload() {
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;

		String pathToOurFile = uploadImageUri;
		String urlServer = Identity.rootUrl + "uploader/storefile?userid="+Identity.id+
							"&title=" + URLEncoder.encode(title) +
							"&detail=" + URLEncoder.encode(detail) +
							"&issuedate=" + URLEncoder.encode(DataUploader.yyyy + "-" + DataUploader.mm+"-" + DataUploader.dd
							+" "+DataUploader.hr+":"+DataUploader.min+":00")
							+"&latitude="+String.format("%.0f", DataUploader.geocode[0]*1000000)
							+"&longitude="+String.format("%.0f", DataUploader.geocode[1]*1000000);
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;

		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			String encodedURI = Identity.rootUrl
					+ "uploader"
					+ "?id="
					+ Identity.id
					+ "&pass="
					+ Identity.pass;
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
			if(sb.toString().compareTo("0")==0)
			{
				Log.d("DataUploader", "Server returned secret 0. Request to upload rejected");
				return;
			}
			FileInputStream fileInputStream = new FileInputStream(new File(
					pathToOurFile));

			URL url = new URL(urlServer);
			connection = (HttpURLConnection) url.openConnection();

			// Allow Inputs & Outputs
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			// Enable POST method
			connection.setRequestMethod("POST");

			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			outputStream = new DataOutputStream(connection.getOutputStream());
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			String[] fileparts = pathToOurFile.split("\\.");
			outputStream
					.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
							+ Identity.id+"."+sb.toString()+"."+fileparts[fileparts.length-1] + "\"" + lineEnd);
			outputStream.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// Read file
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {
				outputStream.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}
			fileInputStream.close();

			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(twoHyphens + boundary + twoHyphens
					+ lineEnd);

			String serverResponseMessage = connection.getResponseMessage();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String in, res = "";
			while ((in = br.readLine()) != null)
				res += in + " ";
			Log.d("DataUploader.Java", "Server Response:" + res);

			Log.d("Data Upload Server Response: ", serverResponseMessage);
			outputStream.flush();
			outputStream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
