package io.renren.modules.generator.utils.oss;

import lombok.Data;

@Data
public class UploadPhotoResponse {
    private String code;
    private String message;
    private long timestamp;
}
