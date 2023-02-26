package com.example.demo.data;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("public_figures")
public class TopTenNameData {
        private String first_name;
        private Integer count;

}
