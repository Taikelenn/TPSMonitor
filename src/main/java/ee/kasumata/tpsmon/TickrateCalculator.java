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

            if (Measurements.size() > 15) {
                // retain data from the last 15 measurements
                Measurements.remove(0);
            }
        }
    }

    public static void reset() {
        synchronized (UpdateLock) {
            Measurements.clear();
        }
    }

    private static double getTPS(int averageOfSeconds) {
        synchronized (UpdateLock) {
            if (Measurements.size() < 2) {
                return 0.0; // we can't compare yet
            }

            long currentTimestamp = Measurements.get(Measurements.size() - 1);
            long previousTimestamp = Measurements.get(Measurements.size() - averageOfSeconds);

            // on average, how long did it take for 20 ticks to execute? (ideal value: 1 second)
            double longTickTime = Math.max((currentTimestamp - previousTimestamp) / (1000.0 * (averageOfSeconds - 1)), 1.0);
            return TPS_NORM / longTickTime;
        }
    }

    public static double getAverageTPS() {
        synchronized (UpdateLock) {
            return getTPS(Measurements.size());
        }
    }

    private TickrateCalculator() {
        // make the class non-instantiable
    }
}
