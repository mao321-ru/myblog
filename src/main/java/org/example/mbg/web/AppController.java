package org.example.mbg.web;

import java.util.Date;
import java.util.logging.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class AppController {
    private final static Logger log = Logger.getLogger( org.example.mbg.web.AppController.class.getName());

    @GetMapping("/")
    public ModelAndView defaultPage() {
        Date date = new Date();
        log.info( "request date: " + date);

        ModelAndView model = new ModelAndView();
        model.setViewName("index");
        model.addObject( "generatedTime", date);
        return model;
    }

}
