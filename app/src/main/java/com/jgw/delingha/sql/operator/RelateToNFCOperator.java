package com.jgw.delingha.sql.operator;

import androidx.annotation.Nullable;

import com.jgw.delingha.sql.entity.RelateToNFCEntity;
import com.jgw.delingha.sql.entity.RelateToNFCEntity_;

import java.util.List;

/**
 * @author : J-T
 * @date : 2022/6/8 16:42
 * description :
 */
public class RelateToNFCOperator extends BaseNoConfigCodeOperator<RelateToNFCEntity> {

    @Override
    public void deleteAllByCurrentUser() {
        deleteAllByCurrentUser(RelateToNFCEntity_.userEntityId);
    }

    @Override
    public RelateToNFCEntity queryEntityByCode(String code) {
        return null;
    }

    @Override
    public long removeEntityByCode(String code) {
        return 0;
    }

    @Nullable
    @Override
    public List<RelateToNFCEntity> queryListByPage(int page, int pageSize) {
        return queryListByPage(RelateToNFCEntity_.userEntityId, RelateToNFCEntity_.id, page, pageSize);
    }

    public boolean checkIsRepeatQrCode(String qrCode) {
        RelateToNFCEntity entity = queryEntityByString(RelateToNFCEntity_.QRCode, qrCode);
        return entity != null;
    }

    public boolean checkIsRepeatNFCCode(String nfcCode) {
        return queryEntityByString(RelateToNFCEntity_.NFCCode, nfcCode) != null;
    }

    @Override
    public long queryCount() {
        return queryCountByCurrentUser(RelateToNFCEntity_.userEntityId);
    }

    @Nullable
    @Override
    public List<RelateToNFCEntity> queryListByCount(int count) {
        return queryListByCount(RelateToNFCEntity_.userEntityId, RelateToNFCEntity_.id, count);
    }

    @Nullable
    public RelateToNFCEntity searchEntityByQRCode(String code) {
        return queryEntityByString(RelateToNFCEntity_.QRCode, code);
    }

    @Nullable
    public RelateToNFCEntity searchEntityByNFCCode(String code) {
        return queryEntityByString(RelateToNFCEntity_.NFCCode, code);
    }

    /**
     * 根据QRcode删除某一条数据
     */
    public void deleteByQRCode(String code) {
        removeEntityByString(RelateToNFCEntity_.QRCode, code);
    }

    /**
     * 根据NFCcode删除某一条数据
     */
    public void deleteByNFCCode(String code) {
        removeEntityByString(RelateToNFCEntity_.NFCCode, code);
    }

    @Nullable
    public List<RelateToNFCEntity> queryGroupDataByUserId(long currentId, int limit) {
        return queryGroupDataByUserId(RelateToNFCEntity_.userEntityId, RelateToNFCEntity_.id, currentId, limit);
    }
}
