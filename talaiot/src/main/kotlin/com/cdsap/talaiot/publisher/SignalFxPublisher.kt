package com.cdsap.talaiot.publisher

import com.cdsap.talaiot.configuration.SignalFxPublisherConfiguration
import com.cdsap.talaiot.entities.TaskMeasurementAggregated
import com.cdsap.talaiot.logger.LogTracker
import com.codahale.metrics.MetricRegistry
import com.signalfx.codahale.SfxMetrics
import com.signalfx.codahale.reporter.SignalFxReporter
import com.signalfx.endpoint.SignalFxEndpoint
import com.signalfx.metrics.auth.StaticAuthToken
import java.net.URL
import java.util.concurrent.TimeUnit

class SignalFxPublisher(
    /**
     * General configuration for the publisher
     */
    private val configuration: SignalFxPublisherConfiguration,
    /**
     * LogTracker to print in console depending on the Mode
     */
    private val logTracker: LogTracker
    ) : Publisher {

    private val reporter: SignalFxReporter
    private val sfxMetrics: SfxMetrics

    init {
        val url = URL(configuration.url)
        val endpoint = SignalFxEndpoint(url.protocol, url.host, url.port)
        val registry = MetricRegistry()

        reporter = SignalFxReporter.Builder(
            registry,
            StaticAuthToken(configuration.authToken),
            configuration.url
        )
            .setEndpoint(endpoint).build()

        reporter.start(1, TimeUnit.SECONDS)

        val metricMetadata = reporter.metricMetadata

        sfxMetrics = SfxMetrics(registry, metricMetadata)
    }

    override fun publish(taskMeasurementAggregated: TaskMeasurementAggregated) {
        logTracker.log("================")
        logTracker.log("SignalFxPublisher")
        logTracker.log("================")
        if (configuration.url.isEmpty() ||
            configuration.authToken.isEmpty()
        ) {
            println(
                "SignalFxPublisher not executed. Configuration requires url and authToken: \n" +
                        "signalFxPublisher {\n" +
                        "            url = \"https://ingest.us0.signalfx.com\"\n" +
                        "            authToken = \"XXX\"\n" +
                        "}\n" +
                        "Please update your configuration"
            )

        } else {
            taskMeasurementAggregated.apply {
                taskMeasurement
                    .forEach {
                        val attributes = values + mapOf(
                            "state" to it.state.name,
                            "module" to it.module,
                            "rootNode" to it.rootNode.toString(),
                            "task" to it.taskPath
                        )
                        var gauge = sfxMetrics.longGauge("gradle_exec", attributes)
                        gauge.setValue(it.ms)
                    }
                logTracker.log("Wrote gradle_exec batch")
            }
        }
    }
}