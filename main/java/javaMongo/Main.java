package javaMongo;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ConnectMongo connectMongo = new ConnectMongo();
        Insert insert = new Insert(scanner);
        
        Read read = new Read(scanner);
        Remove remove = new Remove(scanner, connectMongo);
        Edit edit = new Edit(scanner, connectMongo);
        
        Update update = new Update(javaMongo.Insert.getStudentStack(), javaMongo.Insert.getStudentQueue(), connectMongo);

        update.syncData();

        while (true) {
            System.out.println("\nWelcome to Student Management System");
            System.out.println("1. Get students.");
            System.out.println("2. Insert student.");
            System.out.println("3. Remove students.");
            System.out.println("4. Edit a student.");
            System.out.println("5. Update the system's data.");
            System.out.println("9. Exit the program.");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    read.displayMenu();
                    update.syncData();
                    break;
                case 2:
                	insert.menu();
                    update.syncData();
                    break;
                case 3:
                   remove.displayMenu();
                    update.syncData();
                    break;
                case 4:
                   edit.displayMenu();
                    update.syncData();
                    break;
                case 5:
                     update.syncData();
                     System.out.println("System'data successfully updated.");
                     break;
                case 9:
                    System.out.println("Goodbye!");
                    connectMongo.getMongoClient().close();
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}