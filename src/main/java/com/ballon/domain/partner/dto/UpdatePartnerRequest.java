package com.ballon.domain.partner.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UpdatePartnerRequest {
    String email;
    String name;
    String overview;
    List<Long> categoryIds;
}
