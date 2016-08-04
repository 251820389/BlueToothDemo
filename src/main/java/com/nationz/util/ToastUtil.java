package com.nationz.util;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtil
{
	private final static boolean	isShowToast	= true;

	public static void showToast(final Context context, final String msg)
	{
		if (isShowToast)
		{
			if (Looper.myLooper() == Looper.getMainLooper())
			{
				Toast.makeText(context, msg+"\n"+getRunningActivityName(context), Toast.LENGTH_SHORT).show();
			}
			else
			{
				((Activity) context).runOnUiThread(new Runnable()
				{

					@Override
					public void run()
					{
						Toast.makeText(context, msg+"\n"+getRunningActivityName(context), Toast.LENGTH_SHORT).show();
					}
				});
			}
		}
	}

	public static void showToast(final Context context, final int id)
	{
		if (isShowToast)
		{
			if (Looper.myLooper() == Looper.getMainLooper())
			{
				Toast.makeText(context, context.getString(id)+"\n"+getRunningActivityName(context), Toast.LENGTH_SHORT).show();
			}
			else
			{
				((Activity) context).runOnUiThread(new Runnable()
				{

					@Override
					public void run()
					{
						Toast.makeText(context, context.getString(id)+"\n"+getRunningActivityName(context), Toast.LENGTH_SHORT).show();
					}
				});
			}
		}
	}
	private static String getRunningActivityName(Context context){    
        String contextString = context.toString();
        return contextString.substring(contextString.lastIndexOf(".")+1, contextString.indexOf("@"));
	}
}
