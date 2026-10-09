package com.devon.building.enums;

import java.util.LinkedHashMap;
import java.util.Map;

public enum CustomerStatus {
    CHUA_XU_LY("Chưa xử lý"),
    DANG_XU_LY("Đang xử lý"),
    DA_XU_LY("Đã xử lý");
    private final String status;
    CustomerStatus(String status){
        this.status = status;
    }

    public String getDisplayName() {
        return status;
    }

    public static Map<String,String> getStatusMap(){
        Map<String, String> statusMap = new LinkedHashMap<>();
        for(CustomerStatus status:CustomerStatus.values()){
            statusMap.put(status.toString(), status.status);
        }
        return statusMap;
    }
}
