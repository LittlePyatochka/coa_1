package kamysh.filters;

import kamysh.dto.ChapterDto;
import kamysh.dto.SpaceMarineWithIdDto;
import kamysh.entity.AstartesCategory;
import kamysh.utils.ErrorCode;
import kamysh.utils.ErrorMessage;
import kamysh.utils.InvalidValueException;
import kamysh.utils.Utils;
import lombok.SneakyThrows;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@WebFilter("/api/space-marine/*")
public class OptionsFilter implements Filter {

    private Unmarshaller unmarshaller;

    private static final List<String> EXPECTED_FIELDS = Collections.unmodifiableList(Arrays.asList("id", "name", "coordinates", "creationDate", "health", "heartCount", "loyal", "category", "chapter"));
    private static final List<String> EXPECTED_ACTIONS = Collections.unmodifiableList(Arrays.asList("==", "contains"));

    @SneakyThrows
    @Override
    public void init(FilterConfig filterConfig) {
        JAXBContext context = JAXBContext.newInstance(SpaceMarineWithIdDto.class);
        this.unmarshaller = context.createUnmarshaller();
    }

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;


        if (req.getMethod().equalsIgnoreCase("get")) {
            if (req.getParameter("count") != null) {
                int count, page;

                try {
                    count = Integer.parseInt(req.getParameter("count"));

                    if (!(count > 0)) {
                        Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ErrorCode.INVALID_COUNT_VALUE, "Parameter 'count' must be bigger than 0");
                        return;
                    }

                    req.setAttribute("count", count);
                } catch (NumberFormatException e) {
                    Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ErrorCode.WRONG_COUNT_FORMAT, "Parameter 'count' must be integer");
                    return;
                }

                if (req.getParameter("page") != null) {
                    try {
                        page = Integer.parseInt(req.getParameter("page"));

                        if (!(page > 0)) {
                            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ErrorCode.INVALID_PAGE_VALUE, "Parameter 'page' must be bigger than 0");
                            return;
                        }

                        req.setAttribute("page", page);
                    } catch (NumberFormatException e) {
                        Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ErrorCode.WRONG_PAGE_FORMAT, "Parameter 'page' must be integer");
                        return;
                    }
                } else {
                    req.setAttribute("page", 1);
                }
            }

            if (req.getParameter("order") != null) {
                for (String order : req.getParameterValues("order")) {
                    String[] parts = order.split(",");
                    if (parts.length == 2) {
                        if (!EXPECTED_FIELDS.contains(parts[0])) {
                            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ErrorCode.UNEXPECTED_ORDER_FIELD, "Unexpected field '" + parts[0] + "' specified to order by '" + order + "'. Check documentation for details.");
                            return;
                        }
                        if (!parts[1].equals("d") && !parts[1].equals("a")) {
                            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ErrorCode.INVALID_ORDER_DIRECTION, "Order direction must be 'a' (asc) or 'd' (desc).");
                            return;
                        }
                    } else {
                        Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ErrorCode.INVALID_ORDER_FORMAT, "Order parameter has invalid format '" + order + "'. Check documentation for details.");
                        return;
                    }
                }
            }

            if (req.getParameter("filter") != null) {
                for (String filter : req.getParameterValues("filter")) {
                    String[] parts = filter.split(",");

                    if (!EXPECTED_FIELDS.contains(parts[0])) {
                        Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ErrorCode.UNEXPECTED_ORDER_FIELD, "Unexpected field '" + parts[0] + "' specified in filter '" + filter + "'. Check documentation for details.");
                        return;
                    }

                    if (parts.length != 3) {
                        Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ErrorCode.INVALID_FILTER_FORMAT, "Filter parameter has invalid format '" + filter + "'. Check documentation for details.");
                        return;
                    }

                    if (parts[0].equals("id")) {
                        try {
                            Long.parseLong(parts[2]);
                        } catch (NumberFormatException e) {
                            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ErrorCode.WRONG_ID_FILTER_FORMAT, "Field 'id' in filter must be integer");
                            return;
                        }
                        if (!(Long.parseLong(parts[2]) > 0))
                            throw new InvalidValueException(ErrorCode.WRONG_ID_FORMAT, ErrorMessage.WRONG_ID_FORMAT);
                    }

                    if (parts[0].equals("coordinates")) {
                        try {
                            Long.parseLong(parts[2]);
                        } catch (NumberFormatException e) {
                            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ErrorCode.WRONG_COORDINATES_FILTER_FORMAT, "Field 'coordinates' in filter must be integer");
                            return;
                        }
                        if (!(Long.parseLong(parts[2]) > 0))
                            throw new InvalidValueException(ErrorCode.INVALID_COORDINATES_FORMAT, ErrorMessage.INVALID_COORDINATES_FORMAT);
                    }

                    if (parts[0].equals("creationDate")) {
                        try {
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            formatter.parse(parts[2]);
                        } catch (ParseException e) {
                            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ErrorCode.WRONG_CREATION_DATE_FILTER_FORMAT, "Field 'creationDate' in filter must have format yyyy-MM-dd");
                            return;
                        }
                    }

                    if (parts[0].equals("health")) {
                        try {
                            Float.parseFloat(parts[2]);
                        } catch (NumberFormatException e) {
                            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ErrorCode.WRONG_HEALTH_FILTER_FORMAT, "Field 'health' in filter must be integer");
                            return;
                        }
                        if (!(Long.parseLong(parts[2]) > 0))
                            throw new InvalidValueException(ErrorCode.INVALID_HEALTH_VALUE, ErrorMessage.INVALID_HEALTH_VALUE);
                    }

                    if (parts[0].equals("heartCount")) {
                        try {
                            Long.parseLong(parts[2]);
                        } catch (NumberFormatException e) {
                            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ErrorCode.WRONG_HEALTH_COUNT_FILTER_FORMAT, "Field 'heartCount' in filter must be integer");
                            return;
                        }
                        if (!(Long.parseLong(parts[2]) > 3))
                            throw new InvalidValueException(ErrorCode.INVALID_HEART_COUNT_VALUE, ErrorMessage.INVALID_HEART_COUNT_VALUE);
                    }

                    if (parts[0].equals("loyal")) {
                        if (!(parts[2].equals("true") || parts[2].equals("false"))){
                            throw new InvalidValueException(ErrorCode.WRONG_LOYAL_FILTER_FORMAT, ErrorMessage.WRONG_LOYAL_FILTER_FORMAT);
                        }
                    }

                    if (parts[0].equals("category")) {
                        try {
                            AstartesCategory.valueOf(parts[2]);
                        } catch (IllegalArgumentException e) {
                            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ErrorCode.WRONG_CATEGORY_FILTER_FORMAT, "Field 'category' in filter must be one of expected value. Check documentation for details");
                            return;
                        }
                    }

                    if (parts[0].equals("chapter")) {
                        try {
                            Long.parseLong(parts[2]);
                        } catch (NumberFormatException e) {
                            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ErrorCode.WRONG_CHAPTER_FILTER_FORMAT, "Field 'chapter' in filter must be integer");
                            return;
                        }
                        if (!(Long.parseLong(parts[2]) > 0))
                            throw new InvalidValueException(ErrorCode.INVALID_CHAPTER_FORMAT, ErrorMessage.INVALID_CHAPTER_FORMAT);
                    }

                    if (!EXPECTED_ACTIONS.contains(parts[1])) {
                        Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ErrorCode.UNEXPECTED_ACTION, "Unexpected action '" + parts[0] + "' specified in filter '" + filter + "'. Check documentation for details.");
                        return;
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