package flik;

/** An Integer tester created by Flik Enterprises.
 * @author Josh Hug
 * */
public class Flik {
    /** @param a Value 1
     *  @param b Value 2
     *  @return Whether a and b are the same */
    public static boolean isSameNumber(Integer a, Integer b) {
        // instance a != instance always, so we should use equal() instead of ==
        // in Java, when convert int a to Integer, if a < 128, Java just return an integer in cash
        // so the addresses for Integer a, Integer b whose value < 128 are the same
        // but for value >= 128, Java creates new instance for a and b, hence instance a != instance b
        // that is why isSameNumber(a, b) return false for a.val = b.val >= 128
        // but a.equal(b) is always true when a.val == b.val
        return a.equals(b);
    }
}
