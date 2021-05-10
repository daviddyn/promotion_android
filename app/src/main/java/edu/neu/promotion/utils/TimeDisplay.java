package edu.neu.promotion.utils;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import edu.neu.promotion.R;

public class TimeDisplay {

    private static SimpleDateFormat hourMinuteFormat;
    private static SimpleDateFormat yesterdayHourMinuteFormat;
    private static SimpleDateFormat yesteryesterdayHourMinuteFormat;
    private static SimpleDateFormat monthDateHourMinuteFormat;
    private static SimpleDateFormat yearMonthDateHourMinuteFormat;

    private static void initializeDateFormat(Context context) {
        if (hourMinuteFormat == null) {
            hourMinuteFormat = new SimpleDateFormat(context.getString(R.string.time_format_hour_minute), Locale.CHINA);
            yesterdayHourMinuteFormat = new SimpleDateFormat(context.getString(R.string.time_format_yesterday_hour_minute), Locale.CHINA);
            yesteryesterdayHourMinuteFormat = new SimpleDateFormat(context.getString(R.string.time_format_yesteryesterday_hour_minute), Locale.CHINA);
            monthDateHourMinuteFormat = new SimpleDateFormat(context.getString(R.string.time_format_month_date_hour_minute), Locale.CHINA);
            yearMonthDateHourMinuteFormat = new SimpleDateFormat(context.getString(R.string.time_format_year_month_date_hour_minute), Locale.CHINA);
        }
    }

    public String showRelative(Context context, long timeMills) {
        //规则：
        //1. 时间位于当前时间之后：
        //  1.1 当天时间，则显示“HH:mm”
        //  1.2 当年时间，则显示“M月d日 HH:mm”
        //  1.3 更远时间，则显示“Y年M月d日 HH:mm”
        //2. 时间位于当前时间之前(含)：
        //  2.1 1分钟以内，则显示“刚刚”
        //  2.2 1小时以内，则显示“x分钟前”
        //  2.3 2小时以内，则显示“x小时前”
        //  2.4 当天以内，则显示“HH:mm”
        //  2.5 当天的前1天以内，则显示“昨天 HH:mm”
        //  2.6 当天的前2天以内，则显示“前天 HH:mm”
        //  2.7 当年以内，则显示“M月d日 HH:mm”
        //  2.8 更远时间，则显示“Y年M月d日 HH:mm”
        long now = System.currentTimeMillis();
        initializeDateFormat(context);
        if (timeMills > now) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(now);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.add(Calendar.DATE, 1);
            long tomorrow = calendar.getTimeInMillis();
            if (timeMills < tomorrow) {
                return hourMinuteFormat.format(timeMills);
            }
            calendar.setTimeInMillis(now);
            int currentYear = calendar.get(Calendar.YEAR);
            calendar.setTimeInMillis(timeMills);
            if (currentYear == calendar.get(Calendar.YEAR)) {
                return monthDateHourMinuteFormat.format(timeMills);
            }
            else {
                return yearMonthDateHourMinuteFormat.format(timeMills);
            }
        }
        else {
            long diff = now - timeMills;
            if (diff < 60000) {
                return context.getString(R.string.time_moments_ago);
            }
            if (diff < 3600000) {
                return context.getString(R.string.time_minutes_ago, diff / 60000);
            }
            if (diff < 7200000) {
                return context.getString(R.string.time_hours_ago, diff / 3600000);
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(now);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long bar = calendar.getTimeInMillis();
            if (timeMills >= bar) {
                return hourMinuteFormat.format(timeMills);
            }
            calendar.add(Calendar.DATE, -1);
            bar = calendar.getTimeInMillis();
            if (timeMills >= bar) {
                return yesterdayHourMinuteFormat.format(timeMills);
            }
            calendar.add(Calendar.DATE, -1);
            bar = calendar.getTimeInMillis();
            if (timeMills >= bar) {
                return yesteryesterdayHourMinuteFormat.format(timeMills);
            }
            calendar.setTimeInMillis(now);
            int currentYear = calendar.get(Calendar.YEAR);
            calendar.setTimeInMillis(timeMills);
            if (currentYear == calendar.get(Calendar.YEAR)) {
                return monthDateHourMinuteFormat.format(timeMills);
            }
            else {
                return yearMonthDateHourMinuteFormat.format(timeMills);
            }
        }
    }
}
