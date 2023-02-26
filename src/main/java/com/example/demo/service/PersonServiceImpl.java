package com.example.demo.service;

import com.example.demo.data.PersonData;
import com.example.demo.data.TopTenNameData;
import com.example.demo.dto.PersonDetailsDto;
import com.example.demo.dto.PersonQueryDto;
import com.example.demo.dto.TopTenNameDto;
import com.example.demo.exception.FileException;
import com.example.demo.exception.IllegalFileException;
import com.example.demo.repository.PersonRepository;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    private static final File destDir = new File("src/main/resources/unzipTest");

    @Override
    public void upload(MultipartFile jsonZip) {
        destDir.mkdir();
        unzipFile(jsonZip);
        personRepository.clear();

        List<Document> listJsonObjects = parseJsonFile();

        personRepository.insert(listJsonObjects);
    }

    private List<Document> parseJsonFile() {
        List<Document> parsJson = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonFactory jsonFactory = objectMapper.getFactory();

        try (JsonParser parser = jsonFactory.createParser(new File(destDir + "/pep.json"))) {
            if (parser.nextToken() == JsonToken.START_ARRAY) {
                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    Document document = parser.readValueAs(Document.class);
                    parsJson.add(document);
                }
            } else {
                throw new FileException("Expected array og objects");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return parsJson;
    }


    @Override
    public List<PersonDetailsDto> searchByName(PersonQueryDto query) {
        if (query.getFirst_name() == null && query.getLast_name() == null && query.getPatronymic() == null) {
            return null;
        }
        return personRepository.searchByName(query).stream()
                .map(this::convertToDetails)
                .toList();
    }

    @Override
    public List<TopTenNameDto> getTopTenName() {

        return personRepository.getTopTenName().stream()
                .map(this::convertToDetails)
                .toList();
    }


    private void unzipFile(MultipartFile jsonZip) {
        if (!Objects.requireNonNull(jsonZip.getOriginalFilename()).endsWith(".zip")) {
            throw new IllegalFileException("Must be zip not " + jsonZip.getOriginalFilename());
        }

        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(jsonZip.getInputStream())) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                if (!zipEntry.getName().endsWith(".json")) {
                    throw new IllegalFileException("Must be json file not  " + zipEntry.getName());

                }

                File destFile = new File(destDir, "pep.json");

                try (FileOutputStream fos = new FileOutputStream(destFile)) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
            }
            zis.closeEntry();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private PersonDetailsDto convertToDetails(PersonData data) {
        return PersonDetailsDto.builder()
                .full_name(data.getFull_name())
                .full_name_en(data.getFull_name_en())
                .died(data.getDied())
                .is_pep(data.getIs_pep())
                .build();
    }

    private TopTenNameDto convertToDetails(TopTenNameData data) {
        return TopTenNameDto.builder()
                .first_name(data.getFirst_name())
                .count(data.getCount())
                .build();
    }

}
