package net.maui.kidsboard.model;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

public class PaintModel {

	private static PaintModel instance = new PaintModel();
	
	private volatile Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	private float mPhase = 3;
	
	public static PaintModel getInstance(){
		return instance;
	}
	
	private Paint getDefaultPaint(){
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setStyle(Paint.Style.STROKE);
		//p.setPathEffect(new PathDashPathEffect(makePathDash(), 12, mPhase, PathDashPathEffect.Style.MORPH));
		//p.setPathEffect(new CornerPathEffect(10));
		//p.setPathEffect(new DashPathEffect(new float[] {10,10}, 0)); 
		p.setStrokeWidth(5);
		p.setColor(Color.BLACK);
		return p;
	}
	
	private static Path makePathDash() {
        Path p = new Path();
        p.moveTo(-6, 4);
        p.lineTo(6,4);
        p.lineTo(6,3);
        p.lineTo(-6, 3);
        p.close();
        p.moveTo(-6, -4);
        p.lineTo(6,-4);
        p.lineTo(6,-3);
        p.lineTo(-6, -3);
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
		setStrokeWidth(color);
	}
	private void setStrokeWidth(int color){
		if (color == Color.WHITE) {
			paint.setStrokeWidth(15);
		} else
			paint.setStrokeWidth(5);
	}
}
