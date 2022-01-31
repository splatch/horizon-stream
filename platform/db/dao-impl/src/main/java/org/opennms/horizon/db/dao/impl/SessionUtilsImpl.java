package org.opennms.horizon.db.dao.impl;

import java.util.Objects;
import java.util.function.Supplier;

import javax.transaction.TransactionManager;

import org.opennms.horizon.db.dao.api.SessionUtils;

public class SessionUtilsImpl implements SessionUtils {

    private TransactionManager transactionManager;

    public SessionUtilsImpl(TransactionManager transactionManager) {
        this.transactionManager = Objects.requireNonNull(transactionManager);
    }

    @Override
    public <V> V withTransaction(Supplier<V> supplier) {
        // FIXME: Ooops
        return supplier.get();
    }

    @Override
    public <V> V withReadOnlyTransaction(Supplier<V> supplier) {
        // FIXME: Ooops
        return supplier.get();
    }

    @Override
    public <V> V withManualFlush(Supplier<V> supplier) {
        // FIXME: Ooops
        return supplier.get();
    }
}
