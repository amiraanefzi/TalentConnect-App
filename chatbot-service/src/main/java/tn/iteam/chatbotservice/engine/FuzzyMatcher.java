package tn.iteam.chatbotservice.engine;

import java.util.Locale;

/**
 * Fuzzy Matcher for intelligent spell-tolerance matching
 * Uses Levenshtein distance to find close matches even with typos/misspellings
 */
public class FuzzyMatcher {

    // Threshold for fuzzy matching (0-100, higher = stricter)
    private static final int DEFAULT_THRESHOLD = 70;

    private FuzzyMatcher() {
        // Utility class - prevent instantiation
    }

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
        int distance = damerauLevenshteinDistance(s1, s2);
        return (int) ((1.0 - (double) distance / maxLen) * 100);
    }

    /**
     * Length-adaptive fuzzy match tuned for keyword/typo detection.
     * Allows a bounded number of edits (including adjacent letter swaps)
     * relative to the reference word length, which avoids false positives
     * on short words while staying tolerant on longer ones.
     *
     * @param input   token coming from the user message
     * @param keyword reference keyword to match against
     * @return true when {@code input} is a close misspelling of {@code keyword}
     */
    public static boolean isFuzzyMatch(String input, String keyword) {
        if (input == null || keyword == null) {
            return false;
        }
        String a = input.toLowerCase(Locale.ROOT);
        String b = keyword.toLowerCase(Locale.ROOT);
        if (a.equals(b)) {
            return true;
        }

        int len = b.length();
        int allowed;
        if (len < 4) {
            allowed = 0;          // short words must match exactly (avoids false positives)
        } else if (len <= 6) {
            allowed = 1;          // medium words tolerate one edit
        } else {
            allowed = 2;          // long words tolerate two edits
        }
        if (allowed == 0) {
            return false;
        }
        if (Math.abs(a.length() - len) > allowed) {
            return false;
        }
        return damerauLevenshteinDistance(a, b) <= allowed;
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
     * Calculate the Damerau-Levenshtein distance (optimal string alignment).
     * Unlike plain Levenshtein, it treats an adjacent character transposition
     * (e.g. "bonjuor" vs "bonjour") as a single edit, which matches very common
     * typing mistakes.
     *
     * @param s1 first string
     * @param s2 second string
     * @return edit distance
     */
    public static int damerauLevenshteinDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();
        if (len1 == 0) {
            return len2;
        }
        if (len2 == 0) {
            return len1;
        }

        int[][] dp = new int[len1 + 1][len2 + 1];
        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(
                                dp[i - 1][j] + 1,      // deletion
                                dp[i][j - 1] + 1       // insertion
                        ),
                        dp[i - 1][j - 1] + cost        // substitution
                );
                // adjacent transposition
                if (i > 1 && j > 1
                        && s1.charAt(i - 1) == s2.charAt(j - 2)
                        && s1.charAt(i - 2) == s2.charAt(j - 1)) {
                    dp[i][j] = Math.min(dp[i][j], dp[i - 2][j - 2] + 1);
                }
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

