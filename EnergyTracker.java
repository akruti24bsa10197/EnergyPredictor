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

    // add entry and save
    public void addEntry(EnergyEntry e) {
        entries.add(e);
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
        } catch (IOException e) {
            System.out.println("Could not read data file: " + e.getMessage());
        }
    }

    private void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(dataFile, false))) {
            for (EnergyEntry e : entries) {
                pw.println(e.toCSV());
            }
        } catch (IOException e) {
            System.out.println("Could not save data: " + e.getMessage());
        }
    }

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

    // predict tomorrow using simple average of last 3 days (or fewer)
    public double predictTomorrow() {
        if (entries.isEmpty()) return 0.0;
        List<EnergyEntry> last = entries.stream()
                .sorted(Comparator.comparing(EnergyEntry::getDate).reversed())
                .limit(3)
                .collect(Collectors.toList());
        return last.stream().mapToDouble(EnergyEntry::getEnergyScore).average().orElse(0.0);
    }

    // crash detection: last 2 days average < overall average -> warn
    public boolean isCrashing() {
        if (entries.size() < 2) return false;
        List<EnergyEntry> last2 = entries.stream()
                .sorted(Comparator.comparing(EnergyEntry::getDate).reversed())
                .limit(2)
                .collect(Collectors.toList());
        double avgLast2 = last2.stream().mapToDouble(EnergyEntry::getEnergyScore).average().orElse(0.0);
        double overall = averageEnergy();
        return avgLast2 < overall;
    }

    // recommend deep work time from peakTime frequency among top half energy days
    public String recommendDeepWorkTime() {
        if (entries.isEmpty()) return "No data";
        // consider top 50% energy days
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
        if (count.isEmpty()) return "No peak-time data";
        String best = Collections.max(count.entrySet(), Map.Entry.comparingByValue()).getKey();
        switch (best) {
            case "M": return "Morning";
            case "A": return "Afternoon";
            case "N": return "Night";
            default: return "Unknown";
        }
    }

    // small summary text
    public String summary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Entries stored: ").append(entries.size()).append(System.lineSeparator());
        sb.append(String.format("Average energy: %.2f%n", averageEnergy()));
        highestEnergy().ifPresent(e -> sb.append("Highest: ").append(e.toString()).append(System.lineSeparator()));
        lowestEnergy().ifPresent(e -> sb.append("Lowest: ").append(e.toString()).append(System.lineSeparator()));
        sb.append("Predicted tomorrow energy: ").append(String.format("%.2f", predictTomorrow())).append(System.lineSeparator());
        sb.append("Crash alert: ").append(isCrashing() ? "YES" : "NO").append(System.lineSeparator());
        sb.append("Deep-work recommendation: ").append(recommendDeepWorkTime()).append(System.lineSeparator());
        return sb.toString();
    }
}