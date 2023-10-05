import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class Employee {
    private String name;
    private String position;
    private Date date;
    private int hoursWorked;

    public Employee(String name, String position, Date date, int hoursWorked) {
        this.name = name;
        this.position = position;
        this.date = date;
        this.hoursWorked = hoursWorked;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public Date getDate() {
        return date;
    }

    public int getHoursWorked() {
        return hoursWorked;
    }
}

public class EmployeeAnalyzer1 {
    public static void main(String[] args) {
        String filePath = "Assignment_Timecard.xlsx - Sheet1.csv"; // Replace with the actual file path

        try {
            List<Employee> employees = readEmployeeData(filePath);

            // Sort the employees by date
            Collections.sort(employees, Comparator.comparing(Employee::getDate));

            for (int i = 0; i < employees.size(); i++) {
                Employee currentEmployee = employees.get(i);

                // Check for 7 consecutive days of work
                if (hasWorkedForConsecutiveDays(employees, i, 7)) {
                    System.out.println("Employee: " + currentEmployee.getName() +
                            ", Position: " + currentEmployee.getPosition() +
                            " has worked for 7 consecutive days.");
                }

                // Check for less than 10 hours between shifts but greater than 1 hour
                if (i > 0) {
                    Employee previousEmployee = employees.get(i - 1);
                    long timeDifference = currentEmployee.getDate().getTime() - previousEmployee.getDate().getTime();
                    long hoursBetweenShifts = timeDifference / (60 * 60 * 1000);

                    if (hoursBetweenShifts < 10 && hoursBetweenShifts > 1) {
                        System.out.println("Employee: " + currentEmployee.getName() +
                                ", Position: " + currentEmployee.getPosition() +
                                " has less than 10 hours between shifts but greater than 1 hour.");
                    }
                }

                // Check for more than 14 hours worked in a single shift
                if (currentEmployee.getHoursWorked() > 14) {
                    System.out.println("Employee: " + currentEmployee.getName() +
                            ", Position: " + currentEmployee.getPosition() +
                            " has worked for more than 14 hours in a single shift.");
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    private static List<Employee> readEmployeeData(String filePath) throws IOException {
        List<Employee> employees = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String name = parts[0];
                    String position = parts[1];
                    Date date = dateFormat.parse(parts[2]);
                    int hoursWorked = Integer.parseInt(parts[3]);
                    employees.add(new Employee(name, position, date, hoursWorked));
                }
            }
        } catch (ParseException e) {
            System.err.println("Error parsing date: " + e.getMessage());
        }

        return employees;
    }

    private static boolean hasWorkedForConsecutiveDays(List<Employee> employees, int startIndex, int consecutiveDays) {
        for (int i = startIndex; i < startIndex + consecutiveDays; i++) {
            if (i >= employees.size()) {
                return false; // Not enough data for consecutive days
            }

            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(employees.get(i).getDate());
            cal2.setTime(employees.get(startIndex).getDate());
            long diff = cal1.getTimeInMillis() - cal2.getTimeInMillis();
            long days = diff / (24 * 60 * 60 * 1000);

            if (days != i - startIndex) {
                return false;
            }
        }
        return true;
    }
}

