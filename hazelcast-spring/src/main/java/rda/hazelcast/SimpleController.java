package rda.hazelcast;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleController {

    @Autowired
    SimpleService simpleService;

    @RequestMapping("/")
    private String home() {
        simpleService.evict();
        return "evict all";
    }

    @RequestMapping("/get/{get}")
    private String get(@PathVariable String get) {
        return String.valueOf(simpleService.getWithCache(get)) + ' ' + simpleService.get(get);
    }

    @RequestMapping("/set/{set}")
    private String set(@PathVariable String set) {
        Integer put = simpleService.put(set);
        return "set: " + set + " -> " + put;
    }

}
