package server;

import framework.annotations.*;
import framework.request.Request;
import framework.response.JsonResponse;
import framework.response.Response;

import java.util.HashMap;
import java.util.Map;

@Controller
public class Tem {


    @Autowired(verbose = true)
    private Request request;

    @Autowired(verbose = true)
    private Request request1;

    @Autowired(verbose = true)
    private Proba1 proba1;

    public Tem() {

    }

    @Get
    @Path("/tem1")
    public Response get1(Request request){
        System.out.println(request1.toString());
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("route_location", request.getLocation());
        responseMap.put("route_method", request.getMethod().toString());
        responseMap.put("parameters", request.getParameters());
        responseMap.put("response", proba1.toString() + " " + proba1.toString1());
        System.out.println(proba1.toString());
        System.out.println(proba1.toString1());
        return new JsonResponse(responseMap);
    }
    @Post
    @Path("/tem1")
    public Response post1(Request request){
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("route_location", request.getLocation());
        responseMap.put("route_method", request.getMethod().toString());
        responseMap.put("parameters", request.getParameters());
        return new JsonResponse(responseMap);
    }
    @Get
    @Path("/")
    public Response get2(Request request){
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("route_location", request.getLocation());
        responseMap.put("route_method", request.getMethod().toString());
        responseMap.put("parameters", request.getParameters());
        return new JsonResponse(responseMap);
    }


}
