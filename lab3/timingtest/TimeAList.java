package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
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
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        int testCases = 8;
        int curSize = 1000;
        AList<Integer> n = new AList<Integer>();
        AList<Double> timeCost = new AList<Double>();
        AList<Integer> operations = new AList<Integer>();
        for (int i = 0; i < testCases; i++) {
            n.addLast(curSize);
            operations.addLast(curSize);
            AList<Integer> a = new AList<Integer>();
            long t0 = System.currentTimeMillis();
            for (int j = 0; j < curSize; j++) {
                a.addLast(j);
            }
            long t1 = System.currentTimeMillis();
            timeCost.addLast(0.001 * (t1 - t0));
            curSize *= 2;
        }
        printTimingTable(n, timeCost, operations);
    }
}
