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
        ArrayList<Document> dbStudentsList = connectMongo.getCollection().find().into(new ArrayList<>());

        Set<Document> dbStudents = new HashSet<>(dbStudentsList);

        Set<Document> stackStudents = new HashSet<>(studentStack);
        Set<Document> queueStudents = new HashSet<>(studentQueue);

        Set<Document> localStudents = new HashSet<>(stackStudents);
        localStudents.addAll(queueStudents);

        if (!dbStudents.equals(localStudents)) {
            studentStack.clear();
            studentQueue.clear();

            for (Document student : dbStudentsList) {
                studentStack.push(student);
                studentQueue.offer(student);
            }
        }
    }
}