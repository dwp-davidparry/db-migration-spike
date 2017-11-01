package uk.gov.dwp.migration.kafka.api;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "@type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MongoDeleteMessage.class),
        @JsonSubTypes.Type(value = MongoInsertMessage.class),
        @JsonSubTypes.Type(value = MongoUpdateMessage.class),
})
public interface MongoOperation {

}
