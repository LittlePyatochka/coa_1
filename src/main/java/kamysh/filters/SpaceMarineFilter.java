package kamysh.filters;

import kamysh.dto.SpaceMarineDto;
import kamysh.dto.SpaceMarineWithIdDto;
import kamysh.entity.AstartesCategory;
import kamysh.repository.SpaceMarineRepository;
import kamysh.repository.SpaceMarineRepositoryImpl;
import kamysh.utils.*;
import kamysh.utils.Error;
import lombok.SneakyThrows;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.util.Date;


@WebFilter("/api/space-marine/*")
public class SpaceMarineFilter implements Filter {

    private Unmarshaller unmarshaller;
    private final SpaceMarineRepository spaceMarineRepository = new SpaceMarineRepositoryImpl();


    @SneakyThrows
    @Override
    public void init(FilterConfig filterConfig) {
        JAXBContext context = JAXBContext.newInstance(SpaceMarineWithIdDto.class);
        this.unmarshaller = context.createUnmarshaller();

    }

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        SpaceMarineWithIdDto spaceMarineDto = new SpaceMarineWithIdDto();

        if (req.getMethod().equalsIgnoreCase("delete") && req.getPathInfo() == null)
            throw new InvalidValueException(ErrorCode.MISSING_ID, ErrorMessage.MISSING_ID);


        if (req.getMethod().equalsIgnoreCase("post") || req.getMethod().equalsIgnoreCase("put")) {

            if (req.getPathInfo() != null) {
                try {
                    String rawPathInfo = req.getPathInfo().replaceAll("^/", "");
                    if (rawPathInfo.equals("health/count")) {
                        String health = req.getParameter("value");
                        if (Integer.parseInt(health) < 0)
                            throw new InvalidValueException(ErrorCode.INVALID_HEALTH_VALUE, ErrorMessage.INVALID_HEALTH_VALUE);
                        req.setAttribute("countHealth", req.getParameter("value"));
                    }
                } catch (NumberFormatException e) {
                    throw new InvalidValueException(ErrorCode.INVALID_HEALTH_VALUE, ErrorMessage.INVALID_HEALTH_VALUE);
                }
            } else {
                spaceMarineDto = (SpaceMarineWithIdDto) unmarshaller.unmarshal(servletRequest.getReader());
                if (spaceMarineDto.getId() != null && spaceMarineDto.getId() < 0)
                    throw new InvalidValueException(ErrorCode.WRONG_ID_FORMAT, ErrorMessage.WRONG_ID_FORMAT);
                if (spaceMarineDto.getName() == null)
                    throw new InvalidValueException(ErrorCode.MISSING_NAME, ErrorMessage.MISSING_NAME);
                if (spaceMarineDto.getCoordinates() == null)
                    throw new InvalidValueException(ErrorCode.MISSING_COORDINATES, ErrorMessage.MISSING_COORDINATES);
                if (spaceMarineDto.getHealth() == null)
                    throw new InvalidValueException(ErrorCode.MISSING_HEALTH_COUNT, ErrorMessage.MISSING_HEALTH_COUNT);
                if (spaceMarineDto.getHeartCount() == null)
                    throw new InvalidValueException(ErrorCode.MISSING_HEART_COUNT, ErrorMessage.MISSING_HEART_COUNT);

                if (!(spaceMarineDto.getCoordinates() > 0))
                    throw new InvalidValueException(ErrorCode.INVALID_COORDINATES_FORMAT, ErrorMessage.INVALID_COORDINATES_FORMAT);

                if (req.getMethod().equalsIgnoreCase("post")) spaceMarineDto.setCreationDate(new Date());

                if (req.getMethod().equalsIgnoreCase("put") && spaceMarineDto.getId() == null)
                    throw new InvalidValueException(ErrorCode.MISSING_ID, ErrorMessage.MISSING_ID);

                if (!(spaceMarineDto.getName().length() > 0))
                    throw new InvalidValueException(ErrorCode.INVALID_NAME_VALUE, ErrorMessage.INVALID_NAME_VALUE);

                if (spaceMarineDto.getCoordinates() == null)
                    throw new InvalidValueException(ErrorCode.WRONG_COORDINATES_FORMAT, ErrorMessage.WRONG_COORDINATES_FORMAT);

                if (!(spaceMarineDto.getHealth() > 0))
                    throw new InvalidValueException(ErrorCode.INVALID_HEALTH_VALUE, ErrorMessage.INVALID_HEALTH_VALUE);
                if (!(spaceMarineDto.getHeartCount() > 3))
                    throw new InvalidValueException(ErrorCode.INVALID_HEART_COUNT_VALUE, ErrorMessage.INVALID_HEART_COUNT_VALUE);

                if (spaceMarineDto.getCategory() == null)
                    throw new InvalidValueException(ErrorCode.WRONG_CATEGORY_FORMAT, ErrorMessage.WRONG_CATEGORY_FORMAT);

                if (spaceMarineDto.getChapter() == null)
                    throw new InvalidValueException(ErrorCode.WRONG_CHAPTER_FORMAT, ErrorMessage.WRONG_CHAPTER_FORMAT);

                if (!(spaceMarineDto.getChapter() > 0))
                    throw new InvalidValueException(ErrorCode.INVALID_CHAPTER_FORMAT, ErrorMessage.INVALID_CHAPTER_FORMAT);

                if (spaceMarineDto.getId() != null && spaceMarineRepository.findById(spaceMarineDto.getId()) == null)
                    throw new InvalidValueException(ErrorCode.OBJECT_NOT_FOUND, ErrorMessage.OBJECT_NOT_FOUND);

                req.setAttribute("spaceMarine", spaceMarineDto);
            }
        }

        if (req.getMethod().equalsIgnoreCase("get") || req.getMethod().equalsIgnoreCase("delete")) {
            if (req.getPathInfo() != null) {
                String rawPathInfo = req.getPathInfo().replaceAll("^/", "");
                try {
                    if (!rawPathInfo.equals("heartCount/min") && !(rawPathInfo.equals("loyal"))) {
                        Long id = Long.valueOf(rawPathInfo);
                        if (spaceMarineRepository.findById(id) == null)
                            throw new InvalidValueException(ErrorCode.OBJECT_NOT_FOUND, ErrorMessage.OBJECT_NOT_FOUND);
                        req.setAttribute("id", id);
                    }
                } catch (NumberFormatException e) {
                    throw new InvalidValueException(ErrorCode.WRONG_ID_FORMAT, ErrorMessage.WRONG_ID_FORMAT);
                }
                try {
                    if (rawPathInfo.equals("loyal")) req.setAttribute("loyal", req.getParameter("value"));
                } catch (NumberFormatException e) {
                    throw new InvalidValueException(ErrorCode.WRONG_LOYAL_FORMAT, ErrorMessage.WRONG_LOYAL_FORMAT);
                }
            } else {
                if (req.getParameter("count") != null) {
                    int count, page;

                    try {
                        count = Integer.parseInt(req.getParameter("count"));

                        if (!(count > 0)) {
                            throw new InvalidValueException(ErrorCode.INVALID_COUNT_VALUE, ErrorMessage.INVALID_COUNT_VALUE);
                        }

                        req.setAttribute("count", count);
                    } catch (NumberFormatException e) {
                        throw new InvalidValueException(ErrorCode.WRONG_COUNT_FORMAT, ErrorMessage.WRONG_COUNT_FORMAT);
                    }

                    if (req.getParameter("page") != null) {
                        try {
                            page = Integer.parseInt(req.getParameter("page"));

                            if (!(page > 0)) {
                                throw new InvalidValueException(ErrorCode.INVALID_PAGE_VALUE, ErrorMessage.INVALID_PAGE_VALUE);
                            }

                            req.setAttribute("page", page);
                        } catch (NumberFormatException e) {
                            throw new InvalidValueException(ErrorCode.WRONG_PAGE_FORMAT, ErrorMessage.WRONG_PAGE_FORMAT);
                        }
                    } else {
                        req.setAttribute("page", 1);
                    }
                }
            }


        }


        filterChain.doFilter(req, resp);
    }

    @Override
    public void destroy() {
    }
}