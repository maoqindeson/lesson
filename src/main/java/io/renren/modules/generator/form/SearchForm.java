package io.renren.modules.generator.form;

import lombok.Data;

import java.util.List;


@Data
public class SearchForm {
    private String keyword;
    private String type;
    private Integer pageSize;
    private Integer pageIndex;
    private Integer offset;
    private List<String> search_keys;
}