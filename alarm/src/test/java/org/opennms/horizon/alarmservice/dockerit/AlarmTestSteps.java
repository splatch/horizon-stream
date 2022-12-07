/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2012-2021 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2021 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 ******************************************************************************/

package org.opennms.horizon.alarmservice.dockerit;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Slf4j
public class AlarmTestSteps {

    public static final int DEFAULT_HTTP_SOCKET_TIMEOUT = 15_000;

    //
    // Test Configuration
    //
    private String applicationBaseUrl;

    private String kafaBootstrapServers;

    Producer<String, String> producer;

    //
    // Test Runtime Data
    //
    private Response restAssuredResponse;
    private Response rememberedRestAssuredResponse;

//========================================
// Gherkin Rules
//========================================

    @Given("Application Base URL in system property {string}")
    public void applicationBaseURLInSystemProperty(String systemProperty) {
        this.applicationBaseUrl = System.getProperty(systemProperty);

        log.info("Using BASE URL {}", this.applicationBaseUrl);
    }

    @Given("Kafka Boot Servers in system property {string}")
    public void kafkaBootstrapServersInSystemProperty(String systemProperty) {
        this.kafaBootstrapServers = System.getProperty(systemProperty);

        log.info("################### Kafka Boostrap-servers {}", this.kafaBootstrapServers);
    }
    
    @Given("Kafka producer is setup")
    public void setupKafkaProducer() {

        // create instance for properties to access producer configs
        Properties props = new Properties();

        //Assign localhost id
        props.put("bootstrap.servers", this.kafaBootstrapServers);

        //Set acknowledgements for producer requests.
        props.put("acks","all");

        //If the request fails, the producer can automatically retry,
        props.put("retries", 0);

        //Specify buffer size in config
        props.put("batch.size", 16384);

        //Reduce the no of requests less than 0
        props.put("linger.ms", 1);

        //The buffer.memory controls the total amount of memory available to the producer for buffering.
        props.put("buffer.memory", 33554432);

        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        producer = new KafkaProducer<String, String>(props);

    }

    @Then("Send POST request to application at path {string}")
    public void sendPOSTRequestToApplicationAtPath(String path) throws Exception {
        commonSendPOSTRequestToApplication(path);
    }

    @Then("Send message to Kafka at topic {string}")
    public void sendMessageToKafkaAtTopic(String topic) throws Exception {
        producer.send(new ProducerRecord<String, String>(topic, "blah"));
    }

    @Then("delay")
    public void delay() throws InterruptedException{
        Thread.sleep(6000);
    }

    @Then("Remember response body for later comparison")
    public void rememberResponseBodyForLaterComparison() {
        rememberedRestAssuredResponse = restAssuredResponse;
    }

//========================================
// Utility Rules
//----------------------------------------

    @Then("^DEBUG dump the response body$")
    public void debugDumpTheResponseBody() {
        this.log.info("RESPONSE BODY = {}", restAssuredResponse.getBody().asString());
    }

//========================================
// Internals
//----------------------------------------

    private RestAssuredConfig createRestAssuredTestConfig() {
        return RestAssuredConfig.config()
            .httpClient(HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", DEFAULT_HTTP_SOCKET_TIMEOUT)
                .setParam("http.socket.timeout", DEFAULT_HTTP_SOCKET_TIMEOUT)
            );
    }

    private void commonSendPOSTRequestToApplication(String path) throws MalformedURLException {
        URL requestUrl = new URL(new URL(this.applicationBaseUrl), path);

        RestAssuredConfig restAssuredConfig = this.createRestAssuredTestConfig();

        RequestSpecification requestSpecification =
            RestAssured
                .given()
                .config(restAssuredConfig);

        restAssuredResponse =
            requestSpecification
                .post(requestUrl)
                .thenReturn()
        ;
    }
}
