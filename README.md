## Introduction
This project is a simple Java-based system that helps users track their daily lifestyle inputs (sleep, mood, productivity, caffeine, etc.) and understand their energy patterns. The program stores entries in a CSV file, analyses weekly trends, shows ASCII graphs, predicts next-day energy and mood, and gives useful insights like crash warnings and recommended deep-work time.


# 🌟 Human Energy Pattern Predictor (Java)

A Java console app that tracks daily lifestyle inputs and generates meaningful insights about your energy levels.  
It stores data in CSV, predicts tomorrow’s energy, detects crashes, and visualizes trends using ASCII graphs.

---

## 🚀 Features

- Daily tracking: sleep, mood, productivity, caffeine, work type, peak time  
- Energy score calculation  
- Dashboard summary with insights  
- ASCII energy graph (last 7 days)  
- Weekly stats (best/worst day, avg sleep, productivity tier)  
- Sleep–energy correlation  
- Energy + mood prediction  
- Crash warning system  
- CSV-based data storage  

---

## 📂 Project Structure

Main.java EnergyEntry.java EnergyTracker.java energy_data.csv

---

## ▶️ Run the Project

*Compile*

javac Main.java EnergyEntry.java EnergyTracker.java

*Run*

java Main

---

## 🧠 Concepts Used

- Java OOP  
- File I/O (CSV)  
- ArrayList  
- Simple statistics + correlation  
- Console-based UI  
- Encapsulation & exception handling  

---
## Technologies / Tools Used

-This project is built using core Java and basic file handling concepts. The main tools used include:
-Java JDK (any version 8 or above works)
-A code editor or IDE like VS Code, IntelliJ IDEA or Eclipse
-CSV file storage for saving daily entries
-Java Collections (ArrayList) for handling multiple records
-Object-Oriented Programming principles (classes, objects, modular structure)
-These tools together make the project simple, lightweight and easy to run on any system without extra dependencies.

---

## Steps to Install & Run the Project

1.⁠ ⁠Install Java on your system and verify it using java -version.
2.⁠ ⁠Download or copy the project folder into your computer.
3.⁠ ⁠Open the folder in your IDE (VS Code / IntelliJ / Eclipse) or use terminal.
4.⁠ ⁠Compile all .java files using:
javac Main.java EnergyEntry.java EnergyTracker.java FileManager.java StatisticsUtil.java InsightGenerator.java
5.⁠ ⁠Run the program using:
java Main
6.⁠ ⁠The program will automatically create or read the energy_data.csv file.
7.⁠ ⁠Use the menu options to enter data, view insights or generate predictions.

---

## Instructions for Testing

-To properly test the project, follow these steps:
-Add several sample entries using the “Add New Entry” option.
-Open the CSV file (energy_data.csv) to confirm the data is being saved.
-Restart the program and check if the entries load correctly.
-Open the dashboard and verify energy score, predictions and weekly insights.
-Check that invalid inputs are handled (e.g., typing letters instead of numbers).-=Use the prediction feature and verify that results change based on recent data.
-View the ASCII graph and confirm it matches the values in the CSV file.
-These tests ensure that storage, calculations and insights all work correctly.

---
## 🔮 Future Improvements

- GUI (JavaFX/Swing)  
- Visual charts  
- ML-based prediction  
- Mobile app sync  

---

✨ A compact, practical Java project demonstrating file handling, analytics, and clean program design.

---



