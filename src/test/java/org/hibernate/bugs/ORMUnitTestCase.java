/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.bugs;

import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cache.jcache.internal.JCacheRegionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using its built-in unit test framework. Although ORMStandaloneTestCase is perfectly acceptable as a reproducer, usage of this class is much preferred. Since we nearly always include a regression test with bug fixes, providing your reproducer using this method simplifies the process.
 *
 * What's even better?  Fork hibernate-orm itself, add your test case directly to a module's unit tests, then submit it as a PR!
 */
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public final class ORMUnitTestCase extends BaseCoreFunctionalTestCase {

    // Add in any settings that are specific to your test.  See resources/hibernate.properties for the defaults.
    @Override
    protected void configure(Configuration configuration) {
        super.configure(configuration);

        configuration.setProperty(AvailableSettings.SHOW_SQL, Boolean.TRUE.toString());
        configuration.setProperty(AvailableSettings.FORMAT_SQL, Boolean.TRUE.toString());
        configuration.setProperty(AvailableSettings.CACHE_REGION_FACTORY, JCacheRegionFactory.class.getName());
        configuration.setProperty(AvailableSettings.USE_SECOND_LEVEL_CACHE, Boolean.TRUE.toString());
        configuration.setProperty("hibernate.javax.cache.provider", EhcacheCachingProvider.class.getName());
    }

    // If you use *.hbm.xml mappings, instead of annotations, add the mappings here.
    @Override
    protected String[] getMappings() {
        return new String[]{
        };
    }

    // If those mappings reside somewhere other than resources/org/hibernate/test, change this.
    @Override
    protected String getBaseForMappings() {
        return "org/hibernate/test/";
    }

    // Add your entities here.
    @Override
    protected Class[] getAnnotatedClasses() {
        return new Class[]{
            ReadWriteEntity.class,
            TransactionalEntity.class
        };
    }

    @Test
    public void test1_transactionalCacheRemovableWorking() {
        // BaseCoreFunctionalTestCase automatically creates the SessionFactory and provides the Session.
        final Session session = openSession();
        final Transaction tx = session.beginTransaction();
        final TransactionalEntity myEntity = new TransactionalEntity("text1");
        session.persist(myEntity);
        tx.commit();

        final EntityTransaction tx2 = session.getTransaction();
        tx2.begin();
        final TransactionalEntity found = session.find(TransactionalEntity.class, 1L, LockModeType.PESSIMISTIC_WRITE);
        found.setTestText("text2");
        session.merge(found);
        tx2.commit();
        Assert.assertTrue("Should have created one entry in cache", session.getSessionFactory().getCache().contains(TransactionalEntity.class, 1L));

        final EntityTransaction tx3 = session.getTransaction();
        tx3.begin();
        final TransactionalEntity found4 = session.find(TransactionalEntity.class, 1L, LockModeType.PESSIMISTIC_WRITE);
        session.remove(found4);
        tx3.commit();
        Assert.assertFalse("Should have no entries left in cache", session.getSessionFactory().getCache().contains(TransactionalEntity.class, 1L));

        session.close();
    }

    @Test
    public void test2_readWriteCacheRemovableFailing() {
        final Session session = openSession();
        final Transaction tx = session.beginTransaction();
        final ReadWriteEntity myEntity = new ReadWriteEntity("text1");
        session.persist(myEntity);
        tx.commit();

        final EntityTransaction tx2 = session.getTransaction();
        tx2.begin();
        final ReadWriteEntity found = session.find(ReadWriteEntity.class, 1L, LockModeType.PESSIMISTIC_WRITE);
        found.setTestText("text2");
        session.merge(found);
        tx2.commit();
        Assert.assertTrue("Should have created one entry in cache", session.getSessionFactory().getCache().contains(ReadWriteEntity.class, 1L));

        final EntityTransaction tx3 = session.getTransaction();
        tx3.begin();
        final ReadWriteEntity found4 = session.find(ReadWriteEntity.class, 1L, LockModeType.PESSIMISTIC_WRITE);
        session.remove(found4);
        tx3.commit();
        Assert.assertFalse("Should have no entries left in cache", session.getSessionFactory().getCache().contains(ReadWriteEntity.class, 1L));

        session.close();
    }
}

