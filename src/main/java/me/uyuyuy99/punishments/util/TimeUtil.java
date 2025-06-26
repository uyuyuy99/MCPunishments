package me.uyuyuy99.punishments.util;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {

    public static String formatTimeAbbr(final long seconds) {
        if (seconds == 0L) {
            return "0s";
        } else {
            long day = TimeUnit.SECONDS.toDays(seconds);
            long hours = TimeUnit.SECONDS.toHours(seconds) - day * 24L;
            long minutes = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.SECONDS.toHours(seconds) * 60L;
            long secs = TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.SECONDS.toMinutes(seconds) * 60L;

            StringBuilder sb = new StringBuilder();

            if (day > 0L) {
                sb.append(day).append("d").append(" ");
            }

            if (hours > 0L) {
                sb.append(hours).append("h").append(" ");
            }

            if (minutes > 0L) {
                sb.append(minutes).append("m").append(" ");
            }

            if (secs > 0L) {
                sb.append(secs).append("s");
            }

            String diff = sb.toString();
            return diff.isEmpty() ? "Now" : diff.trim();
        }
    }

    // Parsing & autofill suggestions for time-based commands
    private static final Pattern timePattern = Pattern.compile("^([0-9]+)([smhdw])$");
    public static Argument<Integer> arg(String nodeName) {
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            String input = info.input();
            Matcher matcher = timePattern.matcher(input);

            if (matcher.find()) {
                int value = Integer.parseInt(matcher.group(1));
                char unit = matcher.group(2).charAt(0);

                if (unit == 's') return value;
                if (unit == 'm') return value * 60;
                if (unit == 'h') return value * 60 * 60;
                if (unit == 'd') return value * 60 * 60 * 24;
                if (unit == 'w') return value * 60 * 60 * 24 * 7;
            }
            throw CustomArgument.CustomArgumentException.fromMessageBuilder(
                    new CustomArgument.MessageBuilder("Incorrect time format: ").appendArgInput());
        }).replaceSuggestions(ArgumentSuggestions.strings(info -> {
            if (info.currentInput().matches("^[0-9]+$")) {
                return new String[] { info.currentInput() + "s", info.currentInput() + "m", "h", "d", "w" };
            } else {
                return new String[]{};
            }
        }));
    }

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, ''yy 'at' HH:mm");
    public static String formatDate(long epochMs, String defaultText) {
        if (epochMs <= 0) return defaultText;
        return dateFormat.format(new Date(epochMs));
    }
    public static String formatDate(long epochMs) {
        return formatDate(epochMs, "");
    }

}
