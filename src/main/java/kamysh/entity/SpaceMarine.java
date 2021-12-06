package kamysh.entity;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity(name = "SPACE_MARINE")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class SpaceMarine {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    @Column(name = "NAME")
    private String name; //Поле не может быть null, Строка не может быть пустой

    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinColumn(name = "COORDINATES")
    private Coordinates coordinates; //Поле не может быть null

    @Column(name = "CREATION_DATE")
    private Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически

    @Column(name = "HEALTH")
    private Float health; //Значение поля должно быть больше 0

    @Column(name = "HEART_COUNT")
    private Long heartCount; //Поле не может быть null, Значение поля должно быть больше 0, Максимальное значение поля: 3

    @Column(name = "LOYAL")
    private boolean loyal;

    @Column(name = "CATEGORY")
    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    private AstartesCategory category; //Поле не может быть null

    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinColumn(name = "CHAPTER")
    private Chapter chapter; //Поле не может быть null
}
