package com.nationz.crash;

import android.app.Application;

public class BaseApplication extends Application{

	@Override
	public void onCreate() {
		super.onCreate();
		Crash crash = Crash.getInstance();  
		crash.init(getApplicationContext());
	}
}
