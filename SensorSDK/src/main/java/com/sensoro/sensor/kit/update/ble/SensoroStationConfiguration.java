package com.sensoro.sensor.kit.update.ble;

/**
 * Created by sensoro on 16/8/16.
 */

public class SensoroStationConfiguration {

    String sn;
    String hardwareModelName;// hardware version.
    String firmwareVersion;// firmware version.
    int workStatus;
    int netStatus;
    String ip;
    String gateway;
    String mask;
    String pdns;
    String adns;
    String sid;
    String pwd;
    String encrpt;
    int accessMode;
    int allocationMode;
    String netid;
    String cloudaddress;
    String cloudport;
    String key;

    private SensoroStationConfiguration(Builder builder) {
        this.sn = builder.sn;
        this.accessMode = builder.accessMode;
        this.allocationMode = builder.allocationMode;
        this.ip = builder.ip;
        this.adns = builder.adns;
        this.mask = builder.mask;
        this.firmwareVersion = builder.firmwareVersion;
        this.encrpt = builder.encrpt;
        this.hardwareModelName = builder.hardwareModelName;
        this.workStatus = builder.workStatus;
        this.sid = builder.sid;
        this.pwd = builder.pwd;
        this.pdns = builder.pdns;
        this.gateway = builder.gateway;
        this.netid = builder.netid;
        this.cloudaddress = builder.cloudaddress;
        this.cloudport = builder.cloudport;
        this.key = builder.key;
    }

    public static class Builder {
        private String sn;
        private String hardwareModelName;// hardware version.
        private String firmwareVersion;// firmware version.
        private int workStatus;
        private int netStatus;
        private String ip;
        private String gateway;
        private String mask;
        private String pdns;
        private String adns;
        private String sid;
        private String pwd;
        private String encrpt;
        private int accessMode;
        private int allocationMode;
        private String netid;
        private String cloudaddress;
        private String cloudport;
        private String key;

        public Builder setSn(String sn) {
            this.sn = sn;
            return this;
        }


        public Builder setHardwareModelName(String name) {
            this.hardwareModelName = name;
            return this;
        }

        public Builder setFirmwareVersion(String version) {
            this.firmwareVersion = version;
            return this;
        }

        public Builder setAccessMode(int accessMode) {
            this.accessMode = accessMode;
            return this;
        }

        public Builder setAllocationMode(int assignment) {
            this.allocationMode = assignment;
            return this;
        }

        public Builder setIp(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder setMask(String mask) {
            this.mask = mask;
            return this;
        }

        public Builder setPdns(String pdns) {
            this.pdns = pdns;
            return this;
        }

        public Builder setAdns(String adns) {
            this.adns = adns;
            return this;
        }

        public Builder setSid(String sid) {
            this.sid = sid;
            return this;
        }

        public Builder setNetId(String netid) {
            this.netid = netid;
            return this;
        }
        public Builder setCloudAddress(String address) {
            this.cloudaddress = address;
            return this;
        }

        public Builder setCloudPort(String port) {
            this.cloudport = port;
            return this;
        }
        public Builder setKey(String key) {
            this.key = key;
            return this;
        }
        public Builder setPassword(String pwd) {
            this.pwd = pwd;
            return  this;
        }
        public Builder setEncrpt(String encrpt) {
            this.encrpt = encrpt;
            return this;
        }
        public Builder setRouter(String router) {
            this.gateway = router;
            return this;
        }
        public SensoroStationConfiguration build() {
            return new SensoroStationConfiguration(this);
        }
    }
}
