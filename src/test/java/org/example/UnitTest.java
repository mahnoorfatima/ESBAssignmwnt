package org.example;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
public class UnitTest extends CamelTestSupport {

    @Test
    public void testCsvUnmarshal() throws Exception {
        final String request = "Germany,Berlin,3.759min\n" +
                "Germany,Hamburg,1.822min\n" +
                "Germany,Munich,1.472min\n" +
                "Netherland,Amsterdam,0.821min\n" +
                "Netherland,Utrect,1.307min\n" +
                "Netherland,Rotterdam,0.623min\n";

        @SuppressWarnings("unchecked")
        final List<City> response = Collections.checkedList(template.requestBody("direct:unmarshal", request, List.class), City.class);
        City city1 = new City();
        city1.setCountry("Germany");
        city1.setName("Hamburg");
        city1.setPopulation("1.822min");

        City city2 = new City();
        city2.setCountry("Germany");
        city2.setName("Munich");
        city2.setPopulation("1.472min");

        City city3 = new City();
        city3.setCountry("Netherland");
        city3.setName("Amsterdam");
        city3.setPopulation("0.821min");

        City city4 = new City();
        city4.setCountry("Netherland");
        city4.setName("Utrect");
        city4.setPopulation("1.307min");

        City city5 = new City();
        city5.setCountry("Netherland");
        city5.setName("Rotterdam");
        city5.setPopulation("0.623min");

        City response1 = response.get(0);
        assertEquals(city1, response1);

        City response2 = response.get(1);
        assertEquals(city2, response2);
    }

    @Test
    public void testCsvMarshal() throws Exception {
        ArrayList<City> cities = new ArrayList<City>();

        City city1 = new City();
        city1.setCountry("Germany");
        city1.setName("Hamburg");
        city1.setPopulation("1.822min");

        City city2 = new City();
        city2.setCountry("Germany");
        city2.setName("Munich");
        city2.setPopulation("1.472min");


        City city5 = new City();
        city5.setCountry("Netherland");
        city5.setName("Rotterdam");
        city5.setPopulation("0.623min");

        cities.add(city1);
        cities.add(city2);
        cities.add(city5);

        final String response = template.requestBody("direct:marshal", cities, String.class);

        log.info(response);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<list>\n" +
                "    <city name=Hamburg country=Germany>\n" +
                "        <population>1.822min</population>\n" +
                "    </city>\n" +
                        "    <city name=Munich country=Germany>\n" +
                "        <population>1.472min</population>\n" +
                "    </city>\n" +
                        "    <city name=Rotterdam country=Netherland>\n" +
                "        <population>0.623min</population>\n" +
                "    </city>\n" +
                "</list>\n", response);
    }

    @Test
    public void testSplitAggregatesResponses() throws Exception {
        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.expectedMessageCount(2);

        String filename = "";
        assertFileExists(filename);
        InputStream booksStream = new FileInputStream(filename);

        template.sendBody("direct:in", booksStream);

        assertMockEndpointsSatisfied();
        List<Exchange> receivedExchanges = mockOut.getReceivedExchanges();
        assertCityByCountry(receivedExchanges.get(0));
        assertCityByCountry(receivedExchanges.get(1));
    }

    private void assertCityByCountry(Exchange exchange) {
        Message in = exchange.getIn();
        @SuppressWarnings("unchecked")
        Set<String> cities = Collections.checkedSet(in.getBody(Set.class), String.class);
        String category = in.getHeader("country", String.class);
        switch (category) {
            case "Germany":
                assertTrue(cities.containsAll(Collections.singletonList("Hamburg,Munich,Berlin")));
                break;
            case "Netherland":
                assertTrue(cities.containsAll(Arrays.asList("Amsterdam,Utrecht,Rotterdam")));
                break;
            default:
                fail();
                break;
        }
    }

}
