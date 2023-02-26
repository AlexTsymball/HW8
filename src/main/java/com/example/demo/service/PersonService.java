package com.example.demo.service;

import com.example.demo.dto.PersonDetailsDto;
import com.example.demo.dto.PersonQueryDto;
import com.example.demo.dto.TopTenNameDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PersonService {

    void upload(MultipartFile jsonZip);

    List<PersonDetailsDto> searchByName(PersonQueryDto query);

    List<TopTenNameDto> getTopTenName();

}


