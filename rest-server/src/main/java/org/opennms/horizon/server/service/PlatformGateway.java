/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2022 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2022 The OpenNMS Group, Inc.
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
 *******************************************************************************/

package org.opennms.horizon.server.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import graphql.GraphQLContext;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.autoconfigure.DefaultGlobalContext;
import io.leangen.graphql.util.ContextUtils;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class PlatformGateway {
    public static final String URL_PATH_EVENTS = "/events";
    public static final String URL_PATH_ALARMS = "/alarms";
    public static final String URL_PATH_DEVICES = "/devices";
    public static final String URL_PATH_LOCATIONS = "/locations";
    public static final String URL_PATH_ALARMS_LIST = URL_PATH_ALARMS + "/list";
    public static final String URL_PATH_ALARMS_ACK = URL_PATH_ALARMS + "/%d/ack";
    public static final String URL_PATH_ALARMS_CLEAR = URL_PATH_ALARMS + "/%d/clear";
    public static final String URL_PATH_MINIONS = "/minions";
    public static final String URL_PATH_MINIONS_ID = "/minions/%s";
    private final WebClient webclient;

    public PlatformGateway(String baseUrl) {
        webclient = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build();

    }

    public String getAuthHeader(ResolutionEnvironment env) {
        GraphQLContext graphQLContext = env.dataFetchingEnvironment.getContext();
        DefaultGlobalContext context = (DefaultGlobalContext) ContextUtils.unwrapContext(graphQLContext);
        ServerWebExchange webExchange = (ServerWebExchange) context.getNativeRequest();
        ServerHttpRequest request = webExchange.getRequest();
        return request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
    }

    public <T> Mono<T> post(String path, String authToken, Object data, Class<T> returnType) {
        return requestWithBody(path, HttpMethod.POST, authToken, data, returnType);
    }

    public <T> Mono<T> get(String path, String authToken, Class<T> returnType) {
        return webclient.get()
            .uri(path)
            .header(HttpHeaders.AUTHORIZATION, authToken)
            .retrieve()
            .bodyToMono(returnType);
    }

    public <T> Mono<T> put(String path, String authToken, Object data, Class<T> returnType) {
        return requestWithBody(path, HttpMethod.PUT, authToken, data, returnType);
    }

    private <T> Mono <T> requestWithBody(String path, HttpMethod method, String token, Object data, Class<T> type) {
        return webclient.method(method)
            .uri(path)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(data)
            .retrieve().bodyToMono(type);
    }
}
