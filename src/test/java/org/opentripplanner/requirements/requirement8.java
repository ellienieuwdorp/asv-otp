package org.opentripplanner.requirements;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.opentripplanner.standalone.config.OTPConfiguration;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class requirement8 {

    @Test
    public void test() {
        File config = new File("./src/test/java/org/opentripplanner/requirements");

        OTPConfiguration otpConfiguration = new OTPConfiguration(config);
        JsonNode configuration = otpConfiguration.getGraphConfig(config).routerConfig();
        String urlOne = "http://gtfs.openov.nl/gtfs-rt/tripUpdates.pb";
        String urlTwo = "http://gtfs.openov.nl/gtfs-rt/trainUpdates.pb";

        int iterator = 0;
        for (JsonNode configItem : configuration.path("updaters")) {

            String url = configItem.path("url").asText();
            if (url != null) {
                if (iterator == 0) assertTrue(url.equals(urlOne));
                if (iterator == 1) assertTrue(url.equals(urlTwo));
            }
            iterator++;
        }
    }
}
