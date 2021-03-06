package com.cdsap.talaiot.functional

import io.kotlintest.specs.BehaviorSpec
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome

class PushGatewayPublisherBuildTest : BehaviorSpec({
    given("Build Gradle File") {
        val testProjectDir = TemporaryFolder()
        `when`("Talaiot is included with PushGatewayPublisher") {
            testProjectDir.create()
            val buildFile = testProjectDir.newFile("build.gradle")
            buildFile.appendText(
                """
                   plugins {
                      id 'java'
                      id 'talaiot'
                   }

                  talaiot{
                      logger = com.cdsap.talaiot.logger.LogTracker.Mode.INFO
                      publishers {
                         pushGatewayPublisher {
                             url = "http://localhost:9091"
                             jobName = "tracking"
                    }
                }
            }
            """
            )
            val result = GradleRunner.create()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("assemble")
                .withPluginClasspath()
                .build()
            then("logs are shown in the output and including the pushGateway format") {
                println(result.output)
                assert(result.output.contains("PushGatewayPublisher"))
                assert(result.output.contains(":assemble{state=\"EXECUTED\","))
                assert(result.task(":assemble")?.outcome == TaskOutcome.SUCCESS)

            }
            testProjectDir.delete()
        }
    }
})
