package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import timingtest.AList;

import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    // YOUR TESTS HERE
//    private BuggyAList<Integer> a0 = new BuggyAList<Integer>();
//    private AListNoResizing<Integer> a1 = new AListNoResizing<Integer>();
//    public void main(String[] args) {
//        Integer testCases = 500;
//        for (int i = 0; i < testCases; i++) {
//            int operations = StdRandom.uniform(0, 2);
//            if (operations == 0) {
//                int val = StdRandom.uniform(0, 10);
//                a0.addLast(val);
//                a1.addLast(val);
//            } else {
//                int size0 = a0.size();
//                int size1 = a1.size();
//            }
//        }
//    }
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<Integer>();
        BuggyAList<Integer> B = new BuggyAList<Integer>();
        Boolean passed =true;
        int N = 2000;
        int maxsize = 1000;
        for (int i = 0; i < N; i += 1) {
            if (L.size() != B.size()) {
                System.out.println("Error: L and B have different size!");
                passed = false;
                break;
            }
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                if (L.size() == maxsize) {
                    System.out.println("Add element to a FULL List!");
                    break;
                }
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
            } else if (operationNumber == 1) {
                // getLast
                if (L.size() == 0) {
//                    System.out.println("Error: GetLast when empty!");
                    continue;
                }
                int last0 = L.getLast();
                int last1 = B.getLast();
                if (last0 != last1) {
                    System.out.println("Error: L and B have different last element!");
                    passed = false;
                    break;
                }
            } else {
                // removeLast
                if (L.size() == 0) {
//                    System.out.println("Error: Remove_ast when empty!");
                    continue;
                }
                int last0 = L.removeLast();
                int last1 = B.removeLast();
                if (last0 != last1) {
                    System.out.println("Error: L and B have different last element!");
                    passed = false;
                    break;
                }
            }
        }
        if (passed) {
            System.out.println("Success: All " + N + " test cases passed!");
        } else {
            System.out.println("Fail: Not all test cases passed!");
        }
    }
    public static void main(String[] arg) {
        TestBuggyAList test = new TestBuggyAList();
        test.randomizedTest();
    }
}
