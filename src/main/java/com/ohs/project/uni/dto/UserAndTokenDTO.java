package com.ohs.project.uni.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAndTokenDTO {
    private String id;
    private String email;
    private String fullName;
    private String token;
}