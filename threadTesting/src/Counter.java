public class Counter {
    public static int count = 0;
    public static void inc() {
        try {
            Thread.sleep(1);
        } catch (Exception e) {
            System.out.println("Error in inc()");
        }
        count++;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Counter.inc();
                }
            }).start();
        }
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            System.out.print("Error!");
        }
        System.out.print("Final count is " + count);
    }
}
