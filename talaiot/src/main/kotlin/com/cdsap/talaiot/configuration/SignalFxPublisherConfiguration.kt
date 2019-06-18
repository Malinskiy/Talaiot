package com.cdsap.talaiot.configuration

class SignalFxPublisherConfiguration: PublisherConfiguration {
    /**
     * name of the publisher
     */
    override var name: String = "signalFx"
    /**
     * SignalFx ingest url required to send the measurements. For instance https://ingest.us0.signalfx.com
     */
    var url: String = ""
    /**
     * authentication token
     */
    var authToken: String = ""
}