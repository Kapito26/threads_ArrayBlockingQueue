import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Main {

    public static final int textLength = 100_000;
    public static final int textsCount = 10_000;
    public static final int blockSize = 100;
    public static BlockingQueue<String> arrA = new ArrayBlockingQueue<>(blockSize);
    public static BlockingQueue<String> arrB = new ArrayBlockingQueue<>(blockSize);
    public static BlockingQueue<String> arrC = new ArrayBlockingQueue<>(blockSize);
    public static AtomicInteger maxCountA = new AtomicInteger(0);
    public static AtomicInteger maxCountB = new AtomicInteger(0);
    public static AtomicInteger maxCountC = new AtomicInteger(0);
    public static AtomicReference<String> maxStrA = new AtomicReference<>("");
    public static AtomicReference<String> maxStrB = new AtomicReference<>("");
    public static AtomicReference<String> maxStrC = new AtomicReference<>("");

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static int countFind(char ch, String str) {
        return str.length() - str.replace(String.valueOf(ch), "").length();
    }

    public static String maxStrFind(char ch, String strMax, String strNew) {
        int count = countFind(ch, strNew);
        // System.out.println("взяли " + ch + ": " + strNew + " - " + count);
        String strReturn = strMax;
        if (ch == 'a' && count > maxCountA.get()) {
            maxCountA.set(count);
            strReturn = strNew;
        }
        if (ch == 'b' && count > maxCountB.get()) {
            maxCountB.set(count);
            strReturn = strNew;
        }
        if (ch == 'c' && count > maxCountC.get()) {
            maxCountC.set(count);
            strReturn = strNew;
        }
        return strReturn;
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t0 = new Thread(() -> {
            for (int i = 0; i < textsCount; i++) {
                try {
                    String text = generateText("abc", textLength);
                    arrA.put(text);
                    arrB.put(text);
                    arrC.put(text);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        Thread tA = new Thread(() -> {
            for (int i = 0; i < textsCount; i++) {
                try {
                    maxStrA.set(maxStrFind('a', maxStrA.get(), arrA.take()));
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        Thread tB = new Thread(() -> {
            for (int i = 0; i < textsCount; i++) {
                try {
                    maxStrB.set(maxStrFind('b', maxStrB.get(), arrB.take()));
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        Thread tC = new Thread(() -> {
            for (int i = 0; i < textsCount; i++) {
                try {
                    maxStrC.set(maxStrFind('c', maxStrC.get(), arrC.take()));
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        t0.start();
        tA.start();
        tB.start();
        tC.start();

        t0.join();
        tA.join();
        tB.join();
        tC.join();

        System.out.println("Строка с наибольшим кол-вом a (" + maxCountA + " вхождений): " + maxStrA);
        System.out.println("Строка с наибольшим кол-вом b (" + maxCountB + " вхождений): " + maxStrB);
        System.out.println("Строка с наибольшим кол-вом c (" + maxCountC + " вхождений): " + maxStrC);
    }
}
