package com.kh.shoppingmall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class BannerDto {
    private int bannerNo;
    private String bannerTitle;
    private String bannerLink;
    private int bannerOrder;
    private Integer bannerAttachmentNo;
}
