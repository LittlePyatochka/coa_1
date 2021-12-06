package kamysh.controller;

import kamysh.dto.CoordinatesDto;
import kamysh.dto.ResultListDto;
import kamysh.service.CoordinatesService;
import kamysh.service.CoordinatesServiceImpl;
import lombok.SneakyThrows;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/coordinates/*")
public class CoordinatesController extends HttpServlet {

    private final CoordinatesService coordinatesService;
    private final JAXBContext context;

    public CoordinatesController() throws JAXBException {
        this.coordinatesService = new CoordinatesServiceImpl();
        this.context = JAXBContext.newInstance(CoordinatesDto.class, ResultListDto.class);
    }

    @SneakyThrows
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/xml");

        PrintWriter writer = resp.getWriter();
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        Long id = (Long) req.getAttribute("id");

        if (id == null) {
            List<CoordinatesDto> results = coordinatesService.findAll();
            if (results.size() > 0) {
                ResultListDto coordinates = new ResultListDto();
                coordinates.setResults(coordinatesService.findAll());
                marshaller.marshal(coordinates, writer);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            CoordinatesDto coordinate = coordinatesService.findById(id);

            if (coordinate != null) {
                marshaller.marshal(coordinate, writer);
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
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        CoordinatesDto coordinate = (CoordinatesDto) req.getAttribute("coordinates");

        CoordinatesDto savedValue = coordinatesService.save(coordinate);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        marshaller.marshal(savedValue, writer);

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/xml");

        Long id = (Long) req.getAttribute("id");
        coordinatesService.delete(id);
    }
}
