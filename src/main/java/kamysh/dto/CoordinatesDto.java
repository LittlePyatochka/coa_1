package kamysh.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@XmlRootElement
public class CoordinatesDto {
    private Long id;
    private Long x;
    private Integer y;
}

