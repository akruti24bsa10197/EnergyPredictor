import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class EnergyTracker {
    private List<EnergyEntry> entries;
    private File dataFile;

    public EnergyTracker(String filename) {
        entries = new ArrayList<>();
        dataFile = new File(filename);
        loadFromFile();
    }

    // Add and persist
    public void addEntry(EnergyEntry e) {
        // If an entry for same date exists, replace it (makes sense for daily edits)
        entries.removeIf(x -> x.getDate().equals(e.getDate()));
        entries.add(e);
        // keep sorted by date ascending
        entries.sort(Comparator.comparing(EnergyEntry::getDate));
        saveToFile();
    }

    public List<EnergyEntry> getAll() {
        return Collections.unmodifiableList(entries);
    }

    private void loadFromFile() {
        if (!dataFile.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                try {
                    EnergyEntry e = EnergyEntry.fromCSV(line);
                    entries.add(e);
                } catch (Exception ex) {
                    // skip malformed
                }
            }
            entries.sort(Comparator.comparing(EnergyEntry::getDate));
        } catch (IOException e) {
            System.out.println("Could not read data file: " + e.getMessage());
        }
    }

    private void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(dataFile, false))) {
            for (EnergyEntry e : entries) pw.println(e.toCSV());
        } catch (IOException e) {
            System.out.println("Could not save data: " + e.getMessage());
        }
    }

    // basic stats
    public double averageEnergy() {
        if (entries.isEmpty()) return 0.0;
        return entries.stream().mapToDouble(EnergyEntry::getEnergyScore).average().orElse(0.0);
    }

    public Optional<EnergyEntry> highestEnergy() {
        return entries.stream().max(Comparator.comparingDouble(EnergyEntry::getEnergyScore));
    }

    public Optional<EnergyEntry> lowestEnergy() {
        return entries.stream().min(Comparator.comparingDouble(EnergyEntry::getEnergyScore));
    }

    // Prediction: average of last up to 3 entries
    public double predictTomorrow() {
        if (entries.isEmpty()) return 0.0;
        List<EnergyEntry> last = entries.stream()
                .sorted(Comparator.comparing(EnergyEntry::getDate).reversed())
                .limit(3)
                .collect(Collectors.toList());
        return last.stream().mapToDouble(EnergyEntry::getEnergyScore).average().orElse(0.0);
    }

    // Crash detection: last 2 days average < overall average * 0.9
    public boolean isCrashing() {
        if (entries.size() < 2) return false;
        List<EnergyEntry> last2 = entries.stream()
                .sorted(Comparator.comparing(EnergyEntry::getDate).reversed())
                .limit(2)
                .collect(Collectors.toList());
        double avgLast2 = last2.stream().mapToDouble(EnergyEntry::getEnergyScore).average().orElse(0.0);
        double overall = averageEnergy();
        return avgLast2 < overall * 0.9; // slight threshold to avoid false positives
    }

    // energy classification
    public String classifyEnergy(double score) {
        if (score >= 80) return "High";
        if (score >= 50) return "Medium";
        if (score >= 20) return "Low";
        return "Critical";
    }

    // recommend deep work time based on peakTime frequency among top half energy days
    public String recommendDeepWorkTime() {
        if (entries.isEmpty()) return "No data";
        List<EnergyEntry> sorted = entries.stream()
                .sorted(Comparator.comparingDouble(EnergyEntry::getEnergyScore).reversed())
                .collect(Collectors.toList());
        int take = Math.max(1, sorted.size() / 2);
        Map<String, Long> count = new HashMap<>();
        for (int i = 0; i < take; i++) {
            String t = sorted.get(i).getPeakTime();
            if (t == null || t.isEmpty()) continue;
            count.put(t, count.getOrDefault(t, 0L) + 1);
        }
        if (count.isEmpty()) {
            // fallback: use average sleep hour approximate -> suggest morning if average sleep >=7 else afternoon
            double avgSleep = entries.stream().mapToDouble(EnergyEntry::getSleepHours).average().orElse(7.0);
            return (avgSleep >= 7.0) ? "Morning" : "Afternoon";
        }
        String best = Collections.max(count.entrySet(), Map.Entry.comparingByValue()).getKey();
        switch (best) {
            case "M": return "Morning";
            case "A": return "Afternoon";
            case "N": return "Night";
            default: return "Unknown";
        }
    }

    // ASCII graph last N days
    public String asciiGraph(int days) {
        if (entries.isEmpty()) return "No data to graph.";
        List<EnergyEntry> last = entries.stream()
                .sorted(Comparator.comparing(EnergyEntry::getDate).reversed())
                .limit(days)
                .collect(Collectors.toList());
        Collections.reverse(last); // oldest first
        double max = last.stream().mapToDouble(EnergyEntry::getEnergyScore).max().orElse(1.0);
        if (max < 1) max = 1;
        StringBuilder sb = new StringBuilder();
        sb.append("Energy Graph (last ").append(last.size()).append(" days)\n");
        for (EnergyEntry e : last) {
            int barLen = (int) Math.round((e.getEnergyScore() / max) * 30); // scale to 30 chars
            sb.append(e.getDate()).append(" | ");
            for (int i = 0; i < barLen; i++) sb.append("|");
            sb.append(String.format(" (%.0f)%n", e.getEnergyScore()));
        }
        return sb.toString();
    }

    // Sleep vs Energy Pearson correlation (last up to 30 days)
    public double sleepEnergyCorrelation() {
        if (entries.size() < 2) return Double.NaN;
        List<EnergyEntry> last = entries.stream()
                .sorted(Comparator.comparing(EnergyEntry::getDate).reversed())
                .limit(30)
                .collect(Collectors.toList());
        // use oldest->newest order
        Collections.reverse(last);
        int n = last.size();
        double[] x = new double[n]; // sleep
        double[] y = new double[n]; // energy
        for (int i = 0; i < n; i++) {
            x[i] = last.get(i).getSleepHours();
            y[i] = last.get(i).getEnergyScore();
        }
        double meanX = Arrays.stream(x).average().orElse(0);
        double meanY = Arrays.stream(y).average().orElse(0);
        double num = 0, denX = 0, denY = 0;
        for (int i = 0; i < n; i++) {
            double dx = x[i] - meanX;
            double dy = y[i] - meanY;
            num += dx * dy;
            denX += dx * dx;
            denY += dy * dy;
        }
        double denom = Math.sqrt(denX * denY);
        if (denom == 0) return Double.NaN;
        return num / denom;
    }

    // mood prediction (simple rounded average of last 3 days)
    public int predictMood() {
        if (entries.isEmpty()) return 3; // neutral default
        List<EnergyEntry> last = entries.stream()
                .sorted(Comparator.comparing(EnergyEntry::getDate).reversed())
                .limit(3)
                .collect(Collectors.toList());
        double avg = last.stream().mapToInt(EnergyEntry::getMood).average().orElse(3.0);
        return (int)Math.round(avg);
    }

    // productivity tier based on last 7 days average productivity
    public String productivityTier() {
        if (entries.isEmpty()) return "No data";
        List<EnergyEntry> last = entries.stream()
                .sorted(Comparator.comparing(EnergyEntry::getDate).reversed())
                .limit(7)
                .collect(Collectors.toList());
        double avgProd = last.stream().mapToInt(EnergyEntry::getProductivity).average().orElse(0.0);
        if (avgProd >= 4.0) return "High Performer";
        if (avgProd >= 2.5) return "Moderate Performer";
        return "Low Performer";
    }

    // weekly insights: analyze last 7 entries
    public String weeklyInsights() {
        if (entries.isEmpty()) return "No data";
        List<EnergyEntry> last = entries.stream()
                .sorted(Comparator.comparing(EnergyEntry::getDate).reversed())
                .limit(7)
                .collect(Collectors.toList());
        Collections.reverse(last);
        StringBuilder sb = new StringBuilder();
        sb.append("----- Weekly Insights (last ").append(last.size()).append(" days) -----\n");
        double avgEnergy = last.stream().mapToDouble(EnergyEntry::getEnergyScore).average().orElse(0.0);
        sb.append(String.format("Avg Energy (week): %.2f (%s)%n", avgEnergy, classifyEnergy(avgEnergy)));
        Optional<EnergyEntry> best = last.stream().max(Comparator.comparingDouble(EnergyEntry::getEnergyScore));
        Optional<EnergyEntry> worst = last.stream().min(Comparator.comparingDouble(EnergyEntry::getEnergyScore));
        best.ifPresent(e -> sb.append("Best day: ").append(e.toString()).append("\n"));
        worst.ifPresent(e -> sb.append("Worst day: ").append(e.toString()).append("\n"));
        double avgSleep = last.stream().mapToInt(EnergyEntry::getSleepHours).average().orElse(0.0);
        sb.append(String.format("Avg Sleep (week): %.2f hrs%n", avgSleep));
        long caffeineSpikeDays = last.stream().filter(e -> e.getCaffeine() >= 3).count();
        sb.append("Days with 3+ cups caffeine: ").append(caffeineSpikeDays).append("\n");
        sb.append("Productivity tier (week): ").append(productivityTier()).append("\n");
        return sb.toString();
    }

    // full summary (dashboard)
    public String summaryDashboard() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== DAILY DASHBOARD ==========\n");
        if (entries.isEmpty()) {
            sb.append("No data yet. Add today's entry first.\n");
            return sb.toString();
        }
        EnergyEntry latest = entries.get(entries.size() - 1);
        sb.append(String.format("Date: %s%n", latest.getDate()));
        sb.append(String.format("Energy Score: %.2f (%s)%n", latest.getEnergyScore(), classifyEnergy(latest.getEnergyScore())));
        sb.append(String.format("Sleep: %d hrs | Mood: %d | Productivity: %d | Caffeine: %d cups%n",
                latest.getSleepHours(), latest.getMood(), latest.getProductivity(), latest.getCaffeine()));
        sb.append("Work Type: ").append(latest.getWorkType()).append("\n");
        sb.append("Recommended deep work: ").append(recommendDeepWorkTime()).append("\n");
        sb.append("Predicted energy tomorrow: ").append(String.format("%.2f", predictTomorrow())).append(" (").append(classifyEnergy(predictTomorrow())).append(")\n");
        sb.append("Mood likely tomorrow: ").append(predictMood()).append("\n");
        sb.append("Crash alert: ").append(isCrashing() ? "YES - consider rest" : "NO").append("\n");
        double corr = sleepEnergyCorrelation();
        sb.append("Sleep-Energy correlation (pearson): ");
        if (Double.isNaN(corr)) sb.append("Insufficient data\n");
        else sb.append(String.format("%.2f (%s)%n", corr, interpretCorrelation(corr)));
        sb.append(asciiGraph(7));
        sb.append(weeklyInsights());
        sb.append("=====================================\n");
        return sb.toString();
    }

    private String interpretCorrelation(double r) {
        double ar = Math.abs(r);
        if (ar >= 0.8) return "Strong";
        if (ar >= 0.5) return "Moderate";
        if (ar >= 0.3) return "Weak";
        return "Very weak or none";
    }
}
