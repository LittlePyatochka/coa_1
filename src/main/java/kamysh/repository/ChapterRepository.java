package kamysh.repository;

import kamysh.entity.Chapter;
import kamysh.utils.InvalidValueException;

import java.util.List;

public interface ChapterRepository {
    List<Chapter> findAll();

    void save(Chapter coordinates) throws InvalidValueException;

    Chapter findById(Long id);

    Chapter update(Chapter newValue);

    void delete(Long id);
}
