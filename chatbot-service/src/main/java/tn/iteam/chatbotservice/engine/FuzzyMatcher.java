package tn.iteam.chatbotservice.engine;

import java.util.Locale;

/**
 * Fuzzy Matcher for intelligent spell-tolerance matching
 * Uses Levenshtein distance to find close matches even with typos/misspellings
 */
public class FuzzyMatcher {

    // Threshold for fuzzy matching (0-100, higher = stricter)
    private static final int DEFAULT_THRESHOLD = 70;

    /**
     * Calculate similarity percentage between two strings using Levenshtein distance
     * @param str1 first string
     * @param str2 second string
     * @return similarity percentage (0-100)
     */
    public static int similarity(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return str1 == null && str2 == null ? 100 : 0;
        }

        String s1 = str1.toLowerCase(Locale.ROOT);
        String s2 = str2.toLowerCase(Locale.ROOT);

        if (s1.equals(s2)) {
            return 100;
        }

        if (s1.isEmpty() || s2.isEmpty()) {
            return 0;
        }

        int maxLen = Math.max(s1.length(), s2.length());
        int distance = levenshteinDistance(s1, s2);
        return (int) ((1.0 - (double) distance / maxLen) * 100);
    }

    /**
     * Check if two strings are similar enough (above threshold)
     * @param str1 first string
     * @param str2 second string
     * @param threshold minimum similarity percentage required
     * @return true if similarity is >= threshold
     */
    public static boolean isSimilar(String str1, String str2, int threshold) {
        return similarity(str1, str2) >= threshold;
    }

    /**
     * Check if two strings are similar enough (using default threshold)
     * @param str1 first string
     * @param str2 second string
     * @return true if similarity is >= default threshold
     */
    public static boolean isSimilar(String str1, String str2) {
        return isSimilar(str1, str2, DEFAULT_THRESHOLD);
    }

    /**
     * Find the best matching string from a list of candidates
     * @param input input string to match
     * @param candidates list of candidate strings
     * @param threshold minimum similarity threshold
     * @return best matching candidate or empty string if no match meets threshold
     */
    public static String findBestMatch(String input, String[] candidates, int threshold) {
        String bestMatch = "";
        int bestScore = threshold;

        for (String candidate : candidates) {
            int score = similarity(input, candidate);
            if (score > bestScore) {
                bestScore = score;
                bestMatch = candidate;
            }
        }

        return bestMatch;
    }

    /**
     * Find the best matching string from a list of candidates (default threshold)
     */
    public static String findBestMatch(String input, String[] candidates) {
        return findBestMatch(input, candidates, DEFAULT_THRESHOLD);
    }

    /**
     * Calculate Levenshtein distance between two strings
     * @param s1 first string
     * @param s2 second string
     * @return distance
     */
    public static int levenshteinDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();

        // Initialize array with dimensions [len1+1][len2+1]
        int[][] dp = new int[len1 + 1][len2 + 1];

        // Initialize first column
        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }

        // Initialize first row
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        // Fill the matrix
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;

                dp[i][j] = Math.min(
                        Math.min(
                                dp[i - 1][j] + 1,      // deletion
                                dp[i][j - 1] + 1      // insertion
                        ),
                        dp[i - 1][j - 1] + cost    // substitution
                );
            }
        }

        return dp[len1][len2];
    }

    /**
     * Quick similarity check for partial strings (for token-based matching)
     * Useful for matching keywords and phrases with typos
     */
    public static boolean hasCloseMatch(String text, String[] keywords, int threshold) {
        String[] tokens = text.split("[^a-z0-9éèêëàâäùûüôöœçñ]+");

        for (String token : tokens) {
            if (token.length() < 2) continue;

            for (String keyword : keywords) {
                if (isSimilar(token, keyword, threshold)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check if text contains any close match (using default threshold)
     */
    public static boolean hasCloseMatch(String text, String[] keywords) {
        return hasCloseMatch(text, keywords, DEFAULT_THRESHOLD);
    }
}

