package com.positivewand.tourin.domain.common;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class NgramTest {
    @Test
    void N_보다_길이가_짧은_문자열은_Ngram을_만들_수_없다() {
        assertThrows(IllegalArgumentException.class, () -> Ngram.parseNgrams(1, ""));
        assertThrows(IllegalArgumentException.class, () -> Ngram.parseNgrams(2, "d"));
        assertThrows(IllegalArgumentException.class, () -> Ngram.parseNgrams(3, "dd"));
        assertThrows(IllegalArgumentException.class, () -> Ngram.parseNgrams(4, "ddf"));
        assertThrows(IllegalArgumentException.class, () -> Ngram.parseNgrams(5, "ddfd"));
        assertThrows(IllegalArgumentException.class, () -> Ngram.parseNgrams(6, "dddff"));
    }

    @Test
    void 빈칸을_포함한_문자열은_Ngram을_만들_수_없다() {
        assertThrows(IllegalArgumentException.class, () -> Ngram.parseNgrams(2, "df "));
        assertThrows(IllegalArgumentException.class, () -> Ngram.parseNgrams(2, "dfdfdf   "));
        assertThrows(IllegalArgumentException.class, () -> Ngram.parseNgrams(2, " dfdfdf"));
        assertThrows(IllegalArgumentException.class, () -> Ngram.parseNgrams(2, "dfdfdf dfdfdfd"));
        assertThrows(IllegalArgumentException.class, () -> Ngram.parseNgrams(2, "df      "));
        assertThrows(IllegalArgumentException.class, () -> Ngram.parseNgrams(2, "   "));
    }

    @Test
    void 조건을_만족하는_문자열로부터_Ngram이_올바르게_생성된다() {
        assertIterableEquals(Ngram.parseNgrams(0, ""), Arrays.asList(""));
        assertIterableEquals(Ngram.parseNgrams(1, "가나다라마바"), Arrays.asList("가", "나", "다", "라", "마", "바"));
        assertIterableEquals(Ngram.parseNgrams(2, "가나다라마바"), Arrays.asList("가나", "나다", "다라", "라마", "마바"));
        assertIterableEquals(Ngram.parseNgrams(3, "가나다라마바"), Arrays.asList("가나다", "나다라", "다라마", "라마바"));
        assertIterableEquals(Ngram.parseNgrams(4, "가나다라마바"), Arrays.asList("가나다라", "나다라마", "다라마바"));
        assertIterableEquals(Ngram.parseNgrams(5, "가나다라마바"), Arrays.asList("가나다라마", "나다라마바"));
        assertIterableEquals(Ngram.parseNgrams(6, "가나다라마바"), Arrays.asList("가나다라마바"));
    }
}