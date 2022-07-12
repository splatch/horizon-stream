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

package org.opennms.horizon.minion.rest.internal;

import com.google.common.base.Strings;
import org.opennms.horizon.db.dao.api.MinionDao;
import org.opennms.horizon.db.dao.api.SessionUtils;
import org.opennms.horizon.db.model.OnmsMinion;
import org.opennms.horizon.db.model.dto.MinionDTO;
import org.opennms.horizon.minion.rest.MinionRestService;

import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

public class MinionRestServiceImpl implements MinionRestService {

    private final MinionDao minionDao;

    private final SessionUtils sessionUtils;

    public MinionRestServiceImpl(MinionDao minionDao, SessionUtils sessionUtils) {
        this.minionDao = minionDao;
        this.sessionUtils = sessionUtils;
    }

    @Override
    public Response getMinion(String id) {
        OnmsMinion minion = sessionUtils.withReadOnlyTransaction(() -> minionDao.findById(id));
        if (minion == null) {
            return Response.noContent().build();
        }
        return Response.ok(mapper(minion)).build();
    }

    @Override
    public Response getMinions(String location, String label) {

        var builder = minionDao.getEntityManager().getCriteriaBuilder();
        var criteriaQuery = builder.createQuery(OnmsMinion.class);
        var root = criteriaQuery.from(OnmsMinion.class);
        if (!Strings.isNullOrEmpty(location)) {
            criteriaQuery.where(builder.like(root.get("location"), location));
        }
        if (!Strings.isNullOrEmpty(label)) {
            criteriaQuery.where(builder.like(root.get("label"), label));
        }
        var query = minionDao.getEntityManager().createQuery(criteriaQuery);
        var minions = sessionUtils.withReadOnlyTransaction(query::getResultList);
        if (minions.isEmpty()) {
            return Response.noContent().build();
        }
        var minionDTOS = minions.stream().map(this::mapper).collect(Collectors.toList());

        return Response.ok(minionDTOS).build();
    }

    private MinionDTO mapper(OnmsMinion minion) {
        MinionDTO minionDTO = new MinionDTO();
        minionDTO.setId(minion.getId());
        minionDTO.setLabel(minion.getLabel());
        minionDTO.setLocation(minion.getLocation());
        return minionDTO;
    }
}
