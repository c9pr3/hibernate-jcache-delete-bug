package org.hibernate.bugs;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "test1")
@Cacheable
@org.hibernate.annotations.Cache(
//    usage = CacheConcurrencyStrategy.TRANSACTIONAL
    usage = CacheConcurrencyStrategy.READ_WRITE
)
@SuppressWarnings({"JpaDataSourceORMInspection", "unused", "WeakerAccess"})
public final class ReadWriteEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "testtext", nullable = false)
    private String testText;

    public ReadWriteEntity() {
        //
    }

    public ReadWriteEntity(final String testText) {
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
        if (!(o instanceof ReadWriteEntity)) {
            return false;
        }
        final ReadWriteEntity dummyEntity = (ReadWriteEntity) o;
        return Objects.equals(id, dummyEntity.id);
    }

}

