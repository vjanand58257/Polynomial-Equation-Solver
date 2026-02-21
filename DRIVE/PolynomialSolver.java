import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.math.BigInteger;
import java.io.*;

public class PolynomialSolver {

    static class Point {
        BigInteger x;
        BigInteger y;

        Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    static BigInteger convert(String value, int base) {
        return new BigInteger(value, base);
    }

    static BigInteger solveSecret(String filename) throws Exception {

        String json = Files.readString(Path.of(filename));

        Pattern kPattern = Pattern.compile("\"k\"\\s*:\\s*(\\d+)");
        Matcher km = kPattern.matcher(json);
        km.find();
        int k = Integer.parseInt(km.group(1));

        Pattern rootPattern = Pattern.compile(
            "\"(\\d+)\"\\s*:\\s*\\{\\s*\"base\"\\s*:\\s*\"(\\d+)\"\\s*,\\s*\"value\"\\s*:\\s*\"([0-9a-zA-Z]+)\"");

        Matcher m = rootPattern.matcher(json);

        List<Point> points = new ArrayList<>();

        while (m.find()) {
            BigInteger x = new BigInteger(m.group(1));
            int base = Integer.parseInt(m.group(2));
            String value = m.group(3);

            BigInteger y = convert(value, base);
            points.add(new Point(x, y));
        }

        points = points.subList(0, k);

        BigInteger secret = BigInteger.ZERO;

        for (int i = 0; i < points.size(); i++) {

            BigInteger num = BigInteger.ONE;
            BigInteger den = BigInteger.ONE;

            for (int j = 0; j < points.size(); j++) {
                if (i != j) {
                    num = num.multiply(points.get(j).x.negate());
                    den = den.multiply(points.get(i).x.subtract(points.get(j).x));
                }
            }

            BigInteger term = points.get(i).y.multiply(num).divide(den);
            secret = secret.add(term);
        }

        return secret;
    }

    public static void main(String[] args) throws Exception {

        String[] tests = {"test1.json", "test2.json"};

        PrintWriter out = new PrintWriter(new FileWriter("output.txt"));

        for (String file : tests) {
            BigInteger secret = solveSecret(file);

            String result = file + " → Secret = " + secret;

            System.out.println(result);
            out.println(result);
        }

        out.close();

        System.out.println("\n✅ Output saved in output.txt");
    }
}