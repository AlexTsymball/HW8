package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonDetailsDto {
    private String full_name;
    private String full_name_en;
    private Boolean is_pep;
    private Boolean died;
}
