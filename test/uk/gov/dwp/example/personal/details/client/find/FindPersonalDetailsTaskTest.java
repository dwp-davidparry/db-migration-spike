package uk.gov.dwp.example.personal.details.client.find;

import org.junit.Test;
import uk.gov.dwp.personal.details.client.PersonalDetails;
import uk.gov.dwp.personal.details.client.PersonalDetailsClient;
import uk.gov.dwp.personal.details.type.PersonalDetailsId;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class FindPersonalDetailsTaskTest {

    private static final PersonalDetailsId PERSONAL_DETAILS_ID = PersonalDetailsId.newPersonalDetailsId();
    private final PersonalDetailsClient personalDetailsClient = mock(PersonalDetailsClient.class);
    private final PersonalDetails personalDetails = mock(PersonalDetails.class);

    @Test
    public void findWhenPersonalDetailsWhenListIsEmpty() {
        new FindPersonalDetailsTask(personalDetailsClient, Optional::empty).run();

        verifyZeroInteractions(personalDetailsClient);
    }

    @Test
    public void findPersonalDetailsWhenListIsNotEmpty() {
        when(personalDetailsClient.findById(PERSONAL_DETAILS_ID)).thenReturn(personalDetails);

        new FindPersonalDetailsTask(personalDetailsClient, () -> Optional.of(PERSONAL_DETAILS_ID)).run();

        verify(personalDetailsClient).findById(PERSONAL_DETAILS_ID);
    }
}
