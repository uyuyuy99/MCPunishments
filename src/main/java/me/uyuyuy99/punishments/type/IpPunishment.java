package me.uyuyuy99.punishments.type;

import java.util.UUID;

public abstract class IpPunishment {

    private String ip;
    private String reason;

    public IpPunishment(String ip, String reason) {
        this.ip = ip;
        this.reason = reason;
    }

    public String getIp() {
        return ip;
    }

    public String getReason() {
        return reason;
    }

}
