package server;

import framework.annotations.Autowired;
import framework.annotations.Component;
import framework.annotations.Qualifier;
import framework.request.Request;

@Component
public class Proba2 {

    @Autowired(verbose = true)
    private Request request;
    @Autowired(verbose = true)
    private Request request1;
    @Autowired(verbose = true)
    @Qualifier(value = "Interfejs")
    private IProba iProba;
    @Autowired(verbose = true)
    @Qualifier(value = "Interfejs")
    private IProba iProba2;
    public Proba2() {
    }

    @Override
    public String toString() {
        return "Proba2{}";
    }
    public String toString1() {
        return "Proba2{}" + request.hashCode() + " : "+request1.hashCode() + " Int: " + iProba.str() + " " + iProba.hashCode() + ", " + iProba2.hashCode();
    }
}

