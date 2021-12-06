package kamysh.service;

import kamysh.controller.FilterConfiguration;
import kamysh.dto.HealthCountDto;
import kamysh.dto.SpaceMarineDto;
import kamysh.dto.SpaceMarineWithIdDto;
import kamysh.entity.SpaceMarine;
import kamysh.utils.MissingEntityException;

import java.text.ParseException;
import java.util.List;

public interface SpaceMarineService {

    List<SpaceMarineDto> findAll(FilterConfiguration filterConfiguration) throws ParseException;

    SpaceMarineDto saveOrUpdate(SpaceMarineWithIdDto dto) throws MissingEntityException;

    SpaceMarineDto findById(Long id);

    void delete(Long id);

    //    double getAvgHealthCount();
    Boolean deleteLoyal(Boolean loyal);

    SpaceMarineDto getMinHealthCount() throws ParseException;

    HealthCountDto getHealthCount (Integer minHealth);
}
