/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2021 The OpenNMS Group, Inc.
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
 *******************************************************************************/

package org.opennms.miniongateway.ignite;

import org.apache.ignite.cache.store.jdbc.CacheJdbcBlobStore;

/**
 * Helper classs to constitute PostgreSQL support for cache blob store.
 *
 * It might not be used directly, declared here to refer relevant SQL queries configuration.
 *
 * @param <K> Key type.
 * @param <V> Value type.
 */
public class PostgresBlobCacheStore<K, V> extends CacheJdbcBlobStore<K, V> {

    /**
     * Default create table query
     * (value is <tt>create table if not exists ENTRIES (akey binary primary key, val binary)</tt>).
     */
    public static final String DFLT_CREATE_TBL_QRY = "create table if not exists ENTRIES " +
        "(akey bytea primary key, val bytea)";

    public PostgresBlobCacheStore() {
        setCreateTableQuery(DFLT_CREATE_TBL_QRY);
    }
}
