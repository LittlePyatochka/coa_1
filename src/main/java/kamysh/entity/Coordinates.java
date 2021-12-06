package kamysh.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity(name = "COORDINATES")
public class Coordinates {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "X")
    private Long x; //Поле не может быть null

    @Column(name = "Y")
    private Integer y; //Поле не может быть null

    public Coordinates(Long x, Integer y) {
        this.x = x;
        this.y = y;
    }
}
