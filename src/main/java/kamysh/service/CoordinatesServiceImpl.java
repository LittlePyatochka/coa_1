package kamysh.service;

import kamysh.dto.CoordinatesDto;
import kamysh.entity.Coordinates;
import kamysh.mapper.CoordinatesMapper;
import kamysh.repository.CoordinatesRepository;
import kamysh.repository.CoordinatesRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

public class CoordinatesServiceImpl implements CoordinatesService {

    private final CoordinatesRepository coordinatesRepository;
    private final CoordinatesMapper coordinatesMapper;

    public CoordinatesServiceImpl() {
        this.coordinatesRepository = new CoordinatesRepositoryImpl();
        this.coordinatesMapper = new CoordinatesMapper();
    }

    @Override
    public List<CoordinatesDto> findAll() {
        List<Coordinates> coordinates =  coordinatesRepository.findAll();

        List<CoordinatesDto> result = new ArrayList<>();

        for (Coordinates element : coordinates) {
            result.add(coordinatesMapper.entityToDto(element));
        }

        return result;
    }

    @Override
    public CoordinatesDto save(CoordinatesDto dto) {
        Coordinates coordinates = coordinatesMapper.dtoToEntity(dto);

        coordinatesRepository.save(coordinates);
        return coordinatesMapper.entityToDto(coordinates);
    }

    @Override
    public CoordinatesDto findById(Long id) {
        Coordinates coordinates = coordinatesRepository.findById(id);

        if (coordinates != null) {
            return coordinatesMapper.entityToDto(coordinates);
        } else {
            return null;
        }
    }

    @Override
    public void delete(Long id) {
        coordinatesRepository.delete(id);
    }
}
