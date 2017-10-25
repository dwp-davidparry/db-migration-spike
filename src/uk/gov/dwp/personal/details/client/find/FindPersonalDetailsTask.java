package uk.gov.dwp.personal.details.client.find;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.personal.details.api.PersonalDetails;
import uk.gov.dwp.personal.details.api.PersonalDetailsClient;
import uk.gov.dwp.personal.details.api.PersonalDetailsId;

import java.util.Optional;
import java.util.function.Supplier;

public class FindPersonalDetailsTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindPersonalDetailsTask.class);

    private final PersonalDetailsClient personalDetailsClient;
    private final Supplier<Optional<PersonalDetailsId>> personalDetailsIdSupplier;

    public FindPersonalDetailsTask(PersonalDetailsClient personalDetailsClient,
                                   Supplier<Optional<PersonalDetailsId>> personalDetailsIdSupplier) {
        this.personalDetailsClient = personalDetailsClient;
        this.personalDetailsIdSupplier = personalDetailsIdSupplier;
    }

    @Override
    public void run() {
        try {
            personalDetailsIdSupplier.get().ifPresent(personalDetailsId -> {
                PersonalDetails personalDetails = personalDetailsClient.findById(personalDetailsId);
                LOGGER.debug("Found PersonalDetails: {}", personalDetails.getPersonalDetailsId());
            });
        } catch (Exception e) {
            LOGGER.error("Could not create PersonalDetails", e);
        }
    }

}
