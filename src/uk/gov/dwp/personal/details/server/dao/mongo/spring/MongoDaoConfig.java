package uk.gov.dwp.personal.details.server.dao.mongo.spring;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.bson.BSON;
import org.bson.Transformer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.api.PersonalDetails;
import uk.gov.dwp.api.PersonalDetailsId;
import uk.gov.dwp.common.id.Id;
import uk.gov.dwp.common.jackson.spring.JacksonConfiguration;
import uk.gov.dwp.personal.details.server.dao.PersonalDetailsDao;
import uk.gov.dwp.personal.details.server.dao.mongo.MongoPersonalDetailsDao;
import uk.gov.dwp.personal.details.server.dao.mongo.PersonalDetailsDocumentConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static com.mongodb.MongoCredential.createScramSha1Credential;
import static java.time.ZoneOffset.UTC;
import static java.util.Collections.singletonList;

@Configuration
@Import(JacksonConfiguration.class)
@EnableConfigurationProperties(MongoDaoProperties.class)
public class MongoDaoConfig {

    static {
        TimeZone.setDefault(TimeZone.getTimeZone(UTC));
        BSON.addDecodingHook(LocalDateTime.class, new LocalDateTimeTransformer());
        BSON.addEncodingHook(LocalDateTime.class, new LocalDateTimeTransformer());
        BSON.addDecodingHook(Instant.class, new InstantTransformer());
        BSON.addEncodingHook(Instant.class, new InstantTransformer());
        BSON.addEncodingHook(Id.class, new IdTransformer());
        BSON.addDecodingHook(Date.class, new InstantTransformer());
    }

    @Bean
    @DependsOn("mongoDaoProperties")
    public MongoClient mongoClient(MongoDaoProperties mongoDaoProperties) {
        return new MongoClient(
                createSeeds(mongoDaoProperties),
                createCredentials(mongoDaoProperties),
                mongoDaoProperties.mongoClientOptions()
        );
    }

    @Bean
    @DependsOn("mongoDaoProperties")
    public PersonalDetailsDao personalDetailsDao(MongoClient mongoClient,
                                                 MongoDaoProperties mongoDaoProperties,
                                                 PersonalDetailsDocumentConverter personalDetailsDocumentConverter) {
        return new MongoPersonalDetailsDao(
                mongoClient
                        .getDatabase(mongoDaoProperties.getDbName())
                        .getCollection(mongoDaoProperties.getPersonalDetails().getName()),
                personalDetailsDocumentConverter
        );
    }

    @Bean
    public PersonalDetailsDocumentConverter personalDetailsDocumentConverter() {
        return new PersonalDetailsDocumentConverter();
    }

    private List<MongoCredential> createCredentials(MongoDaoProperties mongoDaoProperties) {
        return mongoDaoProperties.getPersonalDetails().getUsername()
                .map(username -> singletonList(createScramSha1Credential(
                        username,
                        mongoDaoProperties.getDbName(),
                        mongoDaoProperties.getPersonalDetails()
                                .getPassword()
                                .orElseThrow(() -> new IllegalArgumentException("Password is required when username specified"))
                                .toCharArray())))
                .orElse(Collections.emptyList());
    }

    private List<ServerAddress> createSeeds(MongoDaoProperties mongoDaoProperties) {
        return mongoDaoProperties.getServerAddresses()
                .stream()
                .map(serverAddress -> new ServerAddress(serverAddress.getHost(), serverAddress.getPort()))
                .collect(Collectors.toList());
    }

    public static class LocalDateTimeTransformer implements Transformer {

        @Override
        public Object transform(Object objectToTransform) {
            if (objectToTransform instanceof LocalDateTime) {
                return Date.from(((LocalDateTime) objectToTransform).toInstant(UTC));
            } else if (objectToTransform instanceof Date) {
                return LocalDateTime.ofInstant(((Date) objectToTransform).toInstant(), UTC);
            }
            throw new IllegalArgumentException("LocalDateTimeTransformer can only be used with LocalDateTime or Date");
        }
    }

    public static class InstantTransformer implements Transformer {

        @Override
        public Object transform(Object objectToTransform) {
            if (objectToTransform instanceof Instant) {
                return Date.from(((Instant) objectToTransform));
            } else if (objectToTransform instanceof Date) {
                return ((Date) objectToTransform).toInstant();
            }
            throw new IllegalArgumentException("InstantTransformer can only be used with Instant or Date");
        }
    }

    public static class IdTransformer implements Transformer {

        @Override
        public Object transform(Object objectToTransform) {
            if (objectToTransform instanceof Id) {
                return ((Id) objectToTransform).getId().toString();
            }
            throw new IllegalArgumentException("IdTransformer can only be used with instances of Id");
        }
    }
}
