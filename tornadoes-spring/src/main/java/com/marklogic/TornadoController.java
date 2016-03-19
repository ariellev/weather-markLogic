package com.marklogic;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.StringHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@RestController
@EnableAutoConfiguration
@RequestMapping("tornado/v1")
public class TornadoController {

    private DatabaseClient client;

    JSONDocumentManager docMgr;

    @Value("${tornadoes.user}")
    private String user;

    @Value("${tornadoes.password}")
    private String password;

    @Value("${tornadoes.host}")
    private String host;

    @Value("${tornadoes.port}")
    private int port;

    private static final Logger logger = LoggerFactory.getLogger(TornadoController.class);

    @PostConstruct
    private void init() {
        logger.info("init");
        logger.info("user={}, password={}, host={}, port={}", user, password, host, port);
        client = DatabaseClientFactory.newClient(host, port, user, password, DatabaseClientFactory.Authentication.DIGEST);
        docMgr = client.newJSONDocumentManager();
    }

    @PreDestroy
    private void destory() {
        logger.info("destroy");
        client.release();
    }

    @RequestMapping("/hello")
    String hello() {
        return "Hello World!";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    String getTornado(@RequestParam(value = "uri", required = true) String uri) {
        logger.info("getTornado, uri=" + uri);
        String tornado = docMgr.read(uri, new StringHandle()).get();
        return tornado;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TornadoController.class, args);
    }

}