package android.jai.com;
import android.app.Activity;
import android.content.Intent;
import android.jai.com.R;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class ChoicePage extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choicepage);
		Button reportIssue = (Button)findViewById(R.id.report_issue);
		Button browseIssue = (Button)findViewById(R.id.browse_issue);
		Button myIssue     = (Button)findViewById(R.id.my_issue);
		reportIssue.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent("android.jai.com.StartPage"));
			}
		});
		browseIssue.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent("android.jai.com.BrowseMap"));
			}
		});
	}

}
