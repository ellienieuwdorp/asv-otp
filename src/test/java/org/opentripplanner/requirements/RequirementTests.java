package org.opentripplanner.requirements;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.opentripplanner.common.model.GenericLocation;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.standalone.config.OTPConfiguration;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class RequirementTests {
    
    @Test
    public void testRequirement1() {
        // Create routing request (Arrange)
        RoutingRequest routingRequest = new RoutingRequest();

        // Create data to enter in to the RoutingRequest
        String fromPlace = "Amersfoort";
        String toPlace = "Weesperplein";

        double fromLat = 5.364759;
        double fromLng = 52.156126;

        double toLat = 4.907845;
        double toLng = 52.361756;

        // Set the routing request according to data (Act)
        routingRequest.from = new GenericLocation(fromPlace, fromPlace + " (" + fromLat + ", " + fromLng + ")");
        routingRequest.to = new GenericLocation(toPlace, toPlace + " (" + toLat + ", " + toLng + ")");

        // Assert that the locations were correctly set
        assertEquals(routingRequest.getFromPlace().name, fromPlace);
        assertEquals((Object) routingRequest.from.lng, fromLng);
        assertEquals((Object) routingRequest.from.lat, fromLat);

        assertEquals(routingRequest.getToPlace().name, toPlace);
        assertEquals((Object) routingRequest.to.lng, toLng);
        assertEquals((Object) routingRequest.to.lat, toLat);
    }

    @Test
    public void testRequirement3() {
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

    @Test
    public void testRequirement8() {
        File config = new File("./src/test/java/org/opentripplanner/requirements");

        OTPConfiguration otpConfiguration = new OTPConfiguration(config);
        JsonNode configuration = otpConfiguration.getGraphConfig(config).routerConfig();
        String urlOne = "http://gtfs.openov.nl/gtfs-rt/tripUpdates.pb";
        String urlTwo = "http://gtfs.openov.nl/gtfs-rt/trainUpdates.pb";

        int iterator = 0;
        for (JsonNode configItem : configuration.path("updaters")) {

            String url = configItem.path("url").asText();
            if (url != null) {
                if (iterator == 0) assertEquals(url, urlOne);
                if (iterator == 1) assertEquals(url, urlTwo);
            }
            iterator++;
        }
    }
}
