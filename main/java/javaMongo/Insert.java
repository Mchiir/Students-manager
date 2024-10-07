package javaMongo;

import com.mongodb.client.MongoCollection;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

public class Insert {
    private static Stack<Document> studentStack = new Stack<>();
    private static Queue<Document> studentQueue = new LinkedList<>();
    private ConnectMongo conn = new ConnectMongo();
    private MongoCollection<Document> collection = conn.getCollection();
    private Scanner scanner;

    public Insert(Scanner scanner) {
        this.scanner = scanner;
    }

    public void menu() {
        System.out.println("\n--- Student Menu ---");
        System.out.println("1. Insert manually");
        System.out.println("2. Insert by file (path)");
        System.out.println("3. Home.");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                addStudent();
                break;
            case "2":
                System.out.println("Enter the file path:");
                String filePath = scanner.nextLine();
                if (isValidFilePath(filePath)) {
                    addStudent(filePath);
                } else {
                    System.out.println("Invalid file path. Please try again.");
                }
                break;
            case "3":
                return;
            default:
                System.out.println("Invalid choice. Please select a valid option.");
        }
    }

    // Manual student entry
    public void addStudent() {
        System.out.println("Firstname: ");
        String firstname = scanner.nextLine();
        System.out.println("Lastname: ");
        String lastname = scanner.nextLine();
        System.out.println("School: ");
        String school = scanner.nextLine();
        System.out.println("Combination: ");
        String combination = scanner.nextLine();
        System.out.println("Level (0, 1, ...): ");
        int level = Integer.parseInt(scanner.nextLine()); // Convert to int
        System.out.println("Date of birth (YYYY-MM-DD): ");
        String dobString = scanner.nextLine();

        // Convert String to Date
        Date dob = Date.valueOf(dobString);

        // Calculate age
        int age = calculateAge(dob);

        Document student = new Document("Firstname", firstname)
                              .append("Lastname", lastname)
                              .append("School", school)
                              .append("Combination", combination)
                              .append("Level", level) // Store level as a number
                              .append("Age", age); // Store age instead of DOB

        collection.insertOne(student);
        studentStack.push(student);
        studentQueue.offer(student);

        System.out.println("Student added successfully!");
    }

    // File entry for students
    public void addStudent(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            int firstnameIndex = -1, lastnameIndex = -1, schoolIndex = -1;
            int combinationIndex = -1, levelIndex = -1, dobIndex = -1;

            // Identify column indexes for each header
            for (Cell cell : headerRow) {
                String header = cell.getStringCellValue();
                switch (header.toLowerCase()) {
                    case "firstname":
                        firstnameIndex = cell.getColumnIndex();
                        break;
                    case "lastname":
                        lastnameIndex = cell.getColumnIndex();
                        break;
                    case "school":
                        schoolIndex = cell.getColumnIndex();
                        break;
                    case "combination":
                        combinationIndex = cell.getColumnIndex();
                        break;
                    case "level":
                        levelIndex = cell.getColumnIndex();
                        break;
                    case "date_of_birth":
                        dobIndex = cell.getColumnIndex();
                        break;
                }
            }

            if (firstnameIndex == -1 || lastnameIndex == -1 || schoolIndex == -1 ||
                combinationIndex == -1 || levelIndex == -1 || dobIndex == -1) {
                System.out.println("Invalid headers. File must contain 'Firstname', 'Lastname', 'School', 'Combination', 'Level', and 'Date_of_Birth' headers.");
                workbook.close();
                return;
            }

            // Read rows and insert each student
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    String firstname = row.getCell(firstnameIndex).getStringCellValue();
                    String lastname = row.getCell(lastnameIndex).getStringCellValue();
                    String school = row.getCell(schoolIndex).getStringCellValue();
                    String combination = row.getCell(combinationIndex).getStringCellValue();
                    int level = (int) row.getCell(levelIndex).getNumericCellValue(); // Get as numeric
                    String dobString = row.getCell(dobIndex).getStringCellValue();

                    // Convert String to Date
                    Date dob = Date.valueOf(dobString);

                    // Calculate age
                    int age = calculateAge(dob);

                    Document student = new Document("Firstname", firstname)
                                          .append("Lastname", lastname)
                                          .append("School", school)
                                          .append("Combination", combination)
                                          .append("Level", level) // Store level as a number
                                          .append("Age", age); // Store age instead of DOB

                    collection.insertOne(student);
                }
            }

            workbook.close();
            System.out.println("Students from file added successfully!");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error processing date: " + e.getMessage());
        }
    }

    private boolean isValidFilePath(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    public static Stack<Document> getStudentStack() {
        return studentStack;
    }

    public static Queue<Document> getStudentQueue() {
        return studentQueue;
    }

    // Method to calculate age from Date of Birth
    public int calculateAge(Date dob) {
        Calendar dobCal = Calendar.getInstance();
        dobCal.setTime(dob);
        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - dobCal.get(Calendar.YEAR);
        
        // Adjust age if the current date is before the birthday in the current year
        if (today.get(Calendar.MONTH) < dobCal.get(Calendar.MONTH) || 
            (today.get(Calendar.MONTH) == dobCal.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) < dobCal.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }
        
        return age;
    }
}