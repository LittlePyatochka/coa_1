package kamysh.service;

import kamysh.dto.CoordinatesDto;

import java.util.List;

public interface CoordinatesService {

    List<CoordinatesDto> findAll();

    CoordinatesDto save(CoordinatesDto dto);

    CoordinatesDto findById(Long id);

    void delete(Long id);

}
