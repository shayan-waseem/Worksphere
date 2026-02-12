package worksphere.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String formatTime(LocalDateTime time) {
        return time != null ? time.format(timeFormatter) : "";
    }

    public static String formatDate(LocalDateTime date) {
        return date != null ? date.format(dateFormatter) : "";
    }
}