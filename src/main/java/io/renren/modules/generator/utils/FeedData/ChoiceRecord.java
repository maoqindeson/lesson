package io.renren.modules.generator.utils.FeedData;

import lombok.Data;

import java.util.List;
@Data
public class ChoiceRecord {
    private List<Choice> choices;
    private String choiceResult;
//    private String correctResult;
}
