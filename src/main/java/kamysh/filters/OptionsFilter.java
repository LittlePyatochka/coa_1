package kamysh.filters;

import kamysh.dto.ChapterDto;
import kamysh.dto.SpaceMarineWithIdDto;
import kamysh.entity.AstartesCategory;
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


@WebFilter("/api/spaceMarines/*")
public class OptionsFilter implements Filter {

    public static final int UNEXPECTED_ORDER_FIELD = 500;
    public static final int INVALID_ORDER_DIRECTION = 501;
    public static final int INVALID_ORDER_FORMAT = 502;
    public static final int UNEXPECTED_ACTION = 503;
    public static final int INVALID_FILTER_FORMAT = 504;

    public static final int WRONG_ID_FILTER_FORMAT = 510;

    public static final int WRONG_COORDINATES_FILTER_FORMAT = 520;

    public static final int WRONG_CREATION_DATE_FILTER_FORMAT = 530;

    public static final int WRONG_HEALTH_FILTER_FORMAT = 540;

    public static final int WRONG_HEALTH_COUNT_FILTER_FORMAT = 550;

    public static final int WRONG_LOYAL_FILTER_FORMAT = 560;

    public static final int WRONG_CATEGORY_FILTER_FORMAT = 570;

    public static final int WRONG_CHAPTER_FILTER_FORMAT = 580;

    public static final int WRONG_COUNT_FORMAT = 590;
    public static final int INVALID_COUNT_VALUE = 591;

    public static final int WRONG_PAGE_FORMAT = 590;
    public static final int INVALID_PAGE_VALUE = 591;

    private JAXBContext context;
    private Unmarshaller unmarshaller;

    private static final List<String> EXPECTED_FIELDS = Collections.unmodifiableList(Arrays.asList("id", "name", "coordinates", "creationDate", "oscarsCount", "goldenPalmCount", "totalBoxOffice", "mpaaRating", "screenWriter"));
    private static final List<String> EXPECTED_ACTIONS = Collections.unmodifiableList(Arrays.asList("<", ">", "==", "<=", ">=", "contains"));

    @SneakyThrows
    @Override
    public void init(FilterConfig filterConfig) {
        this.context = JAXBContext.newInstance(SpaceMarineWithIdDto.class);
        this.unmarshaller = context.createUnmarshaller();
    }

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

            if (req.getParameter("order") != null) {
                for (String order : req.getParameterValues("order")) {
                    String[] parts = order.split(",");
                    if (parts.length == 2) {
                        if (!EXPECTED_FIELDS.contains(parts[0])) {
                            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, UNEXPECTED_ORDER_FIELD, "Unexpected field '" + parts[0] + "' specified to order by '" + order + "'. Check documentation for details.");
                            return;
                        }
                        if (!parts[1].equals("d") && !parts[1].equals("a")) {
                            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, INVALID_ORDER_DIRECTION, "Order direction must be 'a' (asc) or 'd' (desc).");
                            return;
                        }
                    } else {
                        Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, INVALID_ORDER_FORMAT, "Order parameter has invalid format '" + order + "'. Check documentation for details.");
                        return;
                    }
                }
            }

            if (req.getParameter("filter") != null) {
                for (String filter : req.getParameterValues("filter")) {
                    String[] parts = filter.split(",");

                    if (!EXPECTED_FIELDS.contains(parts[0])) {
                        Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, UNEXPECTED_ORDER_FIELD, "Unexpected field '" + parts[0] + "' specified in filter '" + filter + "'. Check documentation for details.");
                        return;
                    }

                    if (parts.length != 3) {
                        Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, INVALID_FILTER_FORMAT, "Filter parameter has invalid format '" + filter + "'. Check documentation for details.");
                        return;
                    }

                    if (parts[0].equals("id")) {
                        try {
                            Long.parseLong(parts[2]);
                        } catch (NumberFormatException e) {
                            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, WRONG_ID_FILTER_FORMAT, "Field 'id' in filter must be integer");
                            return;
                        }
                    }

                    if (parts[0].equals("coordinates")) {
                        try {
                            Long.parseLong(parts[2]);
                        } catch (NumberFormatException e) {
                            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, WRONG_COORDINATES_FILTER_FORMAT, "Field 'coordinates' in filter must be integer");
                            return;
                        }
                    }

                    if (parts[0].equals("creationDate")) {
                        try {
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            formatter.parse(parts[2]);
                        } catch (ParseException e) {
                            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, WRONG_CREATION_DATE_FILTER_FORMAT, "Field 'creationDate' in filter must have format yyyy-MM-dd");
                            return;
                        }
                    }

                    if (parts[0].equals("health")) {
                        try {
                            Float.parseFloat(parts[2]);
                        } catch (NumberFormatException e) {
                            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, WRONG_HEALTH_FILTER_FORMAT, "Field 'health' in filter must be integer");
                            return;
                        }
                    }

                    if (parts[0].equals("heartCount")) {
                        try {
                            Long.parseLong(parts[2]);
                        } catch (NumberFormatException e) {
                            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, WRONG_HEALTH_COUNT_FILTER_FORMAT, "Field 'heartCount' in filter must be integer");
                            return;
                        }
                    }

                    if (parts[0].equals("loyal")) {
                        try {
                            Boolean.parseBoolean(parts[2]);
                        } catch (NumberFormatException e) {
                            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, WRONG_LOYAL_FILTER_FORMAT, "Field 'loyal' in filter must be floating point number");
                            return;
                        }
                    }

                    if (parts[0].equals("category")) {
                        try {
                            AstartesCategory.valueOf(parts[2]);
                        } catch (IllegalArgumentException e) {
                            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, WRONG_CATEGORY_FILTER_FORMAT, "Field 'category' in filter must be one of expected value. Check documentation for details");
                            return;
                        }
                    }

                    if (parts[0].equals("chapter")) {
                        try {
                            Long.parseLong(parts[2]);
                        } catch (NumberFormatException e) {
                            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, WRONG_CHAPTER_FILTER_FORMAT, "Field 'chapter' in filter must be integer");
                            return;
                        }
                    }

                    if (!EXPECTED_ACTIONS.contains(parts[1])) {
                        Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, UNEXPECTED_ACTION, "Unexpected action '" + parts[0] + "' specified in filter '" + filter + "'. Check documentation for details.");
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