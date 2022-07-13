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

package org.opennms.horizon.inventory.device.service;

import java.io.Serializable;
import java.util.List;

import org.opennms.horizon.db.dao.api.OnmsDao;
import org.opennms.horizon.db.dao.api.SessionUtils;

public abstract class AbstractService<T, ID extends Serializable> {
  protected OnmsDao<T, ID> dao;
  protected SessionUtils sessionUtils;

  public void setDao(OnmsDao<T, ID> dao) {
    this.dao = dao;
  }

  public void setSessionUtils(SessionUtils sessionUtils) {
    this.sessionUtils = sessionUtils;
  }

  public T getById(ID id) {
    return sessionUtils.withReadOnlyTransaction(()-> dao.get(id));
  }

  public List<T> findAll() {
    return sessionUtils.withReadOnlyTransaction(
        () -> dao.findAll()
    );
  }
}
