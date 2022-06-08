package org.opennms.horizon.repository.impl;

import lombok.AllArgsConstructor;
import org.opennms.horizon.db.dao.api.NodeDao;
import org.opennms.horizon.db.model.OnmsNode;
import org.opennms.horizon.repository.api.NodeRepository;

@AllArgsConstructor
public class NodeRepositoryImpl implements NodeRepository {
    private final NodeDao nodeDao;

    @Override
    public OnmsNode read(String id) {
        return nodeDao.get(Integer.parseInt(id));
    }

    @Override
    public void delete(String id) {
       nodeDao.delete(Integer.parseInt(id));
    }

    @Override
    public void save(OnmsNode entity) {
       nodeDao.save(entity);
    }

    @Override
    public void update(OnmsNode entity) {
       nodeDao.update(entity);
    }
}
