package io.renren.modules.generator.utils.oss;

import lombok.Data;

import java.util.Date;

@Data
public class ActivityData {
    private Date created;
    private int id;
    private String name;
}
