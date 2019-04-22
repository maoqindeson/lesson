package io.renren.modules.generator.utils.oss;

import lombok.Data;

import java.util.List;

@Data
public class ActivityPhotoResponse {
    private int code;
    private List<ActivityPhotoData> data;
//    private int total;
}
