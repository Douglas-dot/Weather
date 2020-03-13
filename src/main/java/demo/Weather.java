package demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.alibaba.fastjson.JSON;
import demo.dto.City;
import demo.util.HTTPUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

@Controller
@RequestMapping("/weather")
@Scope("prototype")
public class Weather {

    @ResponseBody
    @RequestMapping(value = "query", produces = "application/json;charset=UTF-8;")
    public String query(@RequestParam(name = "city") String city) throws IOException {
        String url = "https://api.seniverse.com/v3/weather/now.json?key=mtpmwyecaphmrzwc&location=" + city + "&language=zh-Hans&unit=c";
        return HTTPUtils.get(url);
    }

    @ResponseBody
    @RequestMapping(value = "initCity", produces = "application/json;charset=UTF-8;")
    public String initCity() throws IOException {
        List<City> citys = new ArrayList<City>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
            String classpath = this.getClass().getResource("/").getPath().replaceFirst("/", "");
            Document document = db.parse(classpath + "dictionary-configuration.xml");

            NodeList nodeList = document.getElementsByTagName("city");
            for (int i = 0; i < nodeList.getLength(); i++) {
                City city = new City();
                //Node nd = nodeList.item(i);
                //NamedNodeMap nnm = nd.getAttributes();
                Element element = (Element) nodeList.item(i);
                String code = element.getAttribute("name");
                city.setCode(code);
                String name = element.getAttribute("label");
                city.setName(name);
                citys.add(city);
            }
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        return JSON.toJSONString(citys).toString();
    }

    @RequestMapping("/report")
    public String report() {
        return "success";
    }

}