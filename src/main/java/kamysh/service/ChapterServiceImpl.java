package kamysh.service;

import kamysh.dto.ChapterDto;
import kamysh.entity.Chapter;
import kamysh.mapper.ChapterMapper;
import kamysh.repository.ChapterRepository;
import kamysh.repository.ChapterRepositoryImpl;
import kamysh.utils.InvalidValueException;

import java.util.ArrayList;
import java.util.List;

public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository personRepository;
    private final ChapterMapper personMapper;

    public ChapterServiceImpl() {
        this.personRepository = new ChapterRepositoryImpl();
        this.personMapper = new ChapterMapper();
    }

    @Override
    public List<ChapterDto> findAll() {
        List<Chapter> chapter =  personRepository.findAll();

        List<ChapterDto> result = new ArrayList<>();

        for (Chapter person : chapter) {
            result.add(personMapper.entityToDto(person));
        }

        return result;
    }

    @Override
    public ChapterDto save(ChapterDto dto) throws InvalidValueException {
        Chapter person = personMapper.dtoToEntity(dto);

        personRepository.save(person);
        return personMapper.entityToDto(person);
    }

    @Override
    public ChapterDto findById(Long id) {
        Chapter person = personRepository.findById(id);

        if (person != null) {
            return personMapper.entityToDto(person);
        } else {
            return null;
        }
    }

    @Override
    public void delete(Long id) {
        personRepository.delete(id);
    }
}
