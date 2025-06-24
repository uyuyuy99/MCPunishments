package me.uyuyuy99.punishments.type;

import lombok.Getter;

@Getter
public abstract class IpPunishment {

    private int id;
    private String ip;
    private String reason;

    public IpPunishment(int id, String ip, String reason) {
        this.id = id;
        this.ip = ip;
        this.reason = reason;
    }

}
