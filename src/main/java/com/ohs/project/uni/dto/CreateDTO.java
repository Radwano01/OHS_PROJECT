package com.ohs.project.uni.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateDTO {
    private String email;
    private String fullName;
    private String password;
}
