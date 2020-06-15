package org.example;

import com.thoughtworks.xstream.XStream;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.dataformat.xstream.XStreamDataFormat;
import org.apache.camel.spi.DataFormat;

/**
 * A Camel Java DSL Router
 */
public class MyRouteBuilder extends RouteBuilder {

    public void configure() {
        final DataFormat bindy = new BindyCsvDataFormat(City.class);
        XStream xStream = new XStream();
        xStream.alias("city", City.class);
        xStream.useAttributeFor(City.class, "name");
        xStream.useAttributeFor(City.class, "country");
        from("file:src/data?fileName=cities.csv&noop=true")
                .unmarshal(bindy)
                .log("csv converted to pojo")
                    .to("direct:marshal");

        from("direct:marshal")
                .marshal(new XStreamDataFormat(xStream))
                .log("pojo to xml")
                .to("direct:split-aggregate");

        from("direct:split-aggregate")
                .split(xpath("/list/city"))
                .setHeader("country", xpath("/city/@country"))
                .aggregate(header("country"), new MyAggregationStrategy()).completionTimeout(500)
               .process(new MyProcessor())
                .to("file:target/cities?fileName=${header.country}.xml&noop=true")
                .log("Successfully created files");

    }

}
