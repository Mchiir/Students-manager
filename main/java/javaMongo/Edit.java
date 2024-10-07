package javaMongo;

import java.sql.Date;
import java.util.Queue;
import java.util.Stack;
import org.bson.Document;
import java.util.Scanner;

public class Edit {

    private Stack<Document> studentStack = Insert.getStudentStack();
    private Queue<Document> studentQueue = Insert.getStudentQueue();
    private ConnectMongo connectMongo;
    private Scanner scanner;

    public Edit(Scanner scanner, ConnectMongo connectMongo) {
        this.scanner = scanner;
        this.connectMongo = connectMongo;
    }

    public void displayMenu() {
        System.out.println("Choose how to edit a student:");
        System.out.println("1. Edit Last (Stack LIFO)");
        System.out.println("2. Edit First (Queue FIFO)");
        System.out.println("3. Edit by Index (Stack)");
        System.out.println("4. Edit by Index (Queue)");
        System.out.println("5. Exit the program.");

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
                System.out.println("Invalid option.");
        }
    }

    public void editLast() {
        if (!studentStack.isEmpty()) {
            Document student = studentStack.pop();
            editStudent(student);
            studentStack.push(student); // Restore the original student after editing
        } else {
            System.out.println("No students available.");
        }
    }

    public void editFirst() {
        if (!studentQueue.isEmpty()) {
            Document student = studentQueue.poll();
            editStudent(student);
            studentQueue.offer(student); // Restore the original student after editing
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
            // Update the stack after editing
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
            // Update the queue after editing
        } else {
            System.out.println("Invalid index.");
        }
    }

    private void editStudent(Document student) {
        System.out.println("Editing student: " + student.toJson());

        String name = promptForInput("Enter new name:", student.getString("Firstname"));
        String dobInput = promptForInput("Enter new Date of Birth (yyyy-MM-dd): ", student.getDate("DateOfBirth").toString());

        try {
            Date dob = Date.valueOf(dobInput);
            int age = new Insert(scanner).calculateAge(dob);

            student.put("Firstname", name);
            student.put("Age", age);

            connectMongo.getCollection().updateOne(
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
}