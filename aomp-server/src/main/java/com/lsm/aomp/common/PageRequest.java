package com.lsm.aomp.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageRequest implements Serializable {

    private Integer pageNum = 1;
    private Integer pageSize = 20;
    private String sortField;
    private String sortOrder = "asc";

    public long getOffset() {
        return (long) (pageNum - 1) * pageSize;
    }
}
