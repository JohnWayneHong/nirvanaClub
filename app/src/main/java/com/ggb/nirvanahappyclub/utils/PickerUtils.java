package com.ggb.nirvanahappyclub.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.OnDismissListener;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PickerUtils {
public static final  String PATTERN_YEAR="yyyy";
public static final  String PATTERN_CHINESE_YEAR="yyyy年";
public static final  String PATTERN_MONTH="yyyy-MM";
public static final  String PATTERN_CHINESE_MONTH="yyyy年MM月";
public static final  String PATTERN_DAY="yyyy-MM-dd";
public static final  String PATTERN_CHINESE_DAY="yyyy年MM月dd日";
public static final  String PATTERN_HOUR="yyyy-MM-dd HH";
public static final  String PATTERN_MINUTE="yyyy-MM-dd HH:mm";
public static final  String PATTERN_SECOND="yyyy-MM-dd HH:mm:ss";
    public static void showTimePicker(Context context, onTimePickedListener listener, onCancelPickedListener cancelListener) {
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.set(2010, 0, 1);

        TimePickerView pickerView = new TimePickerView.Builder(context, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                String time = sdf.format(date);
                listener.onTimePicked(time);
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("年", "月", "日", "时", "分", "秒")
                .isCenterLabel(false)
                .setDividerColor(ResourcesUtils.getColor(com.jgw.common_library.R.color.gray_ee))
                .setTitleBgColor(Color.WHITE)
                .setCancelColor(ResourcesUtils.getColor(com.jgw.common_library.R.color.main_color))
                .setSubmitColor(ResourcesUtils.getColor(com.jgw.common_library.R.color.main_color))
                .setTextColorCenter(ResourcesUtils.getColor(com.jgw.common_library.R.color.gray_33))
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLineSpacingMultiplier(1.8f)
                .setDecorView(null)
                .build();
        if (cancelListener != null) {
            pickerView.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(Object o) {
                    cancelListener.onCancelPicked();
                }
            });
        }
        pickerView.show();
    }

    public static void showTimePicker(Context context, onTimePickedListener listener) {
        showTimePicker(context, listener, null);
    }

    /**
     * 至未来某日的timePicker
     */
    public static void showTimePicker(Date startTime, Date endTime, Context context, onTimePickedListener listener) {
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);
//        startDate.setTime(startDate.getTime());
        Calendar endDate = Calendar.getInstance();

        if (startTime != null) {
            startDate.setTime(startTime);
        } else {
            startDate.set(Calendar.YEAR, 2000);
            startDate.set(Calendar.MONTH, 0);
            startDate.set(Calendar.DAY_OF_MONTH, 1);
        }
        // 月份从0开始
        if (endTime != null) {
            endDate.setTime(endTime);
        }
        if (selectedDate.getTime().getTime() > endDate.getTime().getTime()) {
            selectedDate.setTime(endDate.getTime());
        }


        new TimePickerView.Builder(context, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                String time = sdf.format(date);
                listener.onTimePicked(time);
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("年", "月", "日", "时", "分", "秒")
                .isCenterLabel(false)
                .setDividerColor(ResourcesUtils.getColor(com.jgw.common_library.R.color.gray_ee))
                .setTitleBgColor(Color.WHITE)
                .setCancelColor(ResourcesUtils.getColor(com.jgw.common_library.R.color.main_color))
                .setSubmitColor(ResourcesUtils.getColor(com.jgw.common_library.R.color.main_color))
                .setTextColorCenter(ResourcesUtils.getColor(com.jgw.common_library.R.color.gray_33))
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLineSpacingMultiplier(1.8f)
                .setDecorView(null)
                .build()
                .show();
    }

    public static void showDataPicker(Context context, List<String> list, onDataPickedListener listener) {
        if (list.isEmpty()) {
            ToastUtils.showToast("列表为空");
            return;
        }
        OptionsPickerView pickerView = new OptionsPickerView.Builder(context, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                listener.onDataPicked(options1);
            }
        })
                .setLineSpacingMultiplier(1.8f)
                .setTitleBgColor(Color.WHITE)
                .setCancelColor(ResourcesUtils.getColor(com.jgw.common_library.R.color.main_color))
                .setSubmitColor(ResourcesUtils.getColor(com.jgw.common_library.R.color.main_color))
                .setDividerColor(ResourcesUtils.getColor(com.jgw.common_library.R.color.gray_ee))
                .setSelectOptions(0)
                .build();
        pickerView.setPicker(list);
        pickerView.show();
    }

    public interface onTimePickedListener {

        void onTimePicked(String time);
    }

    public interface onCancelPickedListener {

        void onCancelPicked();
    }

    public interface onDataPickedListener {

        void onDataPicked(int position);
    }
    public static void showTimePicker(@Nullable Calendar startDate, @Nullable Calendar endDate,
                                      @Nullable Calendar selectDate, @NonNull Context context,@NonNull onTimePickedListener listener){
        showTimePicker(startDate,endDate,selectDate,PATTERN_DAY,context,listener,null);
    }

    /**
     *
     * @param startDate 起始范围
     * @param endDate 结束范围
     * @param selectDate 选中的时间
     * @param pattern 根据日期正则解析对应要显示的单位
     * @param context 上下文
     * @param listener 选中时间监听
     * @param cancelListener dialig取消监听
     */
    public static void showTimePicker(@Nullable Calendar startDate, @Nullable Calendar endDate, @Nullable Calendar selectDate,
                                      @NonNull String pattern, @NonNull Context context,
                                      @NonNull onTimePickedListener listener,@Nullable onCancelPickedListener cancelListener) {
        if (startDate == null) {
            startDate = Calendar.getInstance();
            startDate.set(Calendar.YEAR, 2000);
            startDate.set(Calendar.MONTH, 0);
            startDate.set(Calendar.DAY_OF_MONTH, 1);
        }
        if (endDate == null) {
            endDate = Calendar.getInstance();
        }
        if (selectDate == null) {
            selectDate = Calendar.getInstance();
        }
        if (selectDate.getTime().getTime()<startDate.getTime().getTime()){
            selectDate=startDate;
        }
        if (selectDate.getTime().getTime()>endDate.getTime().getTime()){
            selectDate=endDate;
        }
        boolean[] booleans=getFormatByPattern(pattern);
        if (booleans==null){
            ToastUtils.showToast("不支持的时间格式");
            return;
        }
        TimePickerView pickerView = new TimePickerView.Builder(context, (date, v) -> {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.CHINA);
            String time = sdf.format(date);
            listener.onTimePicked(time);
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(booleans)
                .setLabel("年", "月", "日", "时", "分", "秒")
                .isCenterLabel(false)
                .setDividerColor(ResourcesUtils.getColor(com.jgw.common_library.R.color.gray_ee))
                .setTitleBgColor(Color.WHITE)
                .setCancelColor(ResourcesUtils.getColor(com.jgw.common_library.R.color.main_color))
                .setSubmitColor(ResourcesUtils.getColor(com.jgw.common_library.R.color.main_color))
                .setTextColorCenter(ResourcesUtils.getColor(com.jgw.common_library.R.color.gray_33))
                .setDate(selectDate)
                .setRangDate(startDate, endDate)
                .setLineSpacingMultiplier(1.8f)
                .setDecorView(null)
                .build();
        pickerView
                .show();

        if (cancelListener != null) {
            pickerView.setOnDismissListener(o -> cancelListener.onCancelPicked());
        }
    }

    private static boolean[] getFormatByPattern(String pattern) {
        boolean year,month,day,hour,min,seconds;
        switch (pattern){
            case PATTERN_YEAR:
                year=true;
                month=false;
                day=false;
                hour=false;
                min=false;
                seconds=false;
                break;
            case PATTERN_MONTH:
                year=true;
                month=true;
                day=false;
                hour=false;
                min=false;
                seconds=false;
                break;
            case PATTERN_DAY:
                year=true;
                month=true;
                day=true;
                hour=false;
                min=false;
                seconds=false;
                break;
            case PATTERN_HOUR:
                year=true;
                month=true;
                day=true;
                hour=true;
                min=false;
                seconds=false;
                break;
            case PATTERN_MINUTE:
                year=true;
                month=true;
                day=true;
                hour=true;
                min=true;
                seconds=false;
                break;
            case PATTERN_SECOND:
                year=true;
                month=true;
                day=true;
                hour=true;
                min=true;
                seconds=true;
                break;
            default:
                return null;
        }
        return new boolean[]{year, month, day, hour, min, seconds};
    }

}
