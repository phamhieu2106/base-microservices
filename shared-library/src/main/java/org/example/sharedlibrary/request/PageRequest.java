package org.example.sharedlibrary.request;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.sharedlibrary.constaint.PageConstant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageRequest {
    int pageNumber = PageConstant.PAGE_NUMBER;
    int pageSize = PageConstant.PAGE_SIZE;
    String sortOrder = PageConstant.PAGE_SORT_TYPE;
    String keyword = PageConstant.PAGE_DEFAULT_VALUE;
    String[] sortBys = {PageConstant.PAGE_DEFAULT_SORT_BYS};
}