package uk.gov.dwp.personal.details.server.resource;

import uk.gov.dwp.personal.details.client.PersonalDetailsResponse;
import uk.gov.dwp.personal.details.server.model.PersonalDetails;

public class PersonalDetailsResponseAdapter {

    PersonalDetailsResponse toPersonalDetails(PersonalDetails personalDetails) {
        if (personalDetails == null) {
            return null;
        }
        return PersonalDetailsResponse.newPersonalDetailsResponse()
                .withPersonalDetailsId(personalDetails.getPersonalDetailsId())
                .withName(personalDetails.getFirstName() + " " + personalDetails.getLastName())
                .withDateOfBirth(personalDetails.getDateOfBirth())
                .build();
    }
}
