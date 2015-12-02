package rda.simpleapp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rda.simpleapp.model.FsData;
import rda.simpleapp.service.FsService;
import rda.simpleapp.service.FsStatistics;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/fs")
public class FsRest {

    @Autowired
    FsService service;

    @Autowired
    FsStatistics statistics;

    @RequestMapping("/")
    public Collection<FsData> getAll() {
        return service.getAll();
    }

    @RequestMapping("/{path:.+}")
    public Collection<FsData> get(@PathVariable("path") String path) {
        return service.getByPath('/' + path);
    }

    @RequestMapping("/in/{path:.+}")
    public Collection<FsData> getIn(@PathVariable("path") String path) {
        return service.getInPath('/' + path);
    }

    @RequestMapping("/statistics")
    public Map<String, String> statistics() throws ExecutionException, InterruptedException {
        return statistics.get().get();
    }

}
