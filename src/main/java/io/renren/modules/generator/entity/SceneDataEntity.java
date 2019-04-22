package io.renren.modules.generator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tb_scene_data")
public class SceneDataEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String content;
    private String type;
    private String status;
    private String username;
}
