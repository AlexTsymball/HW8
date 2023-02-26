package com.example.demo.data;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document("public_figures")
public class PersonData {
    private String full_name;
    private String full_name_en;
    private Boolean is_pep;
    private Boolean died;
}
