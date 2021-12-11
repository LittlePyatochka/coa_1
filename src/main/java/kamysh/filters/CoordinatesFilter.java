package kamysh.filters;

import com.google.gson.*;
import kamysh.dto.CoordinatesDto;
import kamysh.utils.ErrorCode;
import kamysh.utils.Utils;
import lombok.SneakyThrows;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


@WebFilter("/api/coordinates/*")
public class CoordinatesFilter implements Filter {

    public static final int INVALID_XML = 1;

    public static final int MISSING_ID = 100;
    public static final int WRONG_ID_FORMAT = 101;

    public static final int MISSING_X = 110;
    public static final int MISSING_Y = 120;

    private JAXBContext context;
    private Unmarshaller unmarshaller;

    @SneakyThrows
    @Override
    public void init(FilterConfig filterConfig) {
        this.context = JAXBContext.newInstance(CoordinatesDto.class);
        this.unmarshaller = context.createUnmarshaller();
    }

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse, FilterChain filterChain){
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        CoordinatesDto coordinatesDto = new CoordinatesDto();

        if (req.getMethod().equalsIgnoreCase("delete") && req.getPathInfo() == null) {
            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ErrorCode.MISSING_ID, "Id must be specified in the end of request");
            return;
        }
        if (req.getMethod().equalsIgnoreCase("post")) {
            try {
                coordinatesDto = (CoordinatesDto) unmarshaller.unmarshal(servletRequest.getReader());
            } catch (JAXBException e) {
                Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ErrorCode.INVALID_XML, "Invalid XML in request body");
                return;
            }
            if (coordinatesDto.getX() == null) {
                Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ErrorCode.MISSING_X, "Field 'x' must be specified in request body");
                return;
            }
            if (coordinatesDto.getY() == null) {
                Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ErrorCode.MISSING_Y, "Field 'y' must be specified in request body");
                return;
            }
            req.setAttribute("coordinates", coordinatesDto);
        }

        if (req.getMethod().equalsIgnoreCase("get") || req.getMethod().equalsIgnoreCase("delete")) {
            if (req.getPathInfo() != null) {
                try {
                    Long id = Long.valueOf(req.getPathInfo().replaceAll("^/", ""));
                    req.setAttribute("id", id);
                } catch (NumberFormatException e) {
                    Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ErrorCode.WRONG_ID_FORMAT, "Field 'id' must be integer");
                    return;
                }
            }
        }

        filterChain.doFilter(req, resp);
    }

    @Override
    public void destroy() {}
}