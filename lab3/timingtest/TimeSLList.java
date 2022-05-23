package timingtest;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        Integer testCases = 8;
        Integer curSize = 1000;
        Integer testSize = 10000;
        AList<Integer> n = new AList<Integer>();
        AList<Double> timeCost = new AList<Double>();
        AList<Integer> operations = new AList<Integer>();
        for (int i = 0; i < testCases; i++) {
            n.addLast(curSize);
            operations.addLast(testSize);
            SLList<Integer> ll = new SLList<Integer>();
            for (int j = 0; j < curSize; j++) {
                ll.addLast(j);
            }
            long start = System.currentTimeMillis();
            for (int j = 0; j < testSize; j++) {
                ll.getLast();
            }
            long end = System.currentTimeMillis();
            timeCost.addLast(0.001 * (end - start));
            curSize *= 2;
        }
        printTimingTable(n, timeCost, operations);
    }

}
