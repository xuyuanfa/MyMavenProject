package com.xxx.date;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateDemo {

	public static void main(String[] args) {
//		testCalendar();
		getWeekDay();
	}

	private static void testCalendar() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		System.out.println(calendar.get(Calendar.DAY_OF_MONTH));// 今天的日期
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);// 让日期加1
		System.out.println(calendar.get(Calendar.DATE));// 加1之后的日期Top
		

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		System.out.println(sdf.format(calendar.getTime()));
	}

	private static void getWeekDay(){
		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(new Date());
		System.out.println(calendar.getTime());
		calendar.set(2016, 4, 31);
		System.out.println(calendar.getTime());
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.add(Calendar.DAY_OF_YEAR, -(calendar.get(Calendar.DAY_OF_WEEK)-calendar.getFirstDayOfWeek()));
		Date startDate = calendar.getTime();
		System.out.println(startDate);
		calendar.add(Calendar.DAY_OF_YEAR, 7);
		Date endDate = calendar.getTime();
		System.out.println(endDate);
	}
}
