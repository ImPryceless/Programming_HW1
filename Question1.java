/*
Group HW Team members:
J.P. O'Malley
Ikuseghan Pryce
Ismael Garrido Rodriguez
Shenna Marie-P Wyco
Geidy Dorviny Molina
*/

import java.util.*;
import java.util.stream.*;

public class Question1 {

    static int LOW = 0,
               HIGH = 4999,
               REQUESTS = 1000,
               INF = Integer.MAX_VALUE;

    public static void main(String arg[]) {

        // Get initial position
        int initPos = new Integer(arg[0]);

        // Generate random requests
        Random rand = new Random();
        IntStream randInts = rand.ints(LOW, HIGH + 1).distinct().limit(REQUESTS);
        ArrayList<Integer> queue =
            new ArrayList<Integer>(randInts.boxed().collect(Collectors.toList()));

        fcfs(queue, initPos);
        sstf(queue, initPos);
        scan(queue, initPos);
        cscan(queue, initPos);
        look(queue, initPos);
        clook(queue, initPos);
    }


    // Algorithms

    // FCFS
    public static void fcfs(ArrayList<Integer> queue, int initPos) {

        int prevPos = initPos,
            currPos,
            headMvmt = 0;

        // Go through the queue in order
        for (int i = 0; i < REQUESTS; i++) {

            currPos = queue.get(i);
            headMvmt += Math.abs(currPos - prevPos);
            prevPos = currPos;
        }

        System.out.println("FCFS: " + headMvmt + " cylinders.");
    }

    // SSTF
    public static void sstf(ArrayList<Integer> queue, int initPos) {

        queue = new ArrayList<Integer>(queue);
        queue.sort(null);

        int leftIdx,
            rightIdx = 0,
            currPos = initPos,
            leftReq = LOW,
            rightReq = HIGH,
            leftDist,
            rightDist,
            headMvmt = 0;

        // Get the indexes of the requests to the right and left of the initial position
        while (rightIdx < REQUESTS && queue.get(rightIdx) < initPos) {
            rightIdx++;
        }
        leftIdx = rightIdx - 1;

        for (int n = 0; n < REQUESTS; n++) {

            // Calculate distances to both requests
            if (leftIdx >= 0) {
                leftReq = queue.get(leftIdx);
                leftDist = currPos - leftReq;
            }
            else {
                leftDist = INF;
            }

            if (rightIdx < REQUESTS) {
                rightReq = queue.get(rightIdx);
                rightDist = rightReq - currPos;
            }
            else {
                rightDist = INF;
            }

            // Go to the closer request
            if (leftDist <= rightDist) {
                currPos = leftReq;
                leftIdx -= 1;
                headMvmt += leftDist;
            }
            else {
                currPos = rightReq;
                rightIdx += 1;
                headMvmt += rightDist;
            }
        }

        System.out.println("SSTF: " + headMvmt + " cylinders.");
    }

    // SCAN
    public static void scan(ArrayList<Integer> queue, int initPos) {

        queue = new ArrayList<Integer>(queue);
        queue.sort(null);

        int rightIdx = 0,
            firstReq = queue.get(0),
            lastReq = queue.get(REQUESTS - 1),
            headMvmt = 0;

        // Get the index of the request to the right of initial position
        while (rightIdx < REQUESTS && queue.get(rightIdx) < initPos) {
            rightIdx++;
        }

        if (rightIdx == 0) { // If all the requests are to the right
            headMvmt += lastReq - initPos;
        }
        else if (rightIdx == REQUESTS) { // If all the requests are to the left
            headMvmt += initPos - firstReq;
        }
        else if (rightIdx > (HIGH - LOW) / 2) { // If right end is closer
            headMvmt += HIGH - initPos; // To the rightmost cylinder
            headMvmt += HIGH - firstReq; // To the leftmost request
        }
        else { // If left end is closer
            headMvmt += initPos - LOW; // To the leftmost cylinder
            headMvmt += lastReq - LOW; // To the rightmost request
        }

        System.out.println("SCAN: " + headMvmt + " cylinders.");
    }

    // C-SCAN
    public static void cscan(ArrayList<Integer> queue, int initPos) {

        queue = new ArrayList<Integer>(queue);
        queue.sort(null);

        int rightIdx = 0,
            headMvmt = 0;

        // Get the index of the request to the right of initial position
        while (rightIdx < REQUESTS && queue.get(rightIdx) < initPos) {
            rightIdx++;
        }

        if (rightIdx == 0) { // If all the requests are to the right
            headMvmt += queue.get(REQUESTS - 1) - initPos;
        }
        else if (rightIdx == REQUESTS) { // If all the requests are to the left
            headMvmt += initPos - queue.get(0);
        }
        else if (rightIdx > (HIGH - LOW) / 2) { // If right end is closer
            headMvmt += HIGH - initPos; // To the right end
            headMvmt += queue.get(rightIdx - 1) - LOW; // From the left end to the request left of the initial postion
        }
        else { // If left end is closer
            headMvmt += initPos - LOW; // To the left end
            headMvmt += HIGH - queue.get(rightIdx); // From the right end to the request right of the initial postion
        }

        System.out.println("C-SCAN: " + headMvmt + " cylinders.");
    }

    // LOOK
    public static void look(ArrayList<Integer> queue, int initPos) {

        queue = new ArrayList<Integer>(queue);
        queue.sort(null);

        int rightIdx = 0,
            firstReq = queue.get(0),
            lastReq = queue.get(REQUESTS - 1),
            headMvmt = 0;

        // Get the index of the request to the right of initial position
        while (rightIdx < REQUESTS && queue.get(rightIdx) < initPos) {
            rightIdx++;
        }

        if (rightIdx == 0) { // If all the requests are to the right
            headMvmt += lastReq - initPos;
        }
        else if (rightIdx == REQUESTS) { // If all the requests are to the left
            headMvmt += initPos - firstReq;
        }
        else if (rightIdx > (HIGH - LOW) / 2) { // If right end is closer
            headMvmt += lastReq - initPos; // To the rightmost request
            headMvmt += lastReq - firstReq; // To the leftmost request
        }
        else { // If left end is closer
            headMvmt += initPos - firstReq; // To the leftmost request
            headMvmt += lastReq - firstReq; // To the rightmost request
        }

        System.out.println("LOOK: " + headMvmt + " cylinders.");
    }

    // C-LOOK
    public static void clook(ArrayList<Integer> queue, int initPos) {

        queue = new ArrayList<Integer>(queue);
        queue.sort(null);

        int rightIdx = 0,
            firstReq = queue.get(0),
            lastReq = queue.get(REQUESTS - 1),
            headMvmt = 0;

        // Get the index of the request to the right of initial position
        while (rightIdx < REQUESTS && queue.get(rightIdx) < initPos) {
            rightIdx++;
        }

        if (rightIdx == 0) { // If all the requests are to the right
            headMvmt += lastReq - initPos;
        }
        else if (rightIdx == REQUESTS) { // If all the requests are to the left
            headMvmt += initPos - firstReq;
        }
        else if (rightIdx > (HIGH - LOW) / 2) { // If right end is closer
            headMvmt += lastReq - initPos; // To the rightmost request
            headMvmt += queue.get(rightIdx - 1) - firstReq; // From the leftmost request to the request left of the initial position
        }
        else { // If left end is closer
            headMvmt += initPos - firstReq; // To the leftmost request
            headMvmt += lastReq - queue.get(rightIdx); // From the rightmost request to the request right of the initial position
        }

        System.out.println("C-LOOK: " + headMvmt + " cylinders.");
    }
}
