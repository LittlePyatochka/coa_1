package kamysh.service;

import kamysh.dto.ChapterDto;
import kamysh.utils.InvalidValueException;

import java.util.List;

public interface ChapterService {

    List<ChapterDto> findAll();

    ChapterDto save(ChapterDto dto) throws InvalidValueException;

    ChapterDto findById(Long id);

    void delete(Long id);

}
