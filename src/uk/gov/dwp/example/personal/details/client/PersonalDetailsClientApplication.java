package uk.gov.dwp.example.personal.details.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import uk.gov.dwp.common.id.Id;
import uk.gov.dwp.common.jackson.ISO8601DateFormatWithMilliSeconds;
import uk.gov.dwp.common.jackson.IdDeserializer;
import uk.gov.dwp.common.jackson.IdSerializer;
import uk.gov.dwp.example.personal.details.client.create.CreatePersonalDetailsService;
import uk.gov.dwp.example.personal.details.client.create.CreatePersonalDetailsTask;
import uk.gov.dwp.example.personal.details.client.find.FindPersonalDetailsService;
import uk.gov.dwp.example.personal.details.client.find.FindPersonalDetailsTask;
import uk.gov.dwp.example.personal.details.client.update.UpdatePersonalDetailsService;
import uk.gov.dwp.example.personal.details.client.update.UpdatePersonalDetailsTask;
import uk.gov.dwp.personal.details.client.PersonalDetailsClient;
import uk.gov.dwp.personal.details.type.PersonalDetailsId;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static org.apache.commons.lang3.RandomUtils.nextInt;

public class PersonalDetailsClientApplication {

    private final CreatePersonalDetailsService createPersonalDetailsService;
    private final FindPersonalDetailsService findPersonalDetailsService;
    private final UpdatePersonalDetailsService updatePersonalDetailsService;

    public PersonalDetailsClientApplication(CreatePersonalDetailsService createPersonalDetailsService,
                                            FindPersonalDetailsService findPersonalDetailsService,
                                            UpdatePersonalDetailsService updatePersonalDetailsService) {
        this.createPersonalDetailsService = createPersonalDetailsService;
        this.findPersonalDetailsService = findPersonalDetailsService;
        this.updatePersonalDetailsService = updatePersonalDetailsService;
    }

    public void start() {
        createPersonalDetailsService.start();
        findPersonalDetailsService.start();
        updatePersonalDetailsService.start();
    }

    public static void main(String[] args) {
        String url = args[0];

        PersonalDetailsClient personalDetailsClient = JAXRSClientFactory.create(
                url,
                PersonalDetailsClient.class,
                singletonList(new JacksonJsonProvider(objectMapper()))
        );
        ArrayList<PersonalDetailsId> personalDetailsIdRegistry = new ArrayList<>();

        Supplier<Optional<PersonalDetailsId>> personalDetailsIdGenerator = () -> personalDetailsIdRegistry.isEmpty() ? Optional.empty() : Optional.of(personalDetailsIdRegistry.get(nextInt(0, personalDetailsIdRegistry.size() - 1)));
        PersonalDetailsClientApplication personalDetailsClientApplication = new PersonalDetailsClientApplication(
                createPersonalDetailsService(personalDetailsClient, personalDetailsIdRegistry),
                findPersonalDetailsService(personalDetailsClient, personalDetailsIdGenerator),
                updatePersonalDetailsService(personalDetailsClient, personalDetailsIdGenerator)
        );
        personalDetailsClientApplication.start();
    }

    private static CreatePersonalDetailsService createPersonalDetailsService(PersonalDetailsClient personalDetailsClient,
                                                                            ArrayList<PersonalDetailsId> personalDetailsIdRegistry) {
        return new CreatePersonalDetailsService(
                newSingleThreadScheduledExecutor(),
                new CreatePersonalDetailsTask(personalDetailsClient, personalDetailsIdRegistry),
                Duration.ofSeconds(2L)
        );
    }

    private static FindPersonalDetailsService findPersonalDetailsService(PersonalDetailsClient personalDetailsClient,
                                                                         Supplier<Optional<PersonalDetailsId>> personalDetailsIdGenerator) {
        return new FindPersonalDetailsService(
                newSingleThreadScheduledExecutor(),
                new FindPersonalDetailsTask(
                        personalDetailsClient,
                        personalDetailsIdGenerator),
                Duration.ofSeconds(1L)
        );
    }

    private static UpdatePersonalDetailsService updatePersonalDetailsService(PersonalDetailsClient personalDetailsClient,
                                                                             Supplier<Optional<PersonalDetailsId>> personalDetailsIdGenerator) {
        return new UpdatePersonalDetailsService(
                newSingleThreadScheduledExecutor(),
                new UpdatePersonalDetailsTask(
                        personalDetailsClient,
                        personalDetailsIdGenerator,
                        new RandomPersonalDetailsGenerator()
                ),
                Duration.ofSeconds(3L)
        );
    }


    private static ObjectMapper objectMapper() {
            return new ObjectMapper()
                    .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                    .setDateFormat(new ISO8601DateFormatWithMilliSeconds())
                    .registerModule(new Jdk8Module())
                    .registerModule(new JavaTimeModule())
                    .registerModule(new SimpleModule()
                            .addSerializer(Id.class, new IdSerializer())
                            .addDeserializer(Id.class, new IdDeserializer()))
                    ;
    }
}
