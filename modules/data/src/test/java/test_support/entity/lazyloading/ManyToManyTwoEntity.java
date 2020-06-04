package test_support.entity.lazyloading;

import io.jmix.data.entity.StandardEntity;

import javax.persistence.*;
import java.util.List;

@Table(name = "TEST_MANY_TO_MANY_TWO_ENTITY")
@Entity(name = "test_ManyToManyTwoEntity")
public class ManyToManyTwoEntity extends StandardEntity {
    private static final long serialVersionUID = 1460700519244194333L;

    @Column(name = "NAME")
    protected String name;

    @Column(name = "SECOND_NAME")
    protected String secondName;

    @JoinTable(name = "TEST_MANY_TO_MANY_ENTITY_MANY_TO_MANY_TWO_ENTITY_LINK",
            joinColumns = @JoinColumn(name = "MANY_TO_MANY_TWO_ENTITY_ID"),
            inverseJoinColumns = @JoinColumn(name = "MANY_TO_MANY_ENTITY_ID"))
    @ManyToMany(fetch = FetchType.LAZY)
    protected List<ManyToManyEntity> manyToManyEntities;

    public List<ManyToManyEntity> getManyToManyEntities() {
        return manyToManyEntities;
    }

    public void setManyToManyEntities(List<ManyToManyEntity> manyToManyEntities) {
        this.manyToManyEntities = manyToManyEntities;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}