package com.jgw.delingha.bean;

import android.text.TextUtils;

import java.util.List;

public class CommonOrderStockOutDetailsBean {

    public List<ListBean> houseListProducts;

    public static class ListBean {
        private ProductBean product;
        private int singleCodeNumber;//计划数量

        private int currentInputSingleNumber;//当前手输数量
        private int tempInputSingleNumber;//临时手输数量上传成功后赋值
        private int currentSingleNumber;//当前扫码数量
        private int tempSingleNumber;//当前扫码数量

        private int actualSingleCodeNumber;//实际扫码单码数量(扫码+手输) 已上传数量
        private String productRecordId;//产品记录id
        private String productBatch;
        private String productBatchId;
        private String taskId;

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public int getTempSingleNumber() {
            return tempSingleNumber;
        }

        public void setTempSingleNumber(int tempSingleNumber) {
            this.tempSingleNumber = tempSingleNumber;
        }

        public int getTempInputSingleNumber() {
            return tempInputSingleNumber;
        }

        public void setTempInputSingleNumber(int tempInputSingleNumber) {
            this.tempInputSingleNumber = tempInputSingleNumber;
        }

        public String getSinglePlanNumberText() {
            return singleCodeNumber + "";
        }

        public int getSingleCodeNumber() {
            return singleCodeNumber;
        }

        public int getCurrentInputSingleNumber() {
            return currentInputSingleNumber;
        }

        public int getCurrentSingleNumber() {
            return currentSingleNumber;
        }

        //当前扫码+手输数量
        public int getCurrentNumber() {
            return getCurrentSingleNumber() + getCurrentInputSingleNumber();
        }

        //当前扫码+手输数量+已上传数量
        public int getTotalSingleNumber() {
            return getCurrentNumber() + getActualSingleCodeNumber()+getTempSingleNumber();
        }

        public void setSingleCodeNumber(int singleCodeNumber) {
            this.singleCodeNumber = singleCodeNumber;
        }

        public void setCurrentInputSingleNumber(int currentInputSingleNumber) {
            this.currentInputSingleNumber = currentInputSingleNumber;
        }

        public void setCurrentSingleNumber(int currentSingleNumber) {
            this.currentSingleNumber = currentSingleNumber;
        }

        public int getActualSingleCodeNumber() {
            return actualSingleCodeNumber;
        }
        public String getActualSingleCodeNumberText() {
            return actualSingleCodeNumber+"";
        }

        public void setActualSingleCodeNumber(int actualSingleCodeNumber) {
            this.actualSingleCodeNumber = actualSingleCodeNumber;
        }

        public String getProductRecordId() {
            return productRecordId;
        }

        public void setProductRecordId(String productRecordId) {
            this.productRecordId = productRecordId;
        }

        public String getProductBatch() {
            return productBatch;
        }

        public void setProductBatch(String productBatch) {
            this.productBatch = productBatch;
        }

        public String getProductBatchId() {
            return productBatchId;
        }

        public void setProductBatchId(String productBatchId) {
            this.productBatchId = productBatchId;
        }

        public String getProductCode() {
            return product.getProductCode();
        }

        public String getProductId() {
            return product.getProductId();
        }

        public String getProductName() {
            return product.getProductName();
        }

        public String getWareHouseId() {
            return warehouse.getWareHouseId();
        }

        public String getWareHouseName() {
            return warehouse.getWareHouseName();
        }
        public String getWareHouseCode() {
            return warehouse.getWareHouseCode();
        }

        public String getCurrentNumberText() {
            return getCurrentNumber() + "";
        }

        public ProductBean getProduct() {
            return product;
        }

        public void setProduct(ProductBean product) {
            this.product = product;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ListBean listBean = (ListBean) o;
            return TextUtils.equals(productRecordId, listBean.productRecordId);
        }

        private WarehouseBean warehouse;

        public WarehouseBean getWarehouse() {
            return warehouse;
        }

        public void setWarehouse(WarehouseBean warehouse) {
            this.warehouse = warehouse;
        }

        public static class ProductBean {
            private String productCode;
            private String productId;
            private String productName;
            private ProductWareHouseBean productWareHouse;

            public String getProductCode() {
                return productCode;
            }

            public void setProductCode(String productCode) {
                this.productCode = productCode;
            }

            public String getProductId() {
                return productId;
            }

            public void setProductId(String productId) {
                this.productId = productId;
            }

            public String getProductName() {
                return productName;
            }

            public void setProductName(String productName) {
                this.productName = productName;
            }

            public ProductWareHouseBean getProductWareHouse() {
                return productWareHouse;
            }

            public void setProductWareHouse(ProductWareHouseBean productWareHouse) {
                this.productWareHouse = productWareHouse;
            }


            public static class ProductWareHouseBean {
                private List<ProductPackageRatiosBean> productPackageRatios;

                public List<ProductPackageRatiosBean> getProductPackageRatios() {
                    return productPackageRatios;
                }

                public void setProductPackageRatios(List<ProductPackageRatiosBean> productPackageRatios) {
                    this.productPackageRatios = productPackageRatios;
                }

                public static class ProductPackageRatiosBean {
                    private String farentId;
                    private int firstNumber;
                    private String firstNumberCode;
                    private String firstNumberName;
                    private int lastNumber;
                    private String lastNumberCode;
                    private String lastNumberName;
                    private String packageSpecificationName;
                    private String productPackageRatioId;
                    private String relationTypeCode;
                    private String relationTypeName;

                    public String getFarentId() {
                        return farentId;
                    }

                    public void setFarentId(String farentId) {
                        this.farentId = farentId;
                    }

                    public int getFirstNumber() {
                        return firstNumber;
                    }

                    public void setFirstNumber(int firstNumber) {
                        this.firstNumber = firstNumber;
                    }

                    public String getFirstNumberCode() {
                        return firstNumberCode;
                    }

                    public void setFirstNumberCode(String firstNumberCode) {
                        this.firstNumberCode = firstNumberCode;
                    }

                    public String getFirstNumberName() {
                        return firstNumberName;
                    }

                    public void setFirstNumberName(String firstNumberName) {
                        this.firstNumberName = firstNumberName;
                    }

                    public int getLastNumber() {
                        return lastNumber;
                    }

                    public void setLastNumber(int lastNumber) {
                        this.lastNumber = lastNumber;
                    }

                    public String getLastNumberCode() {
                        return lastNumberCode;
                    }

                    public void setLastNumberCode(String lastNumberCode) {
                        this.lastNumberCode = lastNumberCode;
                    }

                    public String getLastNumberName() {
                        return lastNumberName;
                    }

                    public void setLastNumberName(String lastNumberName) {
                        this.lastNumberName = lastNumberName;
                    }

                    public String getPackageSpecificationName() {
                        return packageSpecificationName;
                    }

                    public void setPackageSpecificationName(String packageSpecificationName) {
                        this.packageSpecificationName = packageSpecificationName;
                    }

                    public String getProductPackageRatioId() {
                        return productPackageRatioId;
                    }

                    public void setProductPackageRatioId(String productPackageRatioId) {
                        this.productPackageRatioId = productPackageRatioId;
                    }

                    public String getRelationTypeCode() {
                        return relationTypeCode;
                    }

                    public void setRelationTypeCode(String relationTypeCode) {
                        this.relationTypeCode = relationTypeCode;
                    }

                    public String getRelationTypeName() {
                        return relationTypeName;
                    }

                    public void setRelationTypeName(String relationTypeName) {
                        this.relationTypeName = relationTypeName;
                    }
                }
            }

        }

        public static class WarehouseBean {
            private String disableFlag;
            private String storeHouseCode;
            private String storeHouseId;
            private String storeHouseName;
            private int storeHouseNumber;
            private String wareHouseCode;
            private String wareHouseId;
            private String wareHouseName;


            public String getDisableFlag() {
                return disableFlag;
            }

            public void setDisableFlag(String disableFlag) {
                this.disableFlag = disableFlag;
            }

            public String getStoreHouseCode() {
                return storeHouseCode;
            }

            public void setStoreHouseCode(String storeHouseCode) {
                this.storeHouseCode = storeHouseCode;
            }

            public String getStoreHouseId() {
                return storeHouseId;
            }

            public void setStoreHouseId(String storeHouseId) {
                this.storeHouseId = storeHouseId;
            }

            public String getStoreHouseName() {
                return storeHouseName;
            }

            public void setStoreHouseName(String storeHouseName) {
                this.storeHouseName = storeHouseName;
            }

            public int getStoreHouseNumber() {
                return storeHouseNumber;
            }

            public void setStoreHouseNumber(int storeHouseNumber) {
                this.storeHouseNumber = storeHouseNumber;
            }

            public String getWareHouseCode() {
                return wareHouseCode;
            }

            public void setWareHouseCode(String wareHouseCode) {
                this.wareHouseCode = wareHouseCode;
            }

            public String getWareHouseId() {
                return wareHouseId;
            }

            public void setWareHouseId(String wareHouseId) {
                this.wareHouseId = wareHouseId;
            }

            public String getWareHouseName() {
                return wareHouseName;
            }

            public void setWareHouseName(String wareHouseName) {
                this.wareHouseName = wareHouseName;
            }
        }
    }
}
