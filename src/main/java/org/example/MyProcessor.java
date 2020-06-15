package org.example;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class MyProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String payload = exchange.getIn().getBody(String.class);
       String str =  payload.replace("[", "").replace("]", "");
        String[] commaSeparator =  str.split(",");
        StringBuffer sb = new StringBuffer();
        sb.append("<cities>");
        for (String lineData : commaSeparator)
        {
            sb.append(lineData);
        }
        sb.append("</cities>");
        exchange.getIn().setBody(sb.toString());
    }
}
