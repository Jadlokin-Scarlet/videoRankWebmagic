package com.jadlokin.test.entity;

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
public class VideoInfo {
    private Long av;

    private String name;

    private String img;

    private String type;

    private String owner;

    private Boolean copyright;

    private String pubTime;

    private Long startTime;

    private Boolean isDelete;
}