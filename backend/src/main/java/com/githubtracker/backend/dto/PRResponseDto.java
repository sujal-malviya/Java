package com.githubtracker.backend.dto;

import java.util.List;

public class PRResponseDto {

    private List<String> labels;
    private List<Integer> counts;
    private int total;   // ✅ MUST be named 'total' (not totalCount)

    public PRResponseDto() {
    }

    public PRResponseDto(List<String> labels, List<Integer> counts, int total) {
        this.labels = labels;
        this.counts = counts;
        this.total = total;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<Integer> getCounts() {
        return counts;
    }

    public void setCounts(List<Integer> counts) {
        this.counts = counts;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
