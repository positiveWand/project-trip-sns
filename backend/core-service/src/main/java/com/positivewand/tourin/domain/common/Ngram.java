package com.positivewand.tourin.domain.common;

import java.util.ArrayList;
import java.util.List;

public class Ngram {
    public static List<String> parseNgrams(int n, String string) {
        if (string.contains(" ")) throw new IllegalArgumentException("문자열에 빈칸이 포함되면 안됩니다.");
        if (string.length() < n) throw new IllegalArgumentException("문자열은 n보다 커야합니다.");

        List<String> ngrams = new ArrayList<>();

        for (int i = 0; i < string.length() - n + 1; i++) {
            ngrams.add(string.substring(i, i+n));
        }

        return ngrams;
    }
}
