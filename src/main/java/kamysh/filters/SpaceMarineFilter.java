package kamysh.filters;

import kamysh.dto.SpaceMarineDto;
import kamysh.dto.SpaceMarineWithIdDto;
import kamysh.entity.AstartesCategory;
import kamysh.repository.SpaceMarineRepository;
import kamysh.repository.SpaceMarineRepositoryImpl;
import kamysh.utils.Utils;
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

    public static final int INVALID_XML = 1;

    public static final int MISSING_ID = 100;
    public static final int WRONG_ID_FORMAT = 101;

    public static final int MISSING_NAME = 110;
    public static final int INVALID_NAME_VALUE = 111;

    public static final int MISSING_COORDINATES = 120;
    public static final int WRONG_COORDINATES_FORMAT = 121;
    public static final int MISSING_COORDINATES_ENTITY = 122;

    public static final int MISSING_HEALTH_COUNT = 140;
    public static final int INVALID_HEALTH_VALUE = 142;

    public static final int MISSING_HEART_COUNT = 150;
    public static final int INVALID_HEART_COUNT_VALUE = 152;

    public static final int WRONG_LOYAL_FORMAT = 161;
    public static final int WRONG_CATEGORY_FORMAT = 171;

    public static final int WRONG_CHAPTER_FORMAT = 181;
    public static final int MISSING_CHAPTER_ENTITY = 182;

    public static final int WRONG_COUNT_FORMAT = 590;
    public static final int INVALID_COUNT_VALUE = 591;

    public static final int WRONG_PAGE_FORMAT = 590;
    public static final int INVALID_PAGE_VALUE = 591;

    private JAXBContext context;
    private Unmarshaller unmarshaller;
    private final SpaceMarineRepository spaceMarineRepository = new SpaceMarineRepositoryImpl();


    @SneakyThrows
    @Override
    public void init(FilterConfig filterConfig) {
        this.context = JAXBContext.newInstance(SpaceMarineWithIdDto.class);
        this.unmarshaller = context.createUnmarshaller();

    }

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        SpaceMarineWithIdDto spaceMarineDto = new SpaceMarineWithIdDto();

        if (req.getMethod().equalsIgnoreCase("delete") && req.getPathInfo() == null) {
            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, MISSING_ID, "Id must be specified in the end of request");
            return;
        }


        if (req.getMethod().equalsIgnoreCase("post") || req.getMethod().equalsIgnoreCase("put")) {

            if (req.getPathInfo() != null) {
                try {
                    String rawPathInfo = req.getPathInfo().replaceAll("^/", "");
                    if (rawPathInfo.equals("health/count")) {
                            String health = req.getParameter("value");
                            if (Integer.parseInt(health) < 0) {
                                Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, INVALID_HEALTH_VALUE, "Field 'health count' must be bigger than 0");
                                return;
                            }else {
                                req.setAttribute("countHealth", req.getParameter("value"));
                            }
                    }
                } catch (NumberFormatException e) {
                    Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, INVALID_HEALTH_VALUE, "Field 'health count' must be integer");
                    return;
                }
            } else {
                try {
                    spaceMarineDto = (SpaceMarineWithIdDto) unmarshaller.unmarshal(servletRequest.getReader());
                } catch (JAXBException e) {
                    Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, INVALID_XML, "Invalid XML in request body");
                    return;
                }
                if (spaceMarineDto.getId() != null && spaceMarineDto.getId() < 0) {
                    Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, WRONG_ID_FORMAT, "Field 'id' must be bigger than 0");
                    return;
                }
                if (spaceMarineDto.getName() == null) {
                    Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, MISSING_NAME, "Field 'name' must be specified in request body");
                    return;
                }
                if (spaceMarineDto.getCoordinates() == null) {
                    Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, MISSING_COORDINATES, "Field 'coordinates' must be specified in request body");
                    return;
                }
                if (spaceMarineDto.getHealth() == null) {
                    Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, MISSING_HEALTH_COUNT, "Field 'health' must be specified in request body");
                    return;
                }
                if (spaceMarineDto.getHeartCount() == null) {
                    Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, MISSING_HEART_COUNT, "Field 'heartCount' must be specified in request body");
                    return;
                }

                if (req.getMethod().equalsIgnoreCase("post")) {
                    spaceMarineDto.setCreationDate(new Date());
                }

                if (req.getMethod().equalsIgnoreCase("put")) {
                    if (spaceMarineDto.getId() == null) {
                        Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, MISSING_ID, "Field 'id' must be specified in request body");
                        return;
                    }
                }

                if (!(spaceMarineDto.getName().length() > 0)) {
                    Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, INVALID_NAME_VALUE, "Length of field 'name' must be bigger than 0");
                    return;
                }


                if (spaceMarineDto.getCoordinates() == null) {
                    Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, WRONG_COORDINATES_FORMAT, "Field 'coordinates' must be integer");
                    return;
                }

                if (!(spaceMarineDto.getHealth() > 0)) {
                    Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, INVALID_HEALTH_VALUE, "Field 'health' must be bigger than 0");
                    return;
                }

                if (!(spaceMarineDto.getHeartCount() > 3)) {
                    Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, INVALID_HEART_COUNT_VALUE, "Field 'heartCount' must be bigger than 3");
                    return;
                }

                if (spaceMarineDto.getCategory() == null) {
                    Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, WRONG_CATEGORY_FORMAT, "Field 'category' must be one of expected value. Check documentation for details");
                    return;
                }

                if (spaceMarineDto.getChapter() == null) {
                    Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, WRONG_CHAPTER_FORMAT, "Field 'chapter' must be integer");
                    return;
                }

                if(spaceMarineDto.getId() != null && spaceMarineRepository.findById(spaceMarineDto.getId()) == null){
                    Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, 404, "Object for update not found");
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }


                req.setAttribute("spaceMarine", spaceMarineDto);
            }
        }


        if (req.getMethod().equalsIgnoreCase("get") || req.getMethod().equalsIgnoreCase("delete")) {
            if (req.getPathInfo() != null) {
                String rawPathInfo = req.getPathInfo().replaceAll("^/", "");
                try {
                    if (!rawPathInfo.equals("heartCount/min") && !(rawPathInfo.equals("loyal"))) {
                        Long id = Long.valueOf(rawPathInfo);

                        if(spaceMarineRepository.findById(id) == null){
                            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, 404, "Object for delete not found");
                            return;
                        }

                        req.setAttribute("id", id);
                    }
                } catch (NumberFormatException e) {
                    Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, WRONG_ID_FORMAT, "Field 'id' must be integer" + req);
                    return;
                }
                try {
                    if (rawPathInfo.equals("loyal")) {
                        req.setAttribute("loyal", req.getParameter("value"));
                    }
                } catch (NumberFormatException e) {
                    Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, WRONG_LOYAL_FORMAT, "Field 'loyal' must be boolean");
                    return;
                }
            }else {
                if (req.getParameter("count") != null) {
                    int count, page;

                    try {
                        count = Integer.parseInt(req.getParameter("count"));

                        if (!(count > 0)) {
                            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, INVALID_COUNT_VALUE, "Parameter 'count' must be bigger than 0");
                            return;
                        }

                        req.setAttribute("count", count);
                    } catch (NumberFormatException e) {
                        Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, WRONG_COUNT_FORMAT, "Parameter 'count' must be integer");
                        return;
                    }

                    if (req.getParameter("page") != null) {
                        try {
                            page = Integer.parseInt(req.getParameter("page"));

                            if (!(page > 0)) {
                                Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, INVALID_PAGE_VALUE, "Parameter 'page' must be bigger than 0");
                                return;
                            }

                            req.setAttribute("page", page);
                        } catch (NumberFormatException e) {
                            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, WRONG_PAGE_FORMAT, "Parameter 'page' must be integer");
                            return;
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