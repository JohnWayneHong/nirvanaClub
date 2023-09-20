package com.ggb.nirvanahappyclub.utils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.scan_code_library.VoiceHelp;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by XiongShaoWu
 * on 2019/11/5
 */
public class ScanCodeService {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    @SuppressLint("StaticFieldLeak")
    private static VoiceHelp voiceHelp;

    //IDATA
    private static final String IDATA_ACTION = "android.intent.action.SCANRESULT";
    private static final String IDATA_RESULT = "value";

    //Supoin
    private static final String SUPOIN_ACTION = "com.android.server.scannerservice.broadcast";
    private static final String SUPOIN_RESULT = "scannerdata";

    //Seuic
    private static final String SEUIC_CUSTOM_ACTION = "com.jgw.luchuanzhu";
    private static final String SEUIC_RESULT = "scannerdata";
    //Seuic 发送广播应用到系统应用 修改设置广播接收扫码结果
    private static final String SEUIC_SET_BROADCAST = "com.android.scanner.service_settings";
    private static final String SEUIC_BROADCAST_KEY = "action_barcode_broadcast";
    private static final String SEUIC_SEND_BARCIDE_MODE = "barcode_send_mode";
    private static final String SEUIC_END_KEY = "endchar";

    //C9000
    private static final String C9000_ACTION = "com.scanner.broadcast";
    private static final String C9000_RESULT = "data";

    public static final String QR_CODE_SCAN_ACTION = "com.scanner.broadcast";
    //    public static final String RFID_SCAN_ACTION = "android.intent.action.scanner.RFID";
    public static final String RFID_SCAN_ACTION = "com.idata.uhfdata";
    public static final String SCAN_EX_SCODE = "value";


    private static final boolean mIDataEnable = true;
    private static final boolean mSupoinEnable = true;
    private static final boolean mSeuicEnable = true;
    private static final boolean mC9000Enable = true;


    public static void init(Context context) {
        initSeuicConfig(context);

        initIntentFilter(context);

        voiceHelp = new VoiceHelp(context, true, false);
        mContext = context;
    }

    private static void initIntentFilter(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        if (mIDataEnable) {
            intentFilter.addAction(IDATA_ACTION);
        }
        if (mSupoinEnable) {
            intentFilter.addAction(SUPOIN_ACTION);
        }
        if (mSeuicEnable) {
            intentFilter.addAction(SEUIC_CUSTOM_ACTION);
        }
        if (mC9000Enable) {
            intentFilter.addAction(C9000_ACTION);
        }
        intentFilter.addAction(QR_CODE_SCAN_ACTION);
        intentFilter.addAction(RFID_SCAN_ACTION);
        //注册广播接受者
        PDAScanReceiver mPDAReceiver = new PDAScanReceiver();
        context.registerReceiver(mPDAReceiver, intentFilter);
    }

    private static void initSeuicConfig(Context context) {
        //Seuic相关配置-------------Seuic相关配置
        Intent intent = new Intent(SEUIC_SET_BROADCAST);
        // 修改广播名称
        intent.putExtra(SEUIC_BROADCAST_KEY, SEUIC_CUSTOM_ACTION);
        // 设置条码发送方式为广播模式
        intent.putExtra(SEUIC_SEND_BARCIDE_MODE, "BROADCAST");
        // 设置条码结束符为none
        intent.putExtra(SEUIC_END_KEY, "NONE");
        context.sendBroadcast(intent);
        //Seuic相关配置-------------Seuic相关配置
    }

    public static void autoScan(String input) {
        if (TextUtils.isEmpty(input)) {
            return;
        }
        if (input.contains("http")) {
            EventBus.getDefault().post(new CommonEvent.ScanQRCodeEvent(input));
            return;
        }
        if (input.contains(":")) {
            String[] split = input.split(":");
            String code = split[0];
            String number = split[1];
            ArrayList<String> codes = new ArrayList<>();
            for (int i = 0; i < Integer.parseInt(number); i++) {
                String realCode = String.valueOf(Long.parseLong(code) + i);
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

    public static class PDAScanReceiver extends BroadcastReceiver {

        long lastTime;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String resultCode = "";
            if (!TextUtils.isEmpty(action)) {
                //noinspection ConstantConditions
                switch (action) {
                    case IDATA_ACTION:
                        resultCode = intent.getStringExtra(IDATA_RESULT);
                        break;
                    case SUPOIN_ACTION:
                        resultCode = intent.getStringExtra(SUPOIN_RESULT);
                        break;
                    case SEUIC_CUSTOM_ACTION:
                        resultCode = intent.getStringExtra(SEUIC_RESULT);
                        if (TextUtils.isEmpty(resultCode)) {
                            resultCode = intent.getStringExtra("value");
                        }
                        break;
                    case C9000_RESULT:
                        resultCode = intent.getStringExtra(C9000_RESULT);
                        break;
                    case QR_CODE_SCAN_ACTION:
                        resultCode = intent.getStringExtra(SCAN_EX_SCODE);
                        break;
                    case RFID_SCAN_ACTION:
                        final String RFIDCode = intent.getStringExtra(SCAN_EX_SCODE);
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastTime > 50) {
                            lastTime = currentTime;
                            EventBus.getDefault().post(new CommonEvent.ScanRFIDEvent(RFIDCode));
                        }
                        break;
                }
            }
            if (!TextUtils.isEmpty(resultCode)) {
                EventBus.getDefault().post(new CommonEvent.ScanQRCodeEvent(resultCode));
            }
        }
    }

    public static void playSuccess() {
        if (voiceHelp != null) {
            voiceHelp.playSuccess();
        }
    }

    public static void playError() {
        if (voiceHelp != null) {
            voiceHelp.playError();
        }
    }

    public static void releaseRes() {
        if (voiceHelp != null) {
            voiceHelp.releaseSoundPool();
        }

        if (mContext != null) {
            mContext = null;
        }
    }
}
