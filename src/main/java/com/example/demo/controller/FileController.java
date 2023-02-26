package com.example.demo.controller;

import com.example.demo.dto.PersonDetailsDto;
import com.example.demo.dto.PersonQueryDto;
import com.example.demo.dto.TopTenNameDto;
import com.example.demo.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final PersonService personService;


    @PostMapping("/upload")
    public ResponseEntity<String> createBook(@RequestPart("file") MultipartFile jsonZip) {
        personService.upload(jsonZip);
        return ResponseEntity.status(HttpStatus.OK).body("The file is uploaded");
    }

    @PostMapping("_search")
    public ResponseEntity<List<PersonDetailsDto>> search(@RequestBody PersonQueryDto query) {
        List<PersonDetailsDto> persons = personService.searchByName(query);
        return ResponseEntity.status(HttpStatus.OK).body(persons);
    }

    @GetMapping("topTen")
    public ResponseEntity<List<TopTenNameDto>> topTen() {
        List<TopTenNameDto> topTen = personService.getTopTenName();
        return ResponseEntity.status(HttpStatus.OK).body(topTen);
    }


}
