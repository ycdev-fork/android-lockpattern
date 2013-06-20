/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package group.pals.android.lib.ui.lockpattern.widget;

import group.pals.android.lib.ui.lockpattern.BuildConfig;
import group.pals.android.lib.ui.lockpattern.collect.Lists;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.util.Log;

/**
 * Utilities for the lock pattern and its settings.
 */
public class LockPatternUtils {

    /**
     * Used for debugging...
     */
    private static final String CLASSNAME = LockPatternUtils.class.getName();

    /**
     * "UTF-8"
     */
    public static final String UTF8 = "UTF-8";

    /**
     * "SHA-1"
     */
    public static final String SHA1 = "SHA-1";

    /**
     * Deserialize a pattern.
     * 
     * @param string
     *            The pattern serialized with {@link #patternToString}
     * @return The pattern.
     */
    public static List<LockPatternView.Cell> stringToPattern(String string) {
        List<LockPatternView.Cell> result = Lists.newArrayList();

        try {
            final byte[] bytes = string.getBytes(UTF8);
            for (int i = 0; i < bytes.length; i++) {
                byte b = bytes[i];
                result.add(LockPatternView.Cell.of(b / 3, b % 3));
            }
        } catch (UnsupportedEncodingException e) {
            // never catch this
        }

        return result;
    }// stringToPattern()

    /**
     * Serialize a pattern.
     * 
     * @param pattern
     *            The pattern.
     * @return The pattern in string form.
     */
    public static String patternToString(List<LockPatternView.Cell> pattern) {
        if (pattern == null) {
            return "";
        }
        final int patternSize = pattern.size();

        byte[] res = new byte[patternSize];
        for (int i = 0; i < patternSize; i++) {
            LockPatternView.Cell cell = pattern.get(i);
            res[i] = (byte) (cell.getRow() * 3 + cell.getColumn());
        }
        try {
            return new String(res, UTF8);
        } catch (UnsupportedEncodingException e) {
            // never catch this
            return "";
        }
    }// patternToString()

    /**
     * Serializes a pattern
     * 
     * @param pattern
     *            The pattern
     * @return The SHA-1 string of the pattern got from
     *         {@link #patternToString(List)}
     */
    public static String patternToSha1(List<LockPatternView.Cell> pattern) {
        try {
            MessageDigest md = MessageDigest.getInstance(SHA1);
            md.update(patternToString(pattern).getBytes(UTF8));

            byte[] digest = md.digest();
            BigInteger bi = new BigInteger(1, digest);
            return String.format((Locale) null,
                    "%0" + (digest.length * 2) + "x", bi).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            // never catch this
            return "";
        } catch (UnsupportedEncodingException e) {
            // never catch this
            return "";
        }
    }// patternToSha1()

    /**
     * Generates a random "CAPTCHA" pattern. By saying "CAPTCHA", this method
     * ensures that the generated pattern is easy for the user to re-draw.
     * <p>
     * <b>Notes:</b> This method is <b>not</b> optimized and <b>not</b>
     * benchmarked yet for large size of the pattern's matrix. Currently it
     * works fine with a matrix of {@code 3x3} cells. Be careful when the size
     * increases.
     * </p>
     * 
     * @param size
     *            the size of the pattern to be generated.
     * @return the generated pattern.
     * @since v2.7 beta
     * @author Hai Bison
     */
    public static ArrayList<LockPatternView.Cell> genCaptchaPattern(int size) {
        final List<Integer> usedIds = Lists.newArrayList();
        final Random random = new Random();

        final ArrayList<LockPatternView.Cell> result = Lists.newArrayList();
        int lastId = random
                .nextInt((int) (System.nanoTime() % Integer.MAX_VALUE))
                % LockPatternView.MATRIX_SIZE;
        do {
            if (BuildConfig.DEBUG)
                Log.d(CLASSNAME, " >> lastId = " + lastId);

            result.add(LockPatternView.Cell.of(lastId));
            usedIds.add(lastId);

            final int row = lastId / LockPatternView.MATRIX_WIDTH;
            final int col = lastId % LockPatternView.MATRIX_WIDTH;

            /*
             * This is the max available rows/ columns that we can reach from
             * the cell of `lastId` to the border of the matrix.
             */
            final int maxDistance = Math.max(
                    Math.max(row, LockPatternView.MATRIX_WIDTH - row),
                    Math.max(col, LockPatternView.MATRIX_WIDTH - col));

            /*
             * Starting from `distance` = 1, find the closest-available
             * neighbour value of `lastId`.
             */
            for (int distance = 1; distance <= maxDistance; distance++) {
                final List<Integer> possibleIds = Lists.newArrayList();

                /*
                 * Now we have a square surrounding the current cell. We call it
                 * ABCD, in which A is top-left, and C is bottom-right.
                 * 
                 * We add all available points in AB, BC, CD, DA to the list.
                 */

                final int rowA = row - distance;
                final int colA = col - distance;
                final int rowC = row + distance;
                final int colC = col + distance;

                int id;

                /*
                 * AB
                 */
                if (rowA >= 0) {
                    for (int c = Math.max(0, colA); c < Math.min(
                            LockPatternView.MATRIX_WIDTH, colC + 1); c++) {
                        id = rowA * LockPatternView.MATRIX_WIDTH + c;
                        if (!usedIds.contains(id))
                            possibleIds.add(id);
                    }
                }

                /*
                 * BC
                 */
                if (colC < LockPatternView.MATRIX_WIDTH) {
                    for (int r = Math.max(0, rowA + 1); r < Math.min(
                            LockPatternView.MATRIX_WIDTH, rowC + 1); r++) {
                        id = r * LockPatternView.MATRIX_WIDTH + colC;
                        if (!usedIds.contains(id))
                            possibleIds.add(id);
                    }
                }

                /*
                 * DC
                 */
                if (rowC < LockPatternView.MATRIX_WIDTH) {
                    for (int c = Math.max(0, colA); c < Math.min(
                            LockPatternView.MATRIX_WIDTH, colC); c++) {
                        id = rowC * LockPatternView.MATRIX_WIDTH + c;
                        if (!usedIds.contains(id))
                            possibleIds.add(id);
                    }
                }

                /*
                 * AD
                 */
                if (colA >= 0) {
                    for (int r = Math.max(0, rowA + 1); r < Math.min(
                            LockPatternView.MATRIX_WIDTH, rowC); r++) {
                        id = r * LockPatternView.MATRIX_WIDTH + colA;
                        if (!usedIds.contains(id))
                            possibleIds.add(id);
                    }
                }

                if (possibleIds.isEmpty())
                    continue;

                /*
                 * NOW GET ONE OF POSSIBLE IDS AND ASSIGN IT TO `lastId`
                 */
                lastId = possibleIds.get(random.nextInt((int) (System
                        .nanoTime() % Integer.MAX_VALUE)) % possibleIds.size());
                break;
            }// for delta
        } while (result.size() < size);

        return result;
    }// genCaptchaPattern()
}
