package server;

import framework.annotations.Qualifier;
import framework.di.DIEngine;
import framework.response.JsonResponse;
import framework.response.Response;
import framework.request.enums.Method;
import framework.request.Header;
import framework.request.Helper;
import framework.request.Request;
import framework.request.exceptions.RequestNotValidException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerThread implements Runnable{

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ServerThread(Socket socket){
        this.socket = socket;

        try {
            in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        try {

            Request request = this.generateRequest();
            if(request == null) {
                in.close();
                out.close();
                socket.close();
                return;
            }

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("route_location", request.getLocation());
            responseMap.put("route_method", request.getMethod().toString());
            responseMap.put("parameters", request.getParameters());
            Response response = new JsonResponse(responseMap);


            if(request.getLocation().contains("?")){
                if (Server.map.containsKey(request.getMethod()+" " + request.getLocation().split("\\?")[0])){
                    String[] tem = Server.map.get(request.getMethod()+" " + request.getLocation().split("\\?")[0]).toString().split(" ");
                    String[] tem2 = tem[tem.length-1].split("\\.");
                    Class cl = Class.forName(tem2[0] + "." + tem2[1]);
                    Object obj;
                    if(cl.isAnnotationPresent(Qualifier.class)){
                        Qualifier q = (Qualifier) cl.getAnnotation(Qualifier.class);
                        if (!DIEngine.mapObj.containsKey(q.value())){
                            DIEngine.mapObj.put(q.value(), cl.getDeclaredConstructor().newInstance());
                        }
                        obj = DIEngine.mapObj.get(q.value());
                    }else{
                        if (!DIEngine.mapObj.containsKey(cl.toString())){
                            DIEngine.mapObj.put(cl.toString(), cl.getDeclaredConstructor().newInstance());
                        }
                        obj = DIEngine.mapObj.get(cl.toString());
                    }

                    out.println(((Response) Server.map.get(request.getMethod()+" " + request.getLocation().split("\\?")[0]).invoke(obj, request)).render());
                }else {
                    System.out.println("eror ?");
                    out.println(response.render());
                }
            }else{
                if (Server.map.containsKey(request.getMethod()+" " + request.getLocation())){
                    String[] tem = Server.map.get(request.getMethod()+" " + request.getLocation()).toString().split(" ");
                    String[] tem2 = tem[tem.length-1].split("\\.");
                    Class cl = Class.forName(tem2[0] + "." + tem2[1]);
                    Object obj;
                    if(cl.isAnnotationPresent(Qualifier.class)){
                        Qualifier q = (Qualifier) cl.getAnnotation(Qualifier.class);
                        /*if (!DIEngine.mapObj.containsKey(q.value())){
                            DIEngine.mapObj.put(q.value(), cl.getDeclaredConstructor().newInstance());
                        }
                        obj = DIEngine.mapObj.get(q.value());*/
                        if (!DIEngine.dependencyContainer.containsKey(q.value())){
                            DIEngine.dependencyContainer.put(q.value(), cl);
                        }
                        Class c = DIEngine.dependencyContainer.get(q.value());
                        if (!DIEngine.mapObj.containsKey(c.toString())){
                            DIEngine.mapObj.put(c.toString(), c.getDeclaredConstructor().newInstance());
                        }
                        obj = DIEngine.mapObj.get(c.toString());
                    }else{
                        if (!DIEngine.mapObj.containsKey(cl.toString())){
                            DIEngine.mapObj.put(cl.toString(), cl.getDeclaredConstructor().newInstance());
                        }
                        obj = DIEngine.mapObj.get(cl.toString());
                    }
                    out.println(((Response) Server.map.get(request.getMethod()+" " + request.getLocation()).invoke(obj, request)).render());
                }else {
                    System.out.println("eror");
                    out.println(response.render());
                }
            }


            in.close();
            out.close();
            socket.close();

        } catch (IOException | RequestNotValidException | IllegalAccessException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private Request generateRequest() throws IOException, RequestNotValidException {
        String command = in.readLine();
        if(command == null) {
            return null;
        }

        String[] actionRow = command.split(" ");
        Method method = Method.valueOf(actionRow[0]);
        String route = actionRow[1];
        Header header = new Header();
        HashMap<String, String> parameters = Helper.getParametersFromRoute(route);

        do {
            command = in.readLine();
            String[] headerRow = command.split(": ");
            if(headerRow.length == 2) {
                header.add(headerRow[0], headerRow[1]);
            }
        } while(!command.trim().equals(""));

        if(method.equals(Method.POST)) {
            int contentLength = Integer.parseInt(header.get("content-length"));
            char[] buff = new char[contentLength];
            in.read(buff, 0, contentLength);
            String parametersString = new String(buff);

            HashMap<String, String> postParameters = Helper.getParametersFromString(parametersString);
            for (String parameterName : postParameters.keySet()) {
                parameters.put(parameterName, postParameters.get(parameterName));
            }
        }

        Request request = new Request(method, route, header, parameters);

        return request;
    }
}
