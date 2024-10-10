package javaMongo;

import java.sql.Date;
import java.util.Calendar;
import java.util.Queue;
import java.util.Stack;
import org.bson.Document;
import java.util.Scanner;

public class Edit {

    private Stack<Document> studentStack;
    private Queue<Document> studentQueue;
    private ConnectMongo conn;
    private Scanner scanner;

    public Edit(Scanner scanner, ConnectMongo conn) {
        this.scanner = scanner;
        this.conn = conn;
        this.studentStack = Insert.getStudentStack();
        this.studentQueue = Insert.getStudentQueue();
    }

    public void displayMenu() {
        System.out.println("\n--- Edit Student Menu ---");
        System.out.println("1. Edit Last.");
        System.out.println("2. Edit First.");
        System.out.println("3. Edit by Stack Index.");
        System.out.println("4. Edit by Queue Index.");
        System.out.println("5. Exit.");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                editLast();
                break;
            case 2:
                editFirst();
                break;
            case 3:
                editByStackIndex();
                break;
            case 4:
                editByQueueIndex();
                break;
            case 5:
                return;
            default:
                System.out.println("Invalid option. Try again.");
                displayMenu();
        }
    }

    public void editLast() {
        if (!studentStack.isEmpty()) {
            Document student = studentStack.pop();
            editStudent(student);
            studentStack.push(student);
        } else {
            System.out.println("No students available.");
        }
    }

    public void editFirst() {
        if (!studentQueue.isEmpty()) {
            Document student = studentQueue.poll();
            editStudent(student);
            studentQueue.offer(student);
        } else {
            System.out.println("No students available.");
        }
    }

    public void editByStackIndex() {
        System.out.println("Enter the index of the student to edit (0 to " + (studentStack.size() - 1) + "):");
        int index = scanner.nextInt();
        scanner.nextLine();

        if (index >= 0 && index < studentStack.size()) {
            Document student = (Document) studentStack.toArray()[index];
            editStudent(student);
        } else {
            System.out.println("Invalid index.");
        }
    }

    public void editByQueueIndex() {
        System.out.println("Enter the index of the student to edit (0 to " + (studentQueue.size() - 1) + "):");
        int index = scanner.nextInt();
        scanner.nextLine();

        if (index >= 0 && index < studentQueue.size()) {
            Document student = (Document) studentQueue.toArray()[index];
            editStudent(student);
        } else {
            System.out.println("Invalid index.");
        }
    }

    private void editStudent(Document student) {
        System.out.println("Editing student: " + student.toJson());

        String firstname = promptForInput("Enter new Firstname:", student.getString("Firstname"));
        String lastname = promptForInput("Enter new Lastname:", student.getString("Lastname"));
        String school = promptForInput("Enter new School:", student.getString("School"));
        String combination = promptForInput("Enter new Combination:", student.getString("Combination"));
        String levelInput = promptForInput("Enter new Level (0, 1, ...):", String.valueOf(student.getInteger("Level")));
        
        int currentAge = student.getInteger("Age");
        String dobInput = promptForInput("Enter new Date of Birth (yyyy-MM-dd):", currentAge > 0 ? calculateDobFromAge(currentAge).toString() : "");

        try {
            Date dob;
            if (!dobInput.isEmpty()) {
                dob = Date.valueOf(dobInput);
            } else {
                dob = calculateDobFromAge(currentAge);
            }

            int age = calculateAge(dob);
            int level = Integer.parseInt(levelInput);

            student.put("Firstname", firstname);
            student.put("Lastname", lastname);
            student.put("School", school);
            student.put("Combination", combination);
            student.put("Level", level);
            student.put("Age", age);

            conn.getCollection().updateOne(
                new Document("_id", student.getObjectId("_id")),
                new Document("$set", student)
            );

            System.out.println("Student edited successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid Date of Birth format. Please enter a date in the format yyyy-MM-dd.");
        }
    }

    private String promptForInput(String prompt, String defaultValue) {
        System.out.println(prompt + " (Press Enter to keep '" + defaultValue + "'):");
        String input = scanner.nextLine();
        return input.isEmpty() ? defaultValue : input;
    }

    private Date calculateDobFromAge(int age) {
        Calendar today = Calendar.getInstance();
        today.add(Calendar.YEAR, -age);
        return new Date(today.getTimeInMillis());
    }

    public int calculateAge(Date dob) {
        Calendar dobCal = Calendar.getInstance();
        dobCal.setTime(dob);
        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - dobCal.get(Calendar.YEAR);
        
        if (today.get(Calendar.MONTH) < dobCal.get(Calendar.MONTH) || 
            (today.get(Calendar.MONTH) == dobCal.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) < dobCal.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }
        
        return age;
    }
}