package appathon.animation;

import com.google.android.gms.maps.model.Marker;

import android.os.CountDownTimer;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;

public class MarkerAnimationCountDownTimer extends CountDownTimer {

	final Interpolator interpolator = new BounceInterpolator();
	private Marker marker;

	private long duration;
	private long millisInFuture;
	
	public MarkerAnimationCountDownTimer(long millisInFuture,
			long countDownInterval, Marker marker, long time) {
		super(millisInFuture, countDownInterval);
		this.marker = marker;
		this.duration = millisInFuture / time;
		this.millisInFuture = millisInFuture;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onTick(long millisUntilFinished) {
		// TODO Auto-generated method stub
		long elapsed = (millisInFuture - millisUntilFinished) % duration;
		float t = Math.max(
				1 - interpolator.getInterpolation((float) elapsed
						/ duration), 0);
		marker.setAnchor(0.5f, 0.5f + 0.5f * t);
	}

	@Override
	public void onFinish() {
		// TODO Auto-generated method stub
		marker.setAnchor(0.5f, 0.5f);
	}

}
