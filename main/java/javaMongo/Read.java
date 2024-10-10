package javaMongo;

import org.bson.Document;
import java.util.Queue;
import java.util.Stack;
import java.util.Scanner;

public class Read {

    private Stack<Document> studentStack = Insert.getStudentStack();
    private Queue<Document> studentQueue = Insert.getStudentQueue();
    private Scanner scanner;

    public Read(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displayMenu() {
        System.out.println("Choose how to get students:");
        System.out.println("1. Get All.");
        System.out.println("2. Get Last.");
        System.out.println("3. Get First.");
        System.out.println("4. Return to Home.");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                getAll();
                break;
            case 2:
                getLast();
                break;
            case 3:
                getFirst();
                break;
            case 4:
                return;
            default:
                System.out.println("Invalid option.");
        }
    }

    public void getAll() {
        if (studentStack.isEmpty()) {
            System.out.println("No students available in stack.");
            return;
        }
        System.out.println("All students in stack: ");
        for (Document student : studentStack) {
            System.out.println(student.toJson());
        }
    }

    public void getLast() {
        if (!studentStack.isEmpty()) {
            System.out.println("Last student : " + studentStack.peek().toJson());
        } else {
            System.out.println("No students available.");
        }
    }

    public void getFirst() {
        if (!studentQueue.isEmpty()) {
            System.out.println("First student : " + studentQueue.peek().toJson());
        } else {
            System.out.println("No students available.");
        }
    }

}