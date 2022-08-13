package server;

import framework.annotations.Autowired;
import framework.annotations.Component;
import framework.annotations.Service;
import framework.request.Request;

@Component
public class Proba1 {


    @Autowired(verbose = true)
    private Request request;
    @Autowired(verbose = true)
    private Request request1;
    @Autowired(verbose = true)
    private Proba2 proba2;



    public Proba1() {
    }

    @Override
    public String toString() {
        return "Proba1{}" ;
    }
    public String toString1() {
        return "Proba1{}" + request.hashCode()  +" "+ request1.hashCode() + " proba"+ proba2.toString() + proba2.toString1();
    }
}
