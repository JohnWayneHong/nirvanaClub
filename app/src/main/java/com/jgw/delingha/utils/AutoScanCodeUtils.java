package com.jgw.delingha.utils;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.utils.MathUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.InputScanCodeDialogBean;

import org.greenrobot.eventbus.EventBus;

import java.math.BigInteger;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AutoScanCodeUtils {

    public static boolean checkAutoScanCode(InputScanCodeDialogBean data) {
        String input1 = data.getInput1();
        String input2 = data.getInput2();
        String count = data.getCount();
        if (TextUtils.isEmpty(count) || Integer.parseInt(count) == 0) {
            ToastUtils.showToast("请扫码或输入正确的码数量");
            return false;
        }
        if (TextUtils.isEmpty(input1) && TextUtils.isEmpty(input2)) {
            ToastUtils.showToast("请扫码或输入正确的码");
            return false;
        }
        if ((!TextUtils.isEmpty(input1) && input1.length() != 16) ||
                (!TextUtils.isEmpty(input2) && input2.length() != 16)) {
            ToastUtils.showToast("请扫码或输入正确的码");
            return false;
        }
        return true;
    }

    public static void startAutoScan(InputScanCodeDialogBean data,CustomObserver<String> observer) {
        String input1 = data.getInput1();
        String input2 = data.getInput2();
        int count = Integer.parseInt(data.getCount());
        if (TextUtils.isEmpty(input2)) {
            AutoScanCodeUtils.autoScanStartCode(input1, count, observer);
            return;
        }
        if (TextUtils.isEmpty(input1)) {
            AutoScanCodeUtils.autoScanEndCode(input2, count, observer);
            return;
        }
        BigInteger startCode = new BigInteger(input1);
        BigInteger endCode = new BigInteger(input2);
        BigInteger subtract = endCode.subtract(startCode);
        int i = subtract.intValue();
        long abs = Math.abs(i) + 1;
        if (abs > 1000) {
            ToastUtils.showToast("码数量过大不能录入!");
            return;
        }
        if (i > 0) {
            AutoScanCodeUtils.autoScanStartCode(input1, Math.abs(i)+1, observer);
        } else if (i < 0) {
            AutoScanCodeUtils.autoScanEndCode(input1, Math.abs(i)+1, observer);
        } else {
            AutoScanCodeUtils.autoScanStartCode(input1, 1, observer);
        }
    }

    public static void autoScan(String input) {
        if (TextUtils.isEmpty(input)){
            return;
        }
        if (input.contains("http")){
            EventBus.getDefault().post(new CommonEvent.ScanQRCodeEvent(input));
            return;
        }
        if (input.contains(":")) {
            String[] split = input.split(":");
            String code = split[0];
            String number = split[1];
            ArrayList<String> codes = new ArrayList<>();
            for (int i = 0; i < Integer.parseInt(number); i++) {
                String realCode = MathUtils.bigNumberAdd(code,i+"");
                codes.add(realCode);
            }
            Observable.fromIterable(codes)
                    .map(s -> {
                        Thread.sleep(200);
                        return s;
                    })
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CustomObserver<String>() {
                        @Override
                        public void onNext(@NonNull String s) {
                            EventBus.getDefault().post(new CommonEvent.ScanQRCodeEvent(s));
                        }
                    });
        } else {
            EventBus.getDefault().post(new CommonEvent.ScanQRCodeEvent(input));
        }
    }


    public static void autoScanStartCode(String startCode, int count, CustomObserver<String> observer) {
        ArrayList<String> codes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String code = MathUtils.bigNumberAdd(startCode, i + "");
            codes.add(code);
        }

        Observable.fromIterable(codes)
                .map(s -> {
                    Thread.sleep(200);
                    return s;
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public static void autoScanEndCode(String endCode, int count, CustomObserver<String> observer) {
        ArrayList<String> codes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String code = MathUtils.bigNumberSub(endCode, i + "");
            codes.add(code);
        }
        Observable.fromIterable(codes)
                .map(s -> {
                    Thread.sleep(200);
                    return s;
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

}
