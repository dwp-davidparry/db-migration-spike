package uk.gov.dwp.migration.kafka.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Map;

@JsonTypeName("MONGO_UPDATE")
public class MongoUpdateMessage implements MongoOperation {

    private final String db;
    private final String collection;
    private final Map dbObject;

    public MongoUpdateMessage(@JsonProperty("db") String db,
                              @JsonProperty("collection") String collection,
                              @JsonProperty("dbObject") Map dbObject) {
        this.db = db;
        this.collection = collection;
        this.dbObject = dbObject;
    }

    public String getDb() {
        return db;
    }

    public String getCollection() {
        return collection;
    }

    public Map getDbObject() {
        return dbObject;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
