package kamysh.controller;

import kamysh.dto.ChapterDto;
import kamysh.dto.CoordinatesDto;
import kamysh.dto.ResultListDto;
import kamysh.service.ChapterService;
import kamysh.service.ChapterServiceImpl;
import kamysh.utils.InvalidValueException;
import kamysh.utils.Utils;
import lombok.SneakyThrows;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/chapter/*")
public class ChapterController extends HttpServlet {

    private final ChapterService chapterService;

    public ChapterController() {
        this.chapterService = new ChapterServiceImpl();
    }

    @SneakyThrows
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/xml");

        PrintWriter writer = resp.getWriter();
        JAXBContext context = JAXBContext.newInstance(ChapterDto.class, ResultListDto.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        // сама сериализация

        Long id = (Long) req.getAttribute("id");

        if (id == null) {
            List<ChapterDto> chapter = null;

            chapter = chapterService.findAll();

            if (chapter != null) {
                ResultListDto resultListDto = new ResultListDto();
                resultListDto.setResults(chapterService.findAll());
                marshaller.marshal(resultListDto, writer);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            ChapterDto chapter = chapterService.findById(id);

            if (chapter != null) {
                marshaller.marshal(chapter, writer);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    @SneakyThrows
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/xml");

        PrintWriter writer = resp.getWriter();
        JAXBContext context = JAXBContext.newInstance(ChapterDto.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        ChapterDto chapter = (ChapterDto) req.getAttribute("chapter");

//        try {
            ChapterDto savedValue = chapterService.save(chapter);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            marshaller.marshal(chapter, writer);
//        } catch (InvalidValueException e) {
//            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getError());
//        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/xml");

        Long id = (Long) req.getAttribute("id");
        chapterService.delete(id);
    }
}
