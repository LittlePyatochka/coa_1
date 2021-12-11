package kamysh.controller;

import kamysh.dto.*;
import kamysh.repository.SpaceMarineRepository;
import kamysh.repository.SpaceMarineRepositoryImpl;
import kamysh.service.SpaceMarineService;
import kamysh.service.SpaceMarineServiceImpl;
import kamysh.utils.InvalidValueException;
import kamysh.utils.Utils;
import kamysh.utils.MissingEntityException;
import lombok.SneakyThrows;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

@WebServlet("/api/space-marine/*")
public class SpaceMarineController extends HttpServlet {

    private final SpaceMarineService spaceMarineService;


    public SpaceMarineController() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(CoordinatesDto.class, ResultListDto.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        this.spaceMarineService = new SpaceMarineServiceImpl();
    }

    @SneakyThrows
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/xml");

        PrintWriter writer = resp.getWriter();
        JAXBContext context = JAXBContext.newInstance(SpaceMarineDto.class, ResultListDto.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        Long id = (Long) req.getAttribute("id");

        if (req.getPathInfo() != null && req.getPathInfo().equals("/heartCount/min")) {
            SpaceMarineDto spaceMarineDto = spaceMarineService.getMinHealthCount();
            marshaller.marshal(spaceMarineDto, writer);

        } else if (id == null) {
            FilterConfiguration filterConfiguration = new FilterConfiguration();
            if (req.getParameter("count") != null) {
                filterConfiguration.setCount(Integer.parseInt(req.getParameter("count")));
                if(req.getParameter("page") != null){
                    filterConfiguration.setPage(Integer.parseInt(req.getParameter("page")));
                }
            }

            if (req.getParameter("order") != null) {
                filterConfiguration.setOrder(Arrays.asList(req.getParameterValues("order")));
            }

            if (req.getParameter("filter") != null) {
                filterConfiguration.setFilter(Arrays.asList(req.getParameterValues("filter")));
            }

            List<SpaceMarineDto> spaceMarines = null;
            try {
                spaceMarines = spaceMarineService.findAll(filterConfiguration);
            } catch (ParseException e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }

            if (spaceMarines != null) {
                ResultListDto resultListDto = new ResultListDto();
                resultListDto.setResults(spaceMarineService.findAll(filterConfiguration));
                marshaller.marshal(resultListDto, writer);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            SpaceMarineDto spaceMarine = spaceMarineService.findById(id);

            if (spaceMarine != null) {
                marshaller.marshal(spaceMarine, writer);
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
        JAXBContext context = JAXBContext.newInstance(SpaceMarineWithIdDto.class, SpaceMarineDto.class, ResultListDto.class, HealthCountDto.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        if (req.getPathInfo() != null && req.getPathInfo().equals("/health/count")) {
            Integer h = Integer.parseInt(req.getAttribute("countHealth").toString());

            marshaller.marshal(spaceMarineService.getHealthCount(h), writer);
        } else {
            SpaceMarineWithIdDto spaceMarine = (SpaceMarineWithIdDto) req.getAttribute("spaceMarine");

            SpaceMarineDto savedValue = spaceMarineService.saveOrUpdate(spaceMarine);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            marshaller.marshal(savedValue, writer);
        }
    }

    @SneakyThrows
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            resp.setContentType("application/xml");

            PrintWriter writer = resp.getWriter();
            JAXBContext context = JAXBContext.newInstance(SpaceMarineWithIdDto.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            SpaceMarineWithIdDto spaceMarine = (SpaceMarineWithIdDto) req.getAttribute("spaceMarine");

            marshaller.marshal(spaceMarineService.saveOrUpdate(spaceMarine), writer);

        } catch (MissingEntityException e) {
            Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getError());
        }
    }

    @SneakyThrows
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/xml");

        PrintWriter writer = resp.getWriter();
        JAXBContext context = JAXBContext.newInstance(SpaceMarineDto.class, ResultListDto.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        if (req.getPathInfo() != null && req.getPathInfo().equals("/loyal")) {
            resp.setContentType("application/xml");

            Boolean status = spaceMarineService.deleteLoyal(Boolean.parseBoolean(req.getAttribute("loyal").toString()));
            marshaller.marshal(status, writer);
        } else {
            Long id = (Long) req.getAttribute("id");
            spaceMarineService.delete(id);
        }
    }
}
