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

        this.maxResult = maxResult;
        this.currentPage = Math.max(page, 1);

        // 1. Đếm số bản ghi
        this.totalRecords = countQuery.getSingleResult().intValue();

        // 2. Tính tổng số trang
        this.totalPages = (int) Math.ceil((double) totalRecords / maxResult);

        // 3. Lấy dữ liệu phân trang
        this.list = query.setFirstResult((currentPage - 1) * maxResult).setMaxResults(maxResult).getResultList();

        // 4. Tính navigation
        this.maxNavigationPage = Math.min(maxNavigationPage, totalPages);
        calcNavigationPages();
    }

    private void calcNavigationPages() {
        navigationPages = buildNavigationPages(currentPage, totalPages, maxNavigationPage);
    }

    public static List<Integer> buildNavigationPages(int currentPage, int totalPages, int maxNavigationPage) {
        List<Integer> pages = new ArrayList<>();
        int current = Math.min(currentPage, totalPages);
        int begin = current - maxNavigationPage / 2;
        int end = current + maxNavigationPage / 2;
        pages.add(1);
        if (begin > 2) pages.add(-1);
        for (int page = begin; page <= end; page++) {
            if (page > 1 && page < totalPages) {
                pages.add(page);
            }
        }
        if (end < totalPages - 2) pages.add(-1);
        if (totalPages > 1) pages.add(totalPages);
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
