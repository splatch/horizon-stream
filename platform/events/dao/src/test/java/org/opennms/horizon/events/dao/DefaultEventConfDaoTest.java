package org.opennms.horizon.events.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;

import org.junit.Test;

public class DefaultEventConfDaoTest {

    @Test
    public void canLoad() {
        DefaultEventConfDao defaultEventConfDao = new DefaultEventConfDao();
        defaultEventConfDao.init();
        assertThat(defaultEventConfDao.getAllEvents(), hasSize(greaterThanOrEqualTo(100)));
    }
}
