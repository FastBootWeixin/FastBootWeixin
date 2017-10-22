/*
 * Copyright (c) 2016-2017, Guangshan (guangshan1992@qq.com) and the original author or authors.
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

package com.mxixm.fastboot.weixin.util;

import java.util.ArrayList;
import java.util.Stack;

/**
 * FastBootWeixin WildcardUtils
 * 通配符匹配
 * copy from filenameUtils
 *
 * @author Guangshan
 * @date 2017/8/21 22:47
 * @since 0.1.2
 */
public class WildcardUtils {

    //-----------------------------------------------------------------------

    /**
     * Checks a filename to see if it matches the specified wildcard matcher,
     * always testing case-sensitive.
     * <p>
     * The wildcard matcher uses the characters '?' and '*' to represent a
     * single or multiple (zero or more) wildcard characters.
     * This is the same as often found on Dos/Unix command lines.
     * The check is case-sensitive always.
     * <pre>
     * wildcardMatch("c.txt", "*.txt")      -- true
     * wildcardMatch("c.txt", "*.jpg")      -- false
     * wildcardMatch("a/b/c.txt", "a/b/*")  -- true
     * wildcardMatch("c.txt", "*.???")      -- true
     * wildcardMatch("c.txt", "*.????")     -- false
     * </pre>
     * N.B. the sequence "*?" does not work properly at present in match strings.
     *
     * @param content         the content to match on
     * @param wildcardMatcher the wildcard string to match against
     * @return true if the filename matches the wilcard string
     */
    public static boolean wildcardMatch(String content, String wildcardMatcher) {
        return wildcardMatch(content, wildcardMatcher, true);
    }

    /**
     * Checks a filename to see if it matches the specified wildcard matcher
     * allowing control over case-sensitivity.
     * <p>
     * The wildcard matcher uses the characters '?' and '*' to represent a
     * single or multiple (zero or more) wildcard characters.
     * N.B. the sequence "*?" does not work properly at present in match strings.
     *
     * @param content         the filename to match on
     * @param wildcardMatcher the wildcard string to match against
     * @param caseSensitivity what case sensitivity rule to use, null means case-sensitive
     * @return true if the filename matches the wilcard string
     * @since 1.3
     */
    public static boolean wildcardMatch(String content, String wildcardMatcher, boolean caseSensitivity) {
        if (content == null && wildcardMatcher == null) {
            return true;
        }
        if (content == null || wildcardMatcher == null) {
            return false;
        }
        String[] wcs = splitOnTokens(wildcardMatcher);
        boolean anyChars = false;
        int textIdx = 0;
        int wcsIdx = 0;
        Stack<int[]> backtrack = new Stack<>();

        // loop around a backtrack stack, to handle complex * matching
        do {
            if (backtrack.size() > 0) {
                int[] array = backtrack.pop();
                wcsIdx = array[0];
                textIdx = array[1];
                anyChars = true;
            }

            // loop whilst tokens and text left to process
            while (wcsIdx < wcs.length) {

                if ("?".equals(wcs[wcsIdx])) {
                    // ? so move to next text char
                    textIdx++;
                    if (textIdx > content.length()) {
                        break;
                    }
                    anyChars = false;

                } else if ("*".equals(wcs[wcsIdx])) {
                    // set any chars status
                    anyChars = true;
                    if (wcsIdx == wcs.length - 1) {
                        textIdx = content.length();
                    }

                } else {
                    // matching text token
                    if (anyChars) {
                        // any chars then try to locate text token
                        textIdx = checkIndexOf(content, textIdx, wcs[wcsIdx], caseSensitivity);
                        if (textIdx == -1) {
                            // token not found
                            break;
                        }
                        int repeat = checkIndexOf(content, textIdx + 1, wcs[wcsIdx], caseSensitivity);
                        if (repeat >= 0) {
                            backtrack.push(new int[]{wcsIdx, repeat});
                        }
                    } else {
                        // matching from current position
                        if (!checkRegionMatches(content, textIdx, wcs[wcsIdx], caseSensitivity)) {
                            // couldnt match token
                            break;
                        }
                    }

                    // matched text token, move text index to end of matched token
                    textIdx += wcs[wcsIdx].length();
                    anyChars = false;
                }

                wcsIdx++;
            }

            // full match
            if (wcsIdx == wcs.length && textIdx == content.length()) {
                return true;
            }

        } while (backtrack.size() > 0);

        return false;
    }

    /**
     * Splits a string into a number of tokens.
     * The text is split by '?' and '*'.
     * Where multiple '*' occur consecutively they are collapsed into a single '*'.
     *
     * @param text the text to split
     * @return the array of tokens, never null
     */
    static String[] splitOnTokens(String text) {
        // used by wildcardMatch
        // package level so a unit test may run on this

        if (text.indexOf('?') == -1 && text.indexOf('*') == -1) {
            return new String[]{text};
        }

        char[] array = text.toCharArray();
        ArrayList<String> list = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (array[i] == '?' || array[i] == '*') {
                if (buffer.length() != 0) {
                    list.add(buffer.toString());
                    buffer.setLength(0);
                }
                if (array[i] == '?') {
                    list.add("?");
                } else if (list.isEmpty() ||
                        i > 0 && "*".equals(list.get(list.size() - 1)) == false) {
                    list.add("*");
                }
            } else {
                buffer.append(array[i]);
            }
        }
        if (buffer.length() != 0) {
            list.add(buffer.toString());
        }

        return list.toArray(new String[list.size()]);
    }

    private static int checkIndexOf(String str, int strStartIndex, String search, boolean caseSensitivity) {
        int endIndex = str.length() - search.length();
        if (endIndex >= strStartIndex) {
            for (int i = strStartIndex; i <= endIndex; i++) {
                if (checkRegionMatches(str, i, search, caseSensitivity)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static boolean checkRegionMatches(String str, int strStartIndex, String search, boolean caseSensitivity) {
        return str.regionMatches(!caseSensitivity, strStartIndex, search, 0, search.length());
    }

}
