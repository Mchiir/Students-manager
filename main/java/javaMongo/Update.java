package javaMongo;

import org.bson.Document;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.HashSet;
import java.util.ArrayList;

public class Update {
    private Stack<Document> studentStack;
    private Queue<Document> studentQueue;
    private ConnectMongo connectMongo;

    public Update(Stack<Document> studentStack, Queue<Document> studentQueue, ConnectMongo connectMongo) {
        this.studentStack = studentStack;
        this.studentQueue = studentQueue;
        this.connectMongo = connectMongo;
    }

    public void syncData() {
        // Retrieve data from the database
        Set<Document> dbStudents = new HashSet<>(connectMongo.getCollection().find().into(new ArrayList<>()));

        // Retrieve data from stack and queue
        Set<Document> stackStudents = new HashSet<>(studentStack);
        Set<Document> queueStudents = new HashSet<>(studentQueue);

        // Combine stack and queue students
        Set<Document> localStudents = new HashSet<>(stackStudents);
        localStudents.addAll(queueStudents);

        // Compare and update
        if (!dbStudents.equals(localStudents)) {
            // Clear the existing stack and queue
            studentStack.clear();
            studentQueue.clear();

            // Add all documents from the database to the stack and queue
            for (Document student : dbStudents) {
                studentStack.push(student);
                studentQueue.offer(student);
            }
        }
    }
}