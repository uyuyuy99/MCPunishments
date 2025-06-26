package me.uyuyuy99.punishments.history;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HistoryRecord {

    private Type type;
    private long start;
    private long end;
    private String reason;
    private boolean valid;

    public HistoryRecord(String type, long start, long end, String reason, int valid) {
        this(Type.fromId(type), start, end, reason, valid > 0);
    }

    @Getter
    public enum Type {

        BAN("ban"),
        MUTE("mute"),
        KICK("kick");

        private String id;

        Type(String id) {
            this.id = id;
        }

        public static Type fromId(String id) {
            for (Type type : values()) {
                if (type.getId().equals(id)) return type;
            }
            return null;
        }

    }

}
