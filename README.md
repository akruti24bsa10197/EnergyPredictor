# Human Energy Pattern Predictor

Simple Java program to record daily self-reported measures and produce basic energy analytics.

## What it does
- User enters one daily record: sleep hours, mood (1-5), productivity (1-5), caffeine cups, work type, optional peak time.
- Program computes a simple "energy score" per day.
- Data is saved to `energy_data.csv`.
- Shows summary: average, highest, lowest, simple crash detection.
- Predicts tomorrow's energy using the average of last 3 days.
- Recommends a deep-work time based on stored peak-time data.

## Files
- `Main.java` : program entry / menu
- `EnergyTracker.java` : manages entries, file I/O, and analytics
- `EnergyEntry.java` : data model for a day's entry
- `energy_data.csv` : data file (auto created)

## How to run (basic)
1. Put the three `.java` files in one folder.
2. Open terminal in that folder.
3. Compile: