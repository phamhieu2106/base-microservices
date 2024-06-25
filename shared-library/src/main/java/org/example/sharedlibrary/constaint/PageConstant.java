package org.example.sharedlibrary.constaint;


import org.springframework.data.domain.Sort;

public class PageConstant {

    public static final int PAGE_NUMBER = 0;
    public static final int PAGE_SIZE = 10;
    public static final String PAGE_SORT_TYPE = "ASC";
    public static final String PAGE_DEFAULT_VALUE = "";
    public static final String PAGE_DEFAULT_SORT_BYS = "createdAt";

    private PageConstant() {
        super();
    }

    public static Sort getSortBy(String[] sortBys, String sortOrder) {
        Sort sort;
        if ("ASC".equals(sortOrder)) {
            sort = Sort.by(Sort.Direction.ASC, sortBys);
        } else {
            sort = Sort.by(Sort.Direction.DESC, sortBys);
        }
        return sort;
    }

}
