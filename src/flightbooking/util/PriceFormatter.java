package flightbooking.util;

public class PriceFormatter {

    public static String formatSeatPrice(long price) {

        if (price >= 1_000_000) {
            double v = price / 1_000_000.0;
            return String.format("%.3fM", v);
        }

        if (price >= 1000) {
            double v = price / 1000.0;
            return String.format("%.0fK", v);
        }

        return String.valueOf(price);
    }
}