import java.util.*;

public class StatisticsUtil {

    public static double average(List<Integer> list) {
        if (list.isEmpty()) return 0;
        double sum = 0;
        for (int x : list) sum += x;
        return sum / list.size();
    }

    public static String classifyEnergy(int score) {
        if (score >= 70) return "High Energy";
        if (score >= 45) return "Moderate Energy";
        return "Low Energy";
    }

    public static double correlation(List<Integer> sleep, List<Integer> energy) {
        if (sleep.size() != energy.size() || sleep.size() < 2) return 0;

        double avgSleep = average(sleep);
        double avgEnergy = average(energy);
        double numerator = 0, denS = 0, denE = 0;

        for (int i = 0; i < sleep.size(); i++) {
            double ds = sleep.get(i) - avgSleep;
            double de = energy.get(i) - avgEnergy;

            numerator += ds * de;
            denS += ds * ds;
            denE += de * de;
        }

        double denominator = Math.sqrt(denS * denE);

        return (denominator == 0) ? 0 : numerator / denominator;
    }

    public static String productivityTier(double avg) {
        if (avg >= 4) return "High Productivity";
        if (avg >= 2.5) return "Moderate Productivity";
        return "Low Productivity";
    }

    public static boolean crashDetected(List<Integer> energyScores) {
        if (energyScores.size() < 4) return false;

        int last = energyScores.get(energyScores.size() - 1);
        double totalAvg = average(energyScores);
        
        return last < (totalAvg * 0.6);  // drop more than 40%
    }
}
