package org.opentripplanner.requirements;

import org.junit.Test;
import org.opentripplanner.common.model.GenericLocation;
import org.opentripplanner.routing.core.RoutingRequest;

import static org.junit.Assert.assertEquals;


public class requirement1 {

    @Test
    public void test() {
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
}
