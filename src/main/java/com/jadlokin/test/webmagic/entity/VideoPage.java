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
public class VideoPage {
    private Long cid;

    private Long av;

    private String part;

    private Integer page;

    private String from;

    private Long duration;

    private String vid;

    private String weblink;
}