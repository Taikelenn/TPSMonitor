package ee.kasumata.tpsmon;

import java.util.ArrayList;
import java.util.List;

public final class TickrateCalculator {
    private static final double TPS_NORM = 20.0;

    private static final Object UpdateLock = new Object();
    private static final List<Long> Measurements = new ArrayList<>();

    public static void reportReceivedPacket() {
        synchronized (UpdateLock) {
            Measurements.add(System.currentTimeMillis());

            if (Measurements.size() > 10) {
                // retain data from the last 10 seconds only
                Measurements.remove(0);
            }
        }
    }

    private static double getTPS(int averageOfSeconds) {
        synchronized (UpdateLock) {
            if (Measurements.size() < 2) {
                return 0.0; // we can't compare yet
            }

            long currentTimestamp = Measurements.get(Measurements.size() - 1);
            long previousTimestamp = Measurements.get(Measurements.size() - averageOfSeconds);

            return ((currentTimestamp - previousTimestamp) * TPS_NORM) / 1000.0;
        }
    }

    public static double getCurrentTPS() {
        return getTPS(2);
    }

    public static double getAverageTPS() {
        return getTPS(Measurements.size());
    }

    private TickrateCalculator() {
        // make the class non-instantiable
    }
}