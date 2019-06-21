package org.opentripplanner.requirements;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.opentripplanner.api.common.Message;
import org.opentripplanner.api.model.Itinerary;
import org.opentripplanner.api.model.TripPlan;
import org.opentripplanner.api.resource.GraphPathToTripPlanConverter;
import org.opentripplanner.common.model.GenericLocation;
import org.opentripplanner.graph_builder.model.GtfsBundle;
import org.opentripplanner.graph_builder.module.GtfsFeedId;
import org.opentripplanner.graph_builder.module.GtfsModule;
import org.opentripplanner.model.FeedScopedId;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.TraverseMode;
import org.opentripplanner.routing.core.TraverseModeSet;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.impl.AlertPatchServiceImpl;
import org.opentripplanner.routing.impl.DefaultStreetVertexIndexFactory;
import org.opentripplanner.routing.impl.GraphPathFinder;
import org.opentripplanner.routing.spt.GraphPath;
import org.opentripplanner.standalone.Router;
import org.opentripplanner.standalone.config.OTPConfiguration;
import org.opentripplanner.updater.alerts.AlertsUpdateHandler;
import org.opentripplanner.updater.stoptime.TimetableSnapshotSource;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

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
    public void testRequirement2() {
        // Create routing requests (Arrange)
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

        long customDateUnix = customDate.getTime();
        long secondDateUnix = 1561377600000L;

        // Set the dates (Act)
        routingRequestOne.setDateTime(customDate);
        routingRequestTwo.setDateTime(secondDate, secondDateTime, secondDateTimeZone);

        // Assert that the dates were correctly set (Assert)
        assertEquals(routingRequestOne.getDateTime().getTime(), customDateUnix);
        assertEquals(routingRequestTwo.getDateTime().getTime(), secondDateUnix);
    }

    @Test
    public void testRequirement3() {
        // Load config and create test vars (Arrange)
        File config = new File("./src/test/java/org/opentripplanner/requirements");

        OTPConfiguration otpConfiguration = new OTPConfiguration(config);
        JsonNode configuration = otpConfiguration.getGraphConfig(config).routerConfig();
        String urlOne = "http://gtfs.openov.nl/gtfs-rt/tripUpdates.pb";
        String urlTwo = "http://gtfs.openov.nl/gtfs-rt/trainUpdates.pb";

        // Get every item from the loaded JSON (Act)
        int iterator = 0;
        for (JsonNode configItem : configuration.path("updaters")) {

            String url = configItem.path("url").asText();
            if (url != null) {
                // Check if config values are correct (Assert)
                if (iterator == 0) assertEquals(url, urlOne);
                if (iterator == 1) assertEquals(url, urlTwo);
            }
            iterator++;
        }
    }

    @Test
    public void testRequirement4() {
        // Create constants to plan with & mock graph and router. These values we're constructed with a mock GTFS
        // implementation in src/test/resources.
        // The from and to vertices are from the FirstForbiddenTripToTripTransferTest.java test.
        // (Arrange)
        final long dateTime = +1388530860L;
        final String fromVertex = "2e31";
        final String toVertex = "2e36";
        final int legCount = 2;
        final String feedName = "mmri/2e3";

        // Mock graph from example feed
        GtfsFeedId feedId = new GtfsFeedId.Builder().id("MMRI").build();
        Graph graph = new Graph();
        GtfsBundle gtfsBundle = new GtfsBundle(new File("src/test/resources/" + feedName));
        gtfsBundle.setFeedId(feedId);
        List<GtfsBundle> gtfsBundleList = Collections.singletonList(gtfsBundle);
        GtfsModule gtfsGraphBuilderImpl = new GtfsModule(gtfsBundleList);
        gtfsGraphBuilderImpl.buildGraph(graph, null);
        graph.index(new DefaultStreetVertexIndexFactory());
        TimetableSnapshotSource timetableSnapshotSource = new TimetableSnapshotSource(graph);
        timetableSnapshotSource.purgeExpiredData = (false);
        graph.timetableSnapshotSource = (timetableSnapshotSource);

        // Create router test object
        Router router = new Router("TEST", graph);
        RoutingRequest routingRequest = new RoutingRequest();

        // Specify arrive by or depart at parameter
        routingRequest.setArriveBy(false);

        // Specify arrival datetime parameter
        routingRequest.dateTime = Math.abs(dateTime);

        // Specify departure place parameter
        if (fromVertex != null && !fromVertex.isEmpty()) {
            routingRequest.from = (new GenericLocation(null, feedId.getId() + ":" + fromVertex));
        }

        // Specify arrival place parameter
        if (toVertex != null && !toVertex.isEmpty()) {
            routingRequest.to = new GenericLocation(null, feedId.getId() + ":" + toVertex);
        }

        // Set transport modes
        routingRequest.setModes(new TraverseModeSet(TraverseMode.WALK, TraverseMode.TRANSIT));

        // Execute the routing request on the graph  and plan the trip. (Act)
        List<GraphPath> paths = new GraphPathFinder(router).getPaths(routingRequest);
        TripPlan tripPlan = GraphPathToTripPlanConverter.generatePlan(paths, routingRequest);

        // Make sure that the returned trip has the correct mode (rail or walk)
        // and the amount of legs is equal to the number specified in legCount (Assert)
        Itinerary itinerary = tripPlan.itinerary.get(0);

        assertTrue(itinerary.legs.get(0).mode.equals(TraverseMode.RAIL.toString()) ||
                            itinerary.legs.get(0).mode.equals(TraverseMode.WALK.toString()));
        assertEquals(legCount, itinerary.legs.size());
    }

    @Test
    public void testRequirement5() {
        // Get messages in different locales to check (Arrange, Act)
        String englishPlanOk = Message.SYSTEM_ERROR.get();
        String germanPlanOk = Message.SYSTEM_ERROR.get(Locale.GERMAN);
        String frenchPlanOk = Message.SYSTEM_ERROR.get(new Locale("fr"));

        String englishBogus = Message.BOGUS_PARAMETER.get();
        String germanBogus = Message.BOGUS_PARAMETER.get(Locale.GERMAN);
        String frenchBogus = Message.BOGUS_PARAMETER.get(new Locale("fr"));

        // Make sure that the message are not null, or the same as another language (Assert)
        assertNotNull(englishPlanOk);
        assertNotNull(germanPlanOk);
        assertNotNull(frenchPlanOk);

        assertNotEquals(englishPlanOk, germanPlanOk);
        assertNotEquals(englishPlanOk, frenchPlanOk);

        assertNotNull(englishBogus);
        assertNotNull(germanBogus);
        assertNotNull(frenchBogus);

        assertNotEquals(englishBogus, germanBogus);
        assertNotEquals(englishBogus, frenchBogus);
    }
}
