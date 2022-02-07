package org.opennms.horizon.db.dao.impl;

import java.util.Objects;
import java.util.function.Supplier;

import javax.transaction.UserTransaction;

import org.opennms.horizon.db.dao.api.SessionUtils;

public class SessionUtilsImpl implements SessionUtils {
    private final UserTransaction tx;

    public SessionUtilsImpl(UserTransaction tx) {
        this.tx = Objects.requireNonNull(tx);
    }

    @Override
    public <V> V withTransaction(Supplier<V> supplier) {
        try {
            tx.begin();
            try {
                return supplier.get();
            } finally {
                tx.commit();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <V> V withReadOnlyTransaction(Supplier<V> supplier) {
        try {
            tx.begin();
            try {
                return supplier.get();
            } finally {
                tx.rollback();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
