package org.opentripplanner.requirements;

import org.junit.Test;
import org.opentripplanner.routing.core.RoutingRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class requirement3 {

    @Test
    public void test() {
        // Create routing requests
        RoutingRequest routingRequestOne = new RoutingRequest();
        RoutingRequest routingRequestTwo = new RoutingRequest();

        // Create date objects to test
        Date customDate = null;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy HH:mm:ss");
        String dateInString = "19-06-2019 15:30:33";
        try {
            customDate = sdf.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String secondDate = "6.24.19";
        String secondDateTime = "14:00";
        TimeZone secondDateTimeZone = TimeZone.getTimeZone("GMT+2");

        long customDateUnix = 1560951033000L;
        long secondDateUnix = 1561377600000L;

        // Set the dates
        routingRequestOne.setDateTime(customDate);
        routingRequestTwo.setDateTime(secondDate, secondDateTime, secondDateTimeZone);

        // Assert that the dates were correctly set
        assertEquals(routingRequestOne.getDateTime().getTime(), customDateUnix);
        assertEquals(routingRequestTwo.getDateTime().getTime(), secondDateUnix);
    }
}
