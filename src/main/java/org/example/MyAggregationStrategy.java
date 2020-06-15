package org.example;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MyAggregationStrategy implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        String body = newExchange.getIn().getBody(String.class);
        if (oldExchange == null) {
            Set<String> set = new HashSet<String>();
            set.add(body);
            newExchange.getIn().setBody(set);
            return newExchange;
        } else {
            @SuppressWarnings("unchecked")
            Set<String> set = Collections.checkedSet(oldExchange.getIn().getBody(Set.class), String.class);
            set.add(body);
            return oldExchange;
        }
    }
}

