package com.codernawaki.portfolio;

import org.springframework.data.domain.Sort;

public enum AdminSubmissionSort {
    NEWEST("Newest first", Sort.by(Sort.Order.desc("createdAt"))),
    OLDEST("Oldest first", Sort.by(Sort.Order.asc("createdAt"))),
    NAME("Name A-Z", Sort.by(Sort.Order.asc("name"), Sort.Order.desc("createdAt"))),
    STATUS("Status", Sort.by(Sort.Order.asc("status"), Sort.Order.desc("createdAt")));

    private final String label;
    private final Sort sort;

    AdminSubmissionSort(String label, Sort sort) {
        this.label = label;
        this.sort = sort;
    }

    public String getLabel() {
        return label;
    }

    public Sort toSort() {
        return sort;
    }
}
