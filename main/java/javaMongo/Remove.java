package javaMongo;

import java.util.Queue;
import java.util.Stack;
import org.bson.Document;
import java.util.Scanner;

public class Remove {

    private Stack<Document> studentStack = Insert.getStudentStack();
    private Queue<Document> studentQueue = Insert.getStudentQueue();
    private ConnectMongo connectMongo;
    private Scanner scanner;

    public Remove(Scanner scanner, ConnectMongo connectMongo) {
        this.scanner = scanner;
        this.connectMongo = connectMongo;
    }

    public void displayMenu() {
        System.out.println("Choose how to remove students:");
        System.out.println("1. Remove All.");
        System.out.println("2. Remove Last.");
        System.out.println("3. Remove First.");
        System.out.println("8. Exit.");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                confirmRemoveAll();
                break;
            case 2:
                confirmRemoveLast();
                break;
            case 3:
                confirmRemoveFirst();
                break;
            case 8:
                return;
            default:
                System.out.println("Invalid option.");
        }
    }

    private boolean confirmRemoval() {
        System.out.println("Confirm removal [y/N]: ");
        String confirmation = scanner.nextLine().trim();

        return confirmation.isEmpty() || confirmation.equalsIgnoreCase("Y");
    }

    public void confirmRemoveAll() {
        if (confirmRemoval()) {
            removeAll();
        } else {
            System.out.println("Remove all cancelled.");
        }
    }

    public void removeAll() {
        connectMongo.getCollection().deleteMany(new Document());
        studentStack.clear(); 
        studentQueue.clear(); 
        System.out.println("All students removed from the database and local storage.");
    }

    public void confirmRemoveLast() {
        if (confirmRemoval()) {
            removeLast();
        } else {
            System.out.println("Remove last student cancelled.");
        }
    }

    public void removeLast() {
        if (!studentStack.isEmpty()) {
            Document removedStudent = studentStack.pop();
            System.out.println("Removed student (LIFO): " + removedStudent.toJson());
            connectMongo.getCollection().deleteOne(new Document("_id", removedStudent.getObjectId("_id")));
        } else {
            System.out.println("No students to remove.");
        }
    }

    public void confirmRemoveFirst() {
        if (confirmRemoval()) {
            removeFirst();
        } else {
            System.out.println("Remove first student cancelled.");
        }
    }

    public void removeFirst() {
        if (!studentQueue.isEmpty()) {
            Document removedStudent = studentQueue.poll();
            System.out.println("Removed student (FIFO): " + removedStudent.toJson());
            connectMongo.getCollection().deleteOne(new Document("_id", removedStudent.getObjectId("_id")));
        } else {
            System.out.println("No students to remove.");
        }
    }
}