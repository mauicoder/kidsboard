package net.maui.kidsboard.model;

import android.graphics.Color;
import android.graphics.Paint;

public class PaintModel {

	private static PaintModel instance = new PaintModel();
	
	private volatile Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	public static PaintModel getInstance(){
		return instance;
	}
	
	private Paint getDefaultPaint(){
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(3);
		p.setColor(Color.BLACK);
		return p;
	}
	
	public Paint setDefaultPaint(){
		setPaint(getDefaultPaint());
		return getPaint();
	}
	
	private PaintModel(){
		setPaint(getDefaultPaint());
	}

	public Paint getPaint() {
		return paint;
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}
	
	public void setColor(int color){
		paint.setColor(color);
	}
}
