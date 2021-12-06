package kamysh.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@XmlRootElement
public class ChapterDto {
    private Long id;
    private String name;
    private String parentLegion;
}

