package com.accionmfb.omnix.core.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationMeta {
    private long pageNumber;   // The page size used in the query. A replica of what the client passed.
    private long pageSize; // The page number used in the query. A replica of what the client passed.
    private long pageCount; // The number of records currently in the page served.
    private long totalCount; // The total item retrieved based on the query even before pagination process
    private long numberOfPages; // The total number of the pages
}
