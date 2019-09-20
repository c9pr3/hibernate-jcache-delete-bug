package org.hibernate.bugs;

import java.util.logging.Logger;
import javax.persistence.*;
import org.junit.*;
import org.junit.runners.MethodSorters;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 * @see ORMUnitTestCase
 */
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public final class JPAUnitTestCase {

	private static EntityManagerFactory entityManagerFactory;
    private static EntityManager entityManager;
    private static final Logger logger = Logger.getLogger(JPAUnitTestCase.class.getSimpleName());

	@Before
	public void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("templatePU");
    }

	@After
	public void destroy() {
	    entityManager.close();
		entityManagerFactory.close();
	}

    @Test
    public void test1_transactionalCacheRemovableWorking() {
        entityManager = entityManagerFactory.createEntityManager();

        logger.info("Persisting new entity");
        final EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        final TransactionalEntity myEntity = new TransactionalEntity("text1");
        entityManager.persist(myEntity);
        tx.commit();

        logger.info("Adding entity to cache via select ... FOR UPDATE");
        final EntityTransaction tx2 = entityManager.getTransaction();
        tx2.begin();
        final TransactionalEntity found = entityManager.find(TransactionalEntity.class, 1L, LockModeType.PESSIMISTIC_WRITE);
        found.setTestText("text2");
        entityManager.merge(found);
        tx2.commit();
        Assert.assertTrue("Should have created one entry in cache", entityManagerFactory.getCache().contains(TransactionalEntity.class, 1L));

        logger.info("Removing entity");
        final EntityTransaction tx3 = entityManager.getTransaction();
        tx3.begin();
        final TransactionalEntity found4 = entityManager.find(TransactionalEntity.class, 1L, LockModeType.PESSIMISTIC_WRITE);
        entityManager.remove(found4);
        tx3.commit();
        Assert.assertFalse("Should have no entries left in cache", entityManagerFactory.getCache().contains(TransactionalEntity.class, 1L));
    }

    @Test
	public void test2_readWriteCacheRemovableFailing() {
        entityManager = entityManagerFactory.createEntityManager();

        logger.info("Persisting new entity");
        final EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        final ReadWriteEntity myEntity = new ReadWriteEntity("text1");
        entityManager.persist(myEntity);
        tx.commit();

        logger.info("Adding entity to cache via select ... FOR UPDATE");
        final EntityTransaction tx2 = entityManager.getTransaction();
        tx2.begin();
        final ReadWriteEntity found = entityManager.find(ReadWriteEntity.class, 1L, LockModeType.PESSIMISTIC_WRITE);
        found.setTestText("text2");
        entityManager.merge(found);
        tx2.commit();
        Assert.assertTrue("Should have created one entry in cache", entityManagerFactory.getCache().contains(ReadWriteEntity.class, 1L));

        logger.info("Removing entity");
        final EntityTransaction tx3 = entityManager.getTransaction();
        tx3.begin();
        final ReadWriteEntity found4 = entityManager.find(ReadWriteEntity.class, 1L, LockModeType.PESSIMISTIC_WRITE);
        entityManager.remove(found4);
        tx3.commit();
        Assert.assertFalse("Should have no entries left in cache", entityManagerFactory.getCache().contains(ReadWriteEntity.class, 1L));
    }

}
