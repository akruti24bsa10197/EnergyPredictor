import java.util.*;

public class InsightGenerator {

    public static String bestEnergyDay(List<EnergyEntry> entries) {
        if (entries.isEmpty()) return "No data";

        EnergyEntry best = entries.get(0);
        for (EnergyEntry e : entries) {
            if (e.getEnergyScore() > best.getEnergyScore()) {
                best = e;
            }
        }
        return best.getDate() + " (" + best.getEnergyScore() + ")";
    }

    public static String worstEnergyDay(List<EnergyEntry> entries) {
        if (entries.isEmpty()) return "No data";

        EnergyEntry worst = entries.get(0);
        for (EnergyEntry e : entries) {
            if (e.getEnergyScore() < worst.getEnergyScore()) {
                worst = e;
            }
        }
        return worst.getDate() + " (" + worst.getEnergyScore() + ")";
    }

    public static double averageSleep(List<EnergyEntry> entries) {
        if (entries.isEmpty()) return 0;

        double sum = 0;
        for (EnergyEntry e : entries) {
            sum += e.getSleepHours();
        }
        return sum / entries.size();
    }

    public static double averageMood(List<EnergyEntry> entries) {
        if (entries.isEmpty()) return 0;

        double sum = 0;
        for (EnergyEntry e : entries) {
            sum += e.getMood();
        }
        return sum / entries.size();
    }

    public static double averageProductivity(List<EnergyEntry> entries) {
        if (entries.isEmpty()) return 0;

        double sum = 0;
        for (EnergyEntry e : entries) {
            sum += e.getProductivity();
        }
        return sum / entries.size();
    }

    public static String mostCommonPeakTime(List<EnergyEntry> entries) {
        if (entries.isEmpty()) return "No data";

        Map<String, Integer> map = new HashMap<>();

        for (EnergyEntry e : entries) {
            map.put(e.getPeakTime(), map.getOrDefault(e.getPeakTime(), 0) + 1);
        }

        String best = "";
        int max = 0;

        for (String key : map.keySet()) {
            if (map.get(key) > max) {
                max = map.get(key);
                best = key;
            }
        }
        return best;
    }

    public static int highCaffeineDays(List<EnergyEntry> entries) {
        int count = 0;

        for (EnergyEntry e : entries) {
            if (e.getCaffeineIntake() >= 3) {
                count++;
            }
        }
        return count;
    }
}
