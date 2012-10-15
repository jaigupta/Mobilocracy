package android.jai.com;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;

public class Identity {
	public static String rootUrl = "http://10.0.2.2/bmic/index.php/";
	private static String identityDirectory = "Identity";
	private static String identityFile = "myDetails.dat";
	public static int id = 0;
	public static String pass = "";
	public static boolean valid = false;
	public static String email = "";
	public static String name = "";
	public static String address = "";
	public static String detail = "";

	public static void loadIdentity(Activity curAct) {
		File path = curAct.getDir(identityDirectory, 0); // 0 = Private to my
															// Application
		File myFile = new File(path, identityFile);
		FileReader fr;
		try {
			fr = new FileReader(myFile);
			char[] data = new char[1000];
			fr.read(data);
			String[] idpass = new String(data).split(" ");
			if (idpass.length < 2)
				return;
			id = Integer.parseInt(idpass[0]);
			pass = idpass[1].trim();
			valid = true;
			fr.close();
			return;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void storeIdentity(Activity curAct) {
		File path = curAct.getDir(identityDirectory, 0);
		File myFile = new File(path, identityFile);
		FileWriter fw;
		try {
			fw = new FileWriter(myFile);
			fw.write(Identity.id + " " + Identity.pass);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
