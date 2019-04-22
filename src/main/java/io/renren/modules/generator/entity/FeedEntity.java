package io.renren.modules.generator.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2018-11-27 09:51:20
 */
@Data
@TableName("tb_feed")
public class FeedEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private int orgId;
    private int nextOrg;
    private int parentId;
    //feed 的 type有这几种:
    //普通文本:text
    //图片:picture
    //视频:video
    //录音:record
    //音乐:audio
    //判断题:tf-question
    //单项选择题:sin-choice
    //多项选择题:mul-choice
    //填空题:full-question
    private String type;
    private String loadType;
    private String content;
    private String choices;
    private String correctResult;
    private String poster;
    private String name;
    private String author;
    private String src;
    private String text;
    private String answer;
    private String pointTo;
    private Integer intervalTime;
    @TableField(exist = false)
    private String feedResult;
    private int hasEnd;
    private int hasStart;
    private int hasExercise;
    private int exerciseId;
    @TableField(exist = false)
    private Object data;
}

