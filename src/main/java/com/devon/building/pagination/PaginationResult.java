package com.devon.building.pagination;

import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class PaginationResult<E> {

    private int totalRecords;
    private int currentPage;
    private List<E> list;
    private int maxResult;
    private int totalPages;
    private int maxNavigationPage;
    private List<Integer> navigationPages;

    public PaginationResult(TypedQuery<E> query, TypedQuery<Long> countQuery, int page, int maxResult, int maxNavigationPage) {
        if (maxResult < 1) {
            throw new IllegalArgumentException("maxResult must be greater than zero");
        }
        this.maxResult = maxResult;
        this.maxNavigationPage = Math.max(maxNavigationPage, 1);

        // Count first so an out-of-range request can be clamped before fetching rows.
        this.totalRecords = countQuery.getSingleResult().intValue();
        this.totalPages = (int) Math.ceil((double) totalRecords / maxResult);
        this.currentPage = totalPages == 0 ? 1 : Math.min(Math.max(page, 1), totalPages);
        this.list = query.setFirstResult((currentPage - 1) * maxResult).setMaxResults(maxResult).getResultList();
        calcNavigationPages();
    }

    private void calcNavigationPages() {
        navigationPages = buildNavigationPages(currentPage, totalPages, maxNavigationPage);
    }

    public static List<Integer> buildNavigationPages(int currentPage, int totalPages, int maxNavigationPage) {
        List<Integer> pages = new ArrayList<>();
        if (totalPages <= 0) {
            return pages;
        }

        int windowSize = Math.max(maxNavigationPage, 1);
        if (totalPages <= windowSize) {
            for (int page = 1; page <= totalPages; page++) pages.add(page);
            return pages;
        }

        int current = Math.min(Math.max(currentPage, 1), totalPages);
        int start = Math.max(1, current - windowSize / 2);
        int end = start + windowSize - 1;
        if (end > totalPages) {
            end = totalPages;
            start = end - windowSize + 1;
        }

        if (start > 2) {
            pages.add(1);
            pages.add(-1);
        } else {
            start = 1;
        }
        for (int page = start; page <= end; page++) pages.add(page);
        if (end < totalPages - 1) {
            pages.add(-1);
            pages.add(totalPages);
        } else {
            for (int page = end + 1; page <= totalPages; page++) pages.add(page);
        }
        return pages;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public List<E> getList() {
        return list;
    }

    public int getMaxResult() {
        return maxResult;
    }

    public List<Integer> getNavigationPages() {
        return navigationPages;
    }
}
