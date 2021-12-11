package kamysh.controller;

import kamysh.utils.*;
import lombok.SneakyThrows;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.IOException;

public class ErrorController extends HttpServlet {

    public ErrorController() {
    }

    @SneakyThrows
    protected void handleJaxBException(HttpServletResponse resp) {
        Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ErrorCode.INVALID_XML, ErrorMessage.INVALID_XML);
    }

    @SneakyThrows
    protected void handleInvalidValueException(HttpServletResponse resp, InvalidValueException e) {
        Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getErrorCode(), e.getMessage());
    }

    @SneakyThrows
    protected void handleMissingEntityException(HttpServletResponse resp, MissingEntityException e) {
        Utils.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getErrorCode(), e.getMessage());
    }

    @SneakyThrows
    protected void handleDefaultException(HttpServletResponse resp, Throwable e) {
        Utils.writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    protected void handleError(HttpServletResponse resp, Throwable throwable) {
        if (throwable instanceof JAXBException) handleJaxBException(resp);
        else if (throwable instanceof InvalidValueException) handleInvalidValueException(resp, (InvalidValueException) throwable);
        else if (throwable instanceof MissingEntityException) handleMissingEntityException(resp, (MissingEntityException) throwable);
        else handleDefaultException(resp, throwable);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Throwable throwable = (Throwable) req.getAttribute("javax.servlet.error.exception");
        handleError(resp, throwable);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
}
