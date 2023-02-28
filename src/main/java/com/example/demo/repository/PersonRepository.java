package com.example.demo.repository;


import com.example.demo.data.PersonData;
import com.example.demo.data.TopTenNameData;
import com.example.demo.dto.PersonQueryDto;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
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
        MatchOperation filter = match(Criteria.where("is_pep").is(true));
        GroupOperation groupByFirstName = group("first_name")
                .count().as("count");
        SortOperation sortByCountDesc = sort(Sort.Direction.DESC, "count");
        LimitOperation limitToOnlyFirstDoc = limit(10);
        ProjectionOperation projectToMatchModel = project("count").and("first_name").previousOperation();

        Aggregation aggregation = newAggregation(
                filter, groupByFirstName, sortByCountDesc, limitToOnlyFirstDoc, projectToMatchModel);

        return mongoTemplate.aggregate(
                aggregation, collection, TopTenNameData.class).getMappedResults();
    }
}
