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
public class Right {
    private Long av;

    private Short bp;

    private Short elec;

    private Short download;

    private Short movie;

    private Short pay;

    private Short hd5;

    private Short noReprint;

    private Short autoplay;

    private Short ugcPay;

    private Short isCooperation;

    private Short ugcPayPreview;

    private Short noBackground;
}