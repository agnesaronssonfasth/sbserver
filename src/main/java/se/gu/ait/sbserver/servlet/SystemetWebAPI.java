package se.gu.ait.sbserver.servlet;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.PrintWriter;
import java.io.OutputStreamWriter;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.Locale;

import se.gu.ait.sbserver.domain.Product;
import se.gu.ait.sbserver.storage.ProductLine;
import se.gu.ait.sbserver.storage.ProductLineFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SystemetWebAPI extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(SystemetWebAPI.class.getName());

    @Override
    public void init() throws ServletException {
        Locale.setDefault(Locale.US);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("Starting up");
        request.setCharacterEncoding(UTF_8.name());
        //response.setContentType("text/html;charset="+UTF_8.name());
        response.setContentType("application/json;charset=" + UTF_8.name());
        PrintWriter out =
                new PrintWriter(new OutputStreamWriter(response.getOutputStream(),
                        UTF_8), true);
        ParameterParser paramParser;
        StringBuilder sb;
        try {
            paramParser = new ParameterParser(request.getQueryString().split("&"));

            Predicate<Product> filter = paramParser.filter();
            System.setProperty("ProductLine", getServletContext().getInitParameter("ProductLine"));
            //System.out.println("ProductLine property: " + System.getProperty("ProductLine"));
            ProductLine productLine = ProductLineFactory.getProductLine();
            List<Product> products = productLine.getProductsFilteredBy(filter);
            Formatter formatter = FormatterFactory.getFormatter();
            String json = formatter.format(products);
            sb = new StringBuilder(json);
            if (paramParser.invalidArgs().size() != 0 &&
                    products.size() == 0) {
                sb = new StringBuilder("{ \"error\": \"")
                        .append("Bad parameters: ")
                        .append(paramParser.invalidArgs().toString())
                        .append("\" }");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else if (products.size() == 0) {
                sb = new StringBuilder("{ \"error\": \"")
                        .append("No products matched the criteria ")
                        .append(request.getQueryString())
                        .append("\" }");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            out.println(sb.toString());
        } catch (NullPointerException e) {
            logger.info("No parameter passed");
        }

        out.close();
    }
    /* GOT broken pipe - TODO: investigate why - no time now */
}
