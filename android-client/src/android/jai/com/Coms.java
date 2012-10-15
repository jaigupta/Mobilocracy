package android.jai.com;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class Coms {

	public static Handler handler = new Handler();

	public static void showToast(final Context c, final String msg,
			final int length) {
		handler.post(new Runnable() {
			public void run() {
				Toast.makeText(c, msg, length).show();
			}
		});
	}
}
