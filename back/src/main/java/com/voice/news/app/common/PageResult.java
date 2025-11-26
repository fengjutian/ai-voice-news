package com.voice.news.app.common;

import java.util.List;

import lombok.Data;

@Data
public class PageResult<T> {

    private long total;
    private List<T> list;

    public static <T> PageResult<T> of(long total, List<T> list) {
        PageResult<T> p = new PageResult<>();
        p.setTotal(total);
        p.setList(list);
        return p;
    }
}

