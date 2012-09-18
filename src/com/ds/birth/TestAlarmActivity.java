package com.ds.birth;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

public class TestAlarmActivity extends Activity{

	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.main);
	        Calendar c=Calendar.getInstance();
	 
	        c.set(Calendar.YEAR,2011);
	        c.set(Calendar.MONTH,Calendar.JUNE);//也可以填数字，0-11,一月为0
	        c.set(Calendar.DAY_OF_MONTH, 28);
	        c.set(Calendar.HOUR_OF_DAY, 19);
	        c.set(Calendar.MINUTE, 50);
	        c.set(Calendar.SECOND, 0);
	        //设定时间为 2011年6月28日19点50分0秒
	        //c.set(2011, 05,28, 19,50, 0);
	        //也可以写在一行里
	 
	        Intent intent=new Intent(this,AlarmReceiver.class);
	        PendingIntent pi=PendingIntent.getBroadcast(this, 0, intent,0);
	        //设置一个PendingIntent对象，发送广播
	        AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);
	        //获取AlarmManager对象
	        am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
	 
	        //时间到时，执行PendingIntent，只执行一次
	        //AlarmManager.RTC_WAKEUP休眠时会运行，如果是AlarmManager.RTC,在休眠时不会运行
	        //am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 10000, pi);
	        //如果需要重复执行，使用上面一行的setRepeating方法，倒数第二参数为间隔时间,单位为毫秒
	 
	    }
}
