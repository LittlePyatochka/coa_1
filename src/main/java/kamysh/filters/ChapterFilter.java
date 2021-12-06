package kamysh.filters;

import kamysh.dto.ChapterDto;
import kamysh.utils.Utils;
import lombok.SneakyThrows;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

@WebFilter("/api/chapter/*")
public class ChapterFilter implements Filter {

    public static final int INVALID_XML = 1;

    public static final int MISSING_ID = 100;
    public static final int WRONG_ID_FORMAT = 101;

    public static final int MISSING_NAME = 110;
    public static final int INVALID_NAME_VALUE = 111;

    public static final int MISSING_PARENT_LEGION = 120;
    public static final int INVALID_PARENT_LEGION = 121;

    private JAXBContext context;
    private Unmarshaller unmarshaller;

    @SneakyThrows
    @Override
    public void init(FilterConfig filterConfig) {
        this.context = JAXBContext.newInstance(ChapterDto.class);
        this.unmarshaller = context.createUnmarshaller();
    }

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        ChapterDto chapterDto = new ChapterDto();

        if (req.getMethod().equalsIgnoreCase("delete") && req.getPathInfo() == null) {
            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, MISSING_ID, "Id must be specified in the end of request");
            return;
        }
        if (req.getMethod().equalsIgnoreCase("post")) {
            try {
                chapterDto = (ChapterDto) unmarshaller.unmarshal(servletRequest.getReader());
            } catch (JAXBException e) {
                    Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, INVALID_XML, "Invalid XML in request body");
                return;
            }

            if (chapterDto.getName() == null) {
                Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, MISSING_NAME, "Field 'name' must be specified in request body");
                return;
            }
            if (chapterDto.getParentLegion() == null) {
                Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, MISSING_PARENT_LEGION, "Field 'parentLegion' must be specified in request body");
                return;
            }
            if (!(chapterDto.getName().length() > 0)) {
                Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, INVALID_NAME_VALUE, "Length of field 'name' must be bigger than 0");
                return;
            }
            if (!(chapterDto.getParentLegion().length() > 0)) {
                Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, INVALID_PARENT_LEGION, "Length of field 'parentLegion' must be bigger than 0");
                return;
            }

            req.setAttribute("chapter", chapterDto);
        }

        if (req.getMethod().equalsIgnoreCase("get") || req.getMethod().equalsIgnoreCase("delete")) {
            if (req.getPathInfo() != null) {
                try {
                    Long id = Long.valueOf(req.getPathInfo().replaceAll("^/", ""));
                    req.setAttribute("id", id);
                } catch (NumberFormatException e) {
                    Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, WRONG_ID_FORMAT, "Field 'id' must be integer");
                    return;
                }
            }
        }

        filterChain.doFilter(req, resp);
    }

    @Override
    public void destroy() {
    }
}