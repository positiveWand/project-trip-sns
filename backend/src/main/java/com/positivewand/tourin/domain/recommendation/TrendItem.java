package com.positivewand.tourin.domain.recommendation;

public record TrendItem(long itemId, double score) implements Comparable<TrendItem> {
    @Override
    public int compareTo(TrendItem other) {
        int c = Double.compare(this.score(), other.score());
        if (c != 0) return c;
        return Long.compare(this.itemId(), other.itemId());
    }
}
