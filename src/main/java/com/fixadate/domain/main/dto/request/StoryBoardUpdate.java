package com.fixadate.domain.main.dto.request;

import org.hibernate.validator.constraints.Length;

public record StoryBoardUpdate(
    @Length(min = 0, max = 30)
    String storyBoard) {

}
