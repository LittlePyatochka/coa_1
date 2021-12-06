package kamysh.repository;

import kamysh.controller.FilterConfiguration;
import kamysh.entity.SpaceMarine;

import java.text.ParseException;
import java.util.List;

public interface SpaceMarineRepository {
    List<SpaceMarine> findAll();

    List<SpaceMarine> findAll(FilterConfiguration filterConfiguration) throws ParseException;

    void save(SpaceMarine coordinates);

    SpaceMarine findById(Long id);

    SpaceMarine update(SpaceMarine newValue);

    void delete(Long id);
}
