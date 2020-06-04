package test_support.entity.lazyloading;

import io.jmix.data.entity.StandardEntity;

import javax.persistence.*;
import java.util.List;

@Table(name = "TEST_MANY_TO_MANY_ENTITY")
@Entity(name = "test_ManyToManyEntity")
public class ManyToManyEntity extends StandardEntity {
    private static final long serialVersionUID = -8839409577127111802L;

    @Column(name = "NAME")
    protected String name;

    @JoinTable(name = "TEST_MANY_TO_MANY_ENTITY_MANY_TO_MANY_TWO_ENTITY_LINK",
            joinColumns = @JoinColumn(name = "MANY_TO_MANY_ENTITY_ID"),
            inverseJoinColumns = @JoinColumn(name = "MANY_TO_MANY_TWO_ENTITY_ID"))
    @ManyToMany(fetch = FetchType.LAZY)
    protected List<ManyToManyTwoEntity> manyToManyTwoEntities;

    @Column(name = "SECOND_NAME")
    protected String secondName;

    public List<ManyToManyTwoEntity> getManyToManyTwoEntities() {
        return manyToManyTwoEntities;
    }

    public void setManyToManyTwoEntities(List<ManyToManyTwoEntity> manyToManyTwoEntities) {
        this.manyToManyTwoEntities = manyToManyTwoEntities;
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