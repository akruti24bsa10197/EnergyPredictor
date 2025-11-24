import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EnergyEntry {
    private LocalDate date;
    private int sleepHours;
    private int mood; // 1-5
    private int productivity; // 1-5
    private int caffeine; // cups
    private String workType; // Study/Creative/Physical/Mixed
    private String peakTime; // M / A / N / blank
    private double energyScore;

    private static DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;

    public EnergyEntry(LocalDate date, int sleepHours, int mood, int productivity,
                       int caffeine, String workType, String peakTime) {
        this.date = date;
        this.sleepHours = sleepHours;
        this.mood = mood;
        this.productivity = productivity;
        this.caffeine = caffeine;
        this.workType = sanitize(workType);
        this.peakTime = (peakTime == null) ? "" : peakTime.toUpperCase();
        calculateScore();
    }

    // parse from CSV line
    public static EnergyEntry fromCSV(String csvLine) {
        // date,sleep,mood,productivity,caffeine,workType,peakTime,score
        String[] parts = csvLine.split(",", -1);
        LocalDate d = LocalDate.parse(parts[0], fmt);
        int s = Integer.parseInt(parts[1]);
        int m = Integer.parseInt(parts[2]);
        int p = Integer.parseInt(parts[3]);
        int c = Integer.parseInt(parts[4]);
        String wt = parts[5];
        String pt = parts[6];
        EnergyEntry e = new EnergyEntry(d, s, m, p, c, wt, pt);
        try {
            e.energyScore = Double.parseDouble(parts[7]);
        } catch (Exception ex) { /* ignore and keep computed */ }
        return e;
    }

    // CSV line
    public String toCSV() {
        return String.join(",",
                date.format(fmt),
                String.valueOf(sleepHours),
                String.valueOf(mood),
                String.valueOf(productivity),
                String.valueOf(caffeine),
                workType.replace(",", " "),
                peakTime,
                String.format("%.2f", energyScore)
        );
    }

    private void calculateScore() {
        // Intentionally simple and explainable:
        // Energy Score = (Sleep × 2) + (Mood × 3) + (Productivity × 3) − (Caffeine × 1)
        this.energyScore = (sleepHours * 2.0) + (mood * 3.0) + (productivity * 3.0) - (caffeine * 1.0);
        if (this.energyScore < 0) this.energyScore = 0;
    }

    private String sanitize(String s) {
        if (s == null) return "Mixed";
        s = s.trim();
        if (s.isEmpty()) return "Mixed";
        return s;
    }

    // getters
    public java.time.LocalDate getDate() { return date; }
    public double getEnergyScore() { return energyScore; }
    public String getPeakTime() { return peakTime; }
    public int getSleepHours() { return sleepHours; }
    public int getMood() { return mood; }
    public int getProductivity() { return productivity; }
    public int getCaffeine() { return caffeine; }
    public String getWorkType() { return workType; }

    @Override
    public String toString() {
        return String.format("%s | Sleep:%d Mood:%d Prod:%d Caf:%d Type:%s Peak:%s Score:%.2f",
                date.format(fmt), sleepHours, mood, productivity, caffeine, workType,
                (peakTime == null || peakTime.isEmpty() ? "-" : peakTime), energyScore);
    }
}
