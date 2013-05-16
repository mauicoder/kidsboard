package net.maui.kidsboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DrawableView extends View {
	private static final String TAG = "DrawableView";
	private float oldX, oldY, curX, curY = -1;
	private boolean draw = false;
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Bitmap bm;
	private Canvas c;
	private Rect rect;

	public DrawableView(Context context, AttributeSet attr) {
		super(context, attr);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		paint.setColor(Color.BLACK);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		//Log.i(TAG, "onSizeChanged");
        if (bm != null) {
            bm .recycle();
        }
        c= new Canvas();
        bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        c.setBitmap(bm);
        rect = new Rect(0,0,bm.getWidth(),bm.getHeight());
    }
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (draw) {
			// Do the draw
			c.drawLine(oldX, oldY, curX, curY, paint);
			// Store Position
			storePosition();
		}
		canvas.drawBitmap(bm, rect, rect, null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		curX = event.getX();
		curY = event.getY();

		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_UP:
//			Log.i(TAG, "ACTION_UP");
			draw = false;
			break;
		case MotionEvent.ACTION_DOWN:
			storePosition();
			draw = false;
//			Log.i(TAG, "ACTION_DOWN");
			break;
		case MotionEvent.ACTION_MOVE:
			Log.i(TAG, "ACTION_MOVE");
			// Draw
			draw = true;
			break;
		default:
			Log.i(TAG, "Default:" + action);
		}
		invalidate();//invalidate to make onDraw be called
		return true;
	}
	
	private void storePosition() {
		oldX = curX;
		oldY = curY;
	}
}
