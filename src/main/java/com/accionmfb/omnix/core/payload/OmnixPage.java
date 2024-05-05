package com.accionmfb.omnix.core.payload;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class OmnixPage<T> {
    private PaginationMeta meta;
    private List<T> records;

    public static <T> OmnixPage<T> from(Page<T> page){
        List<T> content = page.getContent();
        PaginationMeta paginationMeta = new PaginationMeta();
        paginationMeta.setPageNumber(page.getPageable().getPageNumber() + 1);
        paginationMeta.setPageSize(page.getPageable().getPageSize());
        paginationMeta.setPageCount(content.size());
        paginationMeta.setTotalCount(page.getTotalElements());
        paginationMeta.setNumberOfPages(page.getTotalPages());

        OmnixPage<T> omnixPage = new OmnixPage<>();
        return omnixPage.withPaginationMeta(paginationMeta).withRecords(content);
    }

    public OmnixPage<T> withPaginationMeta(PaginationMeta meta){
        setMeta(meta);
        return this;
    }

    public OmnixPage<T> withRecords(List<T> records){
        setRecords(records);
        return this;
    }
}
