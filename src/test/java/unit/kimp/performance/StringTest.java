package unit.kimp.performance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class StringTest {

    @Test
    @DisplayName("String builder | String buffer | String 성능 테스트")
    public void StringTest() {
        java.lang.String[] arr = new java.lang.String[10];

        for (int i = 0; i < arr.length; i++) {
            java.lang.String randomString = generateRandomHangul(20);
            arr[i] = randomString;
        }

        // 1. StringBuilder - 스레드 안정성 불필요
        long start = System.nanoTime();
        StringBuilder builder = new StringBuilder();
        for (java.lang.String str : arr) {
            builder.append(str);
        }
        long end = System.nanoTime();
        System.out.println("StringBuilder: " + (end - start) + " ns");

        // 2. StringBuffer - 스레드 안정성 필요
        long start2 = System.nanoTime();
        StringBuffer sb = new StringBuffer();
        for (java.lang.String str : arr) {
            sb.append(str);
        }
        long end2 = System.nanoTime();
        System.out.println("StringBuffer: " + (end2 - start2) + " ns");

        // 3. String
        long start3 = System.nanoTime();
        java.lang.String combinedStr = "";
        for (java.lang.String str : arr) {
            combinedStr += str;
        }
        long end3 = System.nanoTime();
        System.out.println("String: " + (end3 - start3) + " ns");
    }

    public static java.lang.String generateRandomHangul(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        int start = 0xAC00; // '가'
        int end = 0xD7A3;  // '힣'
        int range = end - start + 1;

        for (int i = 0; i < length; i++) {
            int codePoint = start + random.nextInt(range);
            sb.append((char) codePoint);
        }

        return sb.toString();
    }
}
