package se.gu.ait.sbserver.servlet;

import java.util.List;

import se.gu.ait.sbserver.storage.JsonExporter;
import se.gu.ait.sbserver.domain.Product;

public class JsonFormatter implements Formatter {
  @Override
  public String format(List<Product> products) {
    StringBuilder json = new StringBuilder("[\n");
    JsonExporter jsonExp = new JsonExporter();
    int lastIndex = products.size() - 1;
    int index = 0;
    for (Product product : products) {
      product.export(jsonExp);
      json.append("  ");
      json.append(jsonExp.toJsonString());
      if (index != lastIndex) {
        json.append(","); // all but the last product gets a comma
      }
      index++;
      json.append("\n");
    }
    json.append("]\n");
    return json.toString();
  }
}
