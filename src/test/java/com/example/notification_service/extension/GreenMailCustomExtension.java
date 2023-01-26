package com.example.notification_service.extension;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;

public class GreenMailCustomExtension extends GreenMailExtension {

    public GreenMailCustomExtension() {
        super(ServerSetupTest.SMTP);
    }

    public GreenMailExtension withConfiguration() {
        return super.withConfiguration(GreenMailConfiguration
                        .aConfig()
                        .withUser("test","test"))
                .withPerMethodLifecycle(false);
    }

}
