package com.jadlokin.test.webmagic.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain=true)
public class VideoData {
    private Long av;

    private Short issue;

    private Long view;

    private Long reply;

    private Long favorite;

    private Long coin;

    private Long page;

    private Long point;

    private Long rank;

    private Long danmaku;

    private Long share;

    private Long like;

    private Long dislike;

    private String evaluation;
}