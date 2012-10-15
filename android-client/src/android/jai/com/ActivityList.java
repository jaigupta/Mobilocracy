package android.jai.com;

public class ActivityList {
	public static final int CAMERA = 0;
	public static final int BROWSER = 1;
	public static final int MAPSVIEW = 1;

	public static int curActivity = -1;

	public static void setCurActivity(int curActivity) {
		ActivityList.curActivity = curActivity;
	}
}