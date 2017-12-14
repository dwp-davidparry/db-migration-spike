package uk.gov.dwp.example.personal.details.client.update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.example.personal.details.client.RandomPersonalDetailsGenerator;
import uk.gov.dwp.personal.details.client.v2.PersonalDetailsV2Client;
import uk.gov.dwp.personal.details.type.PersonalDetailsId;

import java.util.Optional;
import java.util.function.Supplier;

import static uk.gov.dwp.personal.details.client.UpdatePersonalDetailsRequest.newUpdatePersonalDetailsRequest;

public class UpdatePersonalDetailsTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdatePersonalDetailsTask.class);

    private final PersonalDetailsV2Client personalDetailsClient;
    private final Supplier<Optional<PersonalDetailsId>> personalDetailsIdSupplier;
    private final RandomPersonalDetailsGenerator personalDetailsGenerator;

    public UpdatePersonalDetailsTask(PersonalDetailsV2Client personalDetailsClient,
                                     Supplier<Optional<PersonalDetailsId>> personalDetailsIdSupplier,
                                     RandomPersonalDetailsGenerator personalDetailsGenerator) {
        this.personalDetailsClient = personalDetailsClient;
        this.personalDetailsIdSupplier = personalDetailsIdSupplier;
        this.personalDetailsGenerator = personalDetailsGenerator;
    }

    @Override
    public void run() {
        try {
            personalDetailsIdSupplier.get().ifPresent(personalDetailsId -> {
                personalDetailsClient.update(
                        newUpdatePersonalDetailsRequest()
                                .withPersonalDetailsId(personalDetailsId)
                                .withFirstName(personalDetailsGenerator.randomFirstName())
                                .withLastName(personalDetailsGenerator.randomLastName())
                                .withDateOfBirth(personalDetailsGenerator.randomDateOfBirth())
                                .build()
                );
                LOGGER.debug("Updated personalDetails with id: {}", personalDetailsId);
            });
        } catch (Exception e) {
            LOGGER.error("Could not update PersonalDetails", e);
        }
    }
}
