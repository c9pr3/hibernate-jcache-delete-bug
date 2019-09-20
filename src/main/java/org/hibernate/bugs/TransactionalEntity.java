package org.hibernate.bugs;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * TransactionalEntity.
 *
 * @author Christian Scharmach (cs@2scale.net)
 * @since 19.09.19
 */
@Entity
@Table(name = "test2")
@Cacheable
@org.hibernate.annotations.Cache(
    usage = CacheConcurrencyStrategy.TRANSACTIONAL
)
@SuppressWarnings({"JpaDataSourceORMInspection", "unused", "WeakerAccess"})
public final class TransactionalEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "testtext", nullable = false)
    private String testText;

    public TransactionalEntity() {
        //
    }

    public TransactionalEntity(final String testText) {
        this.testText = testText;
    }

    public String getTestText() {
        return testText;
    }

    public void setTestText(final String testText) {
        this.testText = testText;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionalEntity)) {
            return false;
        }
        final TransactionalEntity dummyEntity = (TransactionalEntity) o;
        return Objects.equals(id, dummyEntity.id);
    }

}
