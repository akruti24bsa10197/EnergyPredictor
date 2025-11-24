import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    private static final String DATA_FILE = "energy_data.csv";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        EnergyTracker tracker = new EnergyTracker(DATA_FILE);

        System.out.println("=== Human Energy Pattern Predictor (Upgraded) ===");
        while (true) {
            System.out.println();
            System.out.println("1. Add / Update today's entry");
            System.out.println("2. View dashboard summary (detailed)");
            System.out.println("3. Predict tomorrow energy");
            System.out.println("4. Show ASCII energy graph (last 7 days)");
            System.out.println("5. Show all entries");
            System.out.println("6. Export CSV copy");
            System.out.println("7. Exit");
            System.out.print("Choose: ");
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1":
                    addEntryFlow(sc, tracker);
                    break;
                case "2":
                    System.out.println(tracker.summaryDashboard());
                    break;
                case "3":
                    double p = tracker.predictTomorrow();
                    System.out.printf("Predicted energy for tomorrow: %.2f (%s)%n", p, tracker.classifyEnergy(p));
                    System.out.println("Likely mood tomorrow: " + tracker.predictMood());
                    break;
                case "4":
                    System.out.println(tracker.asciiGraph(7));
                    break;
                case "5":
                    tracker.getAll().forEach(System.out::println);
                    break;
                case "6":
                    exportCopy(tracker);
                    break;
                case "7":
                    System.out.println("Bye.");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void addEntryFlow(Scanner sc, EnergyTracker tracker) {
        try {
            LocalDate date = LocalDate.now();
            System.out.println("Adding entry for date: " + date);
            System.out.print("Sleep hours (0-24): ");
            int sleep = Integer.parseInt(sc.nextLine().trim());

            System.out.print("Mood (1-5): ");
            int mood = Integer.parseInt(sc.nextLine().trim());

            System.out.print("Productivity (1-5): ");
            int prod = Integer.parseInt(sc.nextLine().trim());

            System.out.print("Caffeine cups (0-10): ");
            int caf = Integer.parseInt(sc.nextLine().trim());

            System.out.print("Work type (Study/Creative/Physical/Mixed): ");
            String wt = sc.nextLine().trim();
            if (wt.isEmpty()) wt = "Mixed";

            System.out.print("Peak time today? (M=Morning, A=Afternoon, N=Night) (optional): ");
            String peak = sc.nextLine().trim().toUpperCase();

            EnergyEntry e = new EnergyEntry(date, sleep, mood, prod, caf, wt, peak);
            tracker.addEntry(e);
            System.out.println("Saved entry: " + e.toString());
        } catch (Exception ex) {
            System.out.println("Error in input. Please try again. (" + ex.getMessage() + ")");
        }
    }

    private static void exportCopy(EnergyTracker tracker) {
        // export the CSV to a timestamped copy
        try {
            java.nio.file.Path src = java.nio.file.Paths.get("energy_data.csv");
            if (!java.nio.file.Files.exists(src)) {
                System.out.println("No data file to export.");
                return;
            }
            String destName = "energy_data_copy_" + java.time.LocalDate.now() + ".csv";
            java.nio.file.Path dest = java.nio.file.Paths.get(destName);
            java.nio.file.Files.copy(src, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Exported copy to: " + destName);
        } catch (Exception e) {
            System.out.println("Export failed: " + e.getMessage());
        }
    }
}
