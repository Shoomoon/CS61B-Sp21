public class TestStdDraw {
    public static void main(String[] args) {
        StdDraw.setPenRadius(0.05);
//        StdDraw.setPenColor(StdDraw.BLUE);
//        StdDraw.point(0.5, 0.5);
//        StdDraw.setPenColor(StdDraw.MAGENTA);
//        StdDraw.line(0.2, 0.2, 0.8, 0.3);
//        StdDraw.setPenRadius(0.02);
//        StdDraw.square(0.2, 0.2, 0.1);
//        StdDraw.ellipse(0.2, 0.2, 0.1, 0.2);
//        StdDraw.setPenColor(StdDraw.GREEN);
//        StdDraw.circle(0.2, 0.2, 0.2);
//        StdDraw.rectangle(0.2, 0.2, 0.1, 0.2);
        double[] x0 = { 0.1, 0.2, 0.3, 0.2 };
        double[] y0 = { 0.2, 0.3, 0.2, 0.1 };
        StdDraw.filledPolygon(x0, y0);
        StdDraw.setTitle("imageTest");

        StdDraw.setScale(-2, +2);
        StdDraw.enableDoubleBuffering();

        for (double t = 0.0; true; t += 0.02) {
            double x = Math.sin(t);
            double y = Math.cos(t);
            StdDraw.clear();
            StdDraw.filledCircle(x, y, 0.05);
            StdDraw.filledCircle(-x, -y, 0.05);
            StdDraw.show();
            StdDraw.pause(20);
        }
    }
}
  