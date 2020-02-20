package test;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static void main(String[] args) {
//        m1();

    	int b=aaa(2020, 1, 14, 18, 21, 0);
    	System.out.println(b);

    }
    
    public static int aaa(int year, int mon,int day, int hour, int min, int sec) {
		int tt;
		tt = ((year - 2000) * 12 * 31 + ((mon - 1) * 31) + day - 1) * (24 * 60 * 60)
				+(hour * 60 + min) * 60 + sec;
		return tt;
	}

	private static void m1() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        Date date = null;
        try {
            date = sdf.parse("2020-01-10 13:18:00");
            System.out.println(convertDate2Str(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
	}

    public static String convertDate2Str(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//honor
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        long str = (((((year-100-1900)*12+month)*31+day-1)*24+hour)*60+minute)*60+second;
        return str+"";
    }
    public static Date convertStr2Date(String str){
        Long timeStamp = Long.parseLong(str);
        long second = timeStamp % 60;
        timeStamp /= 60;
        long minute = timeStamp % 60;
        timeStamp /= 60;
        long hour = timeStamp % 24;
        timeStamp /= 24;
        long date = timeStamp % 31 + 1;
        timeStamp /= 31;
        long month = timeStamp % 12;
        timeStamp /= 12;
        long year = timeStamp + 100;
        System.out.println(year);
        Timestamp time = new Timestamp((int) year, (int) month, (int) date, (int) hour, (int) minute, (int) second, 0);
        Date d = new Date();
        d.setTime( time.getTime());
        return d;
    }
}
