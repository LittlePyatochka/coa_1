package kamysh.service;

import kamysh.controller.FilterConfiguration;
import kamysh.dto.HealthCountDto;
import kamysh.dto.SpaceMarineDto;
import kamysh.dto.SpaceMarineWithIdDto;
import kamysh.entity.SpaceMarine;
import kamysh.filters.SpaceMarineFilter;
import kamysh.mapper.SpaceMarineMapper;
import kamysh.repository.*;
import kamysh.utils.ErrorCode;
import kamysh.utils.InvalidValueException;
import kamysh.utils.MissingEntityException;
import kamysh.utils.Error;
import lombok.Data;
import lombok.SneakyThrows;

import java.text.ParseException;
import java.util.*;

public class SpaceMarineServiceImpl implements SpaceMarineService {

    private final SpaceMarineRepository spaceMarineRepository;
    private final SpaceMarineMapper spaceMarineMapper;

    private final CoordinatesRepository coordinatesRepository;
    private final ChapterRepository chapterRepository;

    public SpaceMarineServiceImpl() {
        this.spaceMarineRepository = new SpaceMarineRepositoryImpl();
        this.spaceMarineMapper = new SpaceMarineMapper();
        this.coordinatesRepository = new CoordinatesRepositoryImpl();
        this.chapterRepository = new ChapterRepositoryImpl();
    }

    @SneakyThrows
    @Override
    public List<SpaceMarineDto> findAll(FilterConfiguration filterConfiguration) {
        List<SpaceMarineDto> result = new ArrayList<>();
        List<SpaceMarine> spaceMarines = spaceMarineRepository.findAll(filterConfiguration);
        for (SpaceMarine spaceMarine : spaceMarines) {
            result.add(spaceMarineMapper.entityToDto(spaceMarine));
        }
        return result;
    }

    @Override
    public SpaceMarineDto saveOrUpdate(SpaceMarineWithIdDto dto) throws MissingEntityException {

        SpaceMarineDto spaceMarineDto = new SpaceMarineDto();

        SpaceMarine spaceMarine = spaceMarineMapper.dtoToEntity(dto);
        spaceMarine.setCoordinates(coordinatesRepository.findById(dto.getCoordinates()));
        if (spaceMarine.getCoordinates() == null) {
            throw new MissingEntityException(new Error(ErrorCode.MISSING_COORDINATES_ENTITY, "Could not found specified coordinates"));
        }
        spaceMarine.setChapter(chapterRepository.findById(dto.getChapter()));
        if (spaceMarine.getChapter() == null) {
            throw new MissingEntityException(new Error(ErrorCode.MISSING_CHAPTER_ENTITY, "Could not found specified chapter"));
        }

        if (spaceMarine.getId() == null) {
            spaceMarine.setCreationDate(new Date());
            spaceMarineRepository.save(spaceMarine);
            return spaceMarineMapper.entityToDto(spaceMarine);
        } else {
            SpaceMarine updatedValue = spaceMarineRepository.update(spaceMarine);
            return spaceMarineMapper.entityToDto(spaceMarine);
        }
    }

    @Override
    public SpaceMarineDto findById(Long id) {
        SpaceMarine spaceMarine = spaceMarineRepository.findById(id);

        if (spaceMarine != null) {
            return spaceMarineMapper.entityToDto(spaceMarine);
        } else {
            return null;
        }
    }

    @Override
    public void delete(Long id) {
        spaceMarineRepository.delete(id);
    }

    @Override
    public Boolean deleteLoyal(Boolean loyal) {
        FilterConfiguration filterConfiguration = new FilterConfiguration();
        List<SpaceMarineDto> spaceMarines = this.findAll(filterConfiguration);
        for (SpaceMarineDto spaceMarine : spaceMarines) {
            if (spaceMarine.isLoyal() == loyal) {
                spaceMarineRepository.delete(spaceMarine.getId());
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    @SneakyThrows
    @Override
    public SpaceMarineDto getMinHealthCount() {
        FilterConfiguration filterConfiguration = new FilterConfiguration();
        List<SpaceMarineDto> spaceMarines = this.findAll(filterConfiguration);
        SpaceMarineDto result = spaceMarines.get(0);

        for (SpaceMarineDto spaceMarine : spaceMarines) {
            if (result.getHeartCount() > spaceMarine.getHeartCount())
                result = spaceMarine;
        }
        return result;
    }

    @Override
    public HealthCountDto getHealthCount(Integer health) {
        FilterConfiguration filterConfiguration = new FilterConfiguration();
        List<SpaceMarineDto> spaceMarines = this.findAll(filterConfiguration);

        int result = 0;
        for (SpaceMarineDto spaceMarine : spaceMarines) {
            if (health > spaceMarine.getHealth()) result++;
        }
        HealthCountDto healthCountDto = new HealthCountDto();
        healthCountDto.setHealthCount(result);
        return healthCountDto;
    }
}
