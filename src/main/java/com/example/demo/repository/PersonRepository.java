package com.example.demo.repository;


import com.example.demo.data.PersonData;
import com.example.demo.data.TopTenNameData;
import com.example.demo.dto.PersonQueryDto;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
@RequiredArgsConstructor
public class PersonRepository {

    private final MongoTemplate mongoTemplate;

    private static final String collection = "public_figures";


    public void insert(List<Document> listJsonObjects) {
       mongoTemplate.insert(listJsonObjects, collection);
    }


    public void clear() {
        mongoTemplate.remove(new Query(), collection);
    }

    public List<PersonData> searchByName(PersonQueryDto query) {
        Query mongoQuery = new Query();

        if (StringUtils.isNotBlank(query.getFirst_name())) {
            mongoQuery.addCriteria(where("first_name").is(query.getFirst_name()));
        }
        if (StringUtils.isNotBlank(query.getLast_name())) {
            mongoQuery.addCriteria(where("last_name").is(query.getLast_name()));
        }
        if (StringUtils.isNotBlank(query.getPatronymic())) {
            mongoQuery.addCriteria(where("patronymic").is(query.getPatronymic()));
        }

        return mongoTemplate.find(mongoQuery, PersonData.class);
    }

    public List<TopTenNameData> getTopTenName() {
        GroupOperation groupByFirstName = group("first_name")
                .count().as("count");
        SortOperation sortByCountDesc = sort(Sort.Direction.DESC, "count");
        LimitOperation limitToOnlyFirstDoc = limit(10);
        ProjectionOperation projectToMatchModel = project("count").and("first_name").previousOperation();

        Aggregation aggregation = newAggregation(
                groupByFirstName, sortByCountDesc, limitToOnlyFirstDoc, projectToMatchModel);

        return mongoTemplate.aggregate(
                aggregation, collection, TopTenNameData.class).getMappedResults();
    }
}
