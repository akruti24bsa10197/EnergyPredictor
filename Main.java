import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    private static final String DATA_FILE = "energy_data.csv";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        EnergyTracker tracker = new EnergyTracker(DATA_FILE);

        System.out.println("=== Human Energy Pattern Predictor ===");
        while (true) {
            System.out.println();
            System.out.println("1. Add today's entry");
            System.out.println("2. View summary");
            System.out.println("3. Predict tomorrow energy");
            System.out.println("4. Show all entries");
            System.out.println("5. Exit");
            System.out.print("Choose: ");
            String ch = sc.nextLine().trim();
            if (ch.equals("1")) {
                addEntryFlow(sc, tracker);
            } else if (ch.equals("2")) {
                System.out.println(tracker.summary());
            } else if (ch.equals("3")) {
                double p = tracker.predictTomorrow();
                System.out.printf("Predicted energy for tomorrow: %.2f%n", p);
            } else if (ch.equals("4")) {
                tracker.getAll().forEach(e -> System.out.println(e.toString()));
            } else if (ch.equals("5")) {
                System.out.println("Bye.");
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }
        sc.close();
    }

    private static void addEntryFlow(Scanner sc, EnergyTracker tracker) {
        try {
            LocalDate date = LocalDate.now();

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

            System.out.print("Peak time today? (M=Morning, A=Afternoon, N=Night) (optional, press enter to skip): ");
            String peak = sc.nextLine().trim().toUpperCase();

            EnergyEntry e = new EnergyEntry(date, sleep, mood, prod, caf, wt, peak);
            tracker.addEntry(e);
            System.out.println("Saved entry: " + e.toString());
        } catch (Exception ex) {
            System.out.println("Error in input. Please try again. (" + ex.getMessage() + ")");
        }
    }
}