package com.sensoro.sensor.kit.callback;

public interface OnDeviceUpdateObserver {
    //正在切换至DFU
    void onEnteringDFU(String deviceMacAddress, String filePath, String msg);

    //升级完成
    void onUpdateCompleted(String filePath, String deviceMacAddress, String msg);

    //正在传输数据
    void onDFUTransfering(String deviceAddress, int percent, float speed, float avgSpeed, int
            currentPart, int partsTotal, String msg);

    //正在校验
    void onUpdateValidating(String deviceMacAddress, String msg);

    //业务超时
    void onUpdateTimeout(int code, Object data, String msg);

    //断开连接
    void onDisconnecting();

    //操作失败
    void onFailed(String deviceMacAddress, String errorMsg, Throwable e);

}
