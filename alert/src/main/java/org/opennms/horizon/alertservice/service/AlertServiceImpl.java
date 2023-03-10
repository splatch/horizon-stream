/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2023 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2023 The OpenNMS Group, Inc.
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

package org.opennms.horizon.alertservice.service;

import lombok.RequiredArgsConstructor;
import org.opennms.horizon.alerts.proto.Alert;
import org.opennms.horizon.alertservice.api.AlertLifecyleListener;
import org.opennms.horizon.alertservice.api.AlertService;
import org.opennms.horizon.alertservice.db.repository.AlertRepository;
import org.opennms.horizon.events.proto.Event;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertEventProcessor alertEventProcessor;
    private final AlertRepository alertRepository;
    private final AlertListenerRegistry alertListenerRegistry;
    private final AlertMapper alertMapper;

    @Override
    public Optional<Alert> reduceEvent(Event e) {
        Optional<Alert> alert = alertEventProcessor.process(e);
        if (alert.isPresent()) {
            alertListenerRegistry.forEachListener((l) -> l.handleNewOrUpdatedAlert(alert.get()));
        }
        return alert;
    }

    @Override
    @Transactional
    public void deleteAlertById(long id) {
        Optional<org.opennms.horizon.alertservice.db.entity.Alert> dbAlert = alertRepository.findById(id);
        if (dbAlert.isEmpty()) {
            return;
        }

        alertRepository.deleteById(id);
        Alert alert = alertMapper.toProto(dbAlert.get());
        alertListenerRegistry.forEachListener((l) -> l.handleDeletedAlert(alert));
    }

    @Override
    @Transactional
    public void deleteAlert(Alert alert) {
        alertRepository.deleteById(alert.getDatabaseId());
        alertListenerRegistry.forEachListener((l) -> l.handleDeletedAlert(alert));
    }

    @Override
    @Transactional
    public Optional<Alert> acknowledgeAlertById(long id) {
        Optional<org.opennms.horizon.alertservice.db.entity.Alert> dbAlert = alertRepository.findById(id);
        if (dbAlert.isEmpty()) {
            return Optional.empty();
        }

        org.opennms.horizon.alertservice.db.entity.Alert alert = dbAlert.get();
        alert.setAcknowledgedAt(new Date());
        alert.setAcknowledgedByUser("me");
        alertRepository.save(alert);
        return Optional.of(alertMapper.toProto(alert));
    }

    @Override
    @Transactional
    public Optional<Alert> unacknowledgeAlertById(long id) {
        Optional<org.opennms.horizon.alertservice.db.entity.Alert> dbAlert = alertRepository.findById(id);
        if (dbAlert.isEmpty()) {
            return Optional.empty();
        }

        org.opennms.horizon.alertservice.db.entity.Alert alert = dbAlert.get();
        alert.setAcknowledgedAt(null);
        alert.setAcknowledgedByUser(null);
        alertRepository.save(alert);
        return Optional.of(alertMapper.toProto(alert));
    }

    @Override
    public void addListener(AlertLifecyleListener listener) {
        alertListenerRegistry.addListener(listener);
    }

    @Override
    public void removeListener(AlertLifecyleListener listener) {
        alertListenerRegistry.addListener(listener);
    }
}
