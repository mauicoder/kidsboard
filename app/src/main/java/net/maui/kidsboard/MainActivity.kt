package net.maui.kidsboard;

import net.maui.kidsboard.model.PaintModel;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends Activity {

	//private static final String TAG = "net.maui.kidsboard";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		setContentView(R.layout.activity_main);
		ImageButton button = (ImageButton)findViewById(R.id.btnColorPicker);
		button.setBackgroundColor(PaintModel.getInstance().getPaint().getColor());//set default background color
        button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openColorPicker(v);
				
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void openColorPicker(View v){
		
		final Dialog d = new Dialog(this,R.style.ColorDialog);
		d.setContentView(R.layout.color_pick);
		Button b = (Button)d.findViewById(R.id.btnColorBlack);
		setColorButtonClickListener(b,d);
		b = (Button)d.findViewById(R.id.btnColorBlue);
		setColorButtonClickListener(b,d);
		b = (Button) d.findViewById(R.id.btnColorGreen);
		setColorButtonClickListener(b,d);
		b = (Button)d.findViewById(R.id.btnColorRed);
		setColorButtonClickListener(b,d);
		b = (Button)d.findViewById(R.id.btnColorDarkBlue);
		setColorButtonClickListener(b,d);
		b = (Button)d.findViewById(R.id.btnColorMagenta);
		setColorButtonClickListener(b,d);
		b = (Button)d.findViewById(R.id.btnColorOrange);
		setColorButtonClickListener(b,d);
		b = (Button)d.findViewById(R.id.btnColorYellow);
		setColorButtonClickListener(b,d);
		b = (Button)d.findViewById(R.id.btnColorWhite);
		setColorButtonClickListener(b,d);
		b = (Button)d.findViewById(R.id.btnColorCyan);
		setColorButtonClickListener(b,d);
		b = (Button)d.findViewById(R.id.btnColorBrown);
		setColorButtonClickListener(b,d);
		b = (Button)d.findViewById(R.id.btnColorCoffee);
		setColorButtonClickListener(b,d);
		d.show();
	}
	private void setColorButtonClickListener(Button b, final Dialog d){
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				colorSelected(v);
				d.dismiss();
			}
		});
	}
	
	public void colorSelected(View v){
		Drawable bgDrawable = v.getBackground();
		if(bgDrawable instanceof ColorDrawable){
			ColorDrawable btColor = (ColorDrawable) bgDrawable;
			int color = btColor.getColor();
			PaintModel.getInstance().setColor(color);
			ImageButton button = (ImageButton)findViewById(R.id.btnColorPicker);
			button.setBackgroundColor(color);
		}else{
			Log.i(this.getClass().getSimpleName(), "Not a ColorDrawable instance");
		}
			
	}
}
