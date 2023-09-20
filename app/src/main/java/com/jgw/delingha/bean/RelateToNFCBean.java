package com.jgw.delingha.bean;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.jgw.delingha.BR;

/**
 * @author : J-T
 * @date : 2022/6/8 16:59
 * description : 关联NFC bean
 */
public class RelateToNFCBean extends BaseObservable {
    /**
     * 展示的二维码
     */
    private String QRCode;
    private String NFCCode;
    private String RelateToNFCCount;

    public void setQRCode(String QRCode) {
        this.QRCode = QRCode;
        notifyPropertyChanged(BR.qRCode);
    }

    public void setNFCCode(String NFCCode) {
        this.NFCCode = NFCCode;
        notifyPropertyChanged(BR.nFCCode);
    }

    public void setRelateToNFCCount(String relateToNFCCount) {
        RelateToNFCCount = relateToNFCCount;
        notifyPropertyChanged(BR.relateToNFCCount);
    }

    @Bindable
    public String getQRCode() {
        return QRCode;
    }

    @Bindable
    public String getNFCCode() {
        return NFCCode;
    }

    @Bindable
    public String getRelateToNFCCount() {
        return RelateToNFCCount;
    }
}
