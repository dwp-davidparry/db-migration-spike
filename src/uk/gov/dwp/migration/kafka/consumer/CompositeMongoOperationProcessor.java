package uk.gov.dwp.migration.kafka.consumer;

import uk.gov.dwp.migration.kafka.api.MongoOperation;

import java.util.Map;

public class CompositeMongoOperationProcessor implements MongoOperationProcessor {

    private final Map<Class<? extends MongoOperation>, MongoOperationProcessor> processors;

    public CompositeMongoOperationProcessor(Map<Class<? extends MongoOperation>, MongoOperationProcessor> processors) {
        this.processors = processors;
    }

    @Override
    public void process(MongoOperation mongoOperation) {
        processors.get(mongoOperation.getClass()).process(mongoOperation);
    }
}
