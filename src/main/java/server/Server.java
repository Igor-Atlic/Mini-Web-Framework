package server;

import framework.annotations.Controller;
import framework.annotations.Get;
import framework.di.DIEngine;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Server {

    public static final int TCP_PORT = 8080;
    public static Map<String, java.lang.reflect.Method> map = new HashMap<>();



    public static void main(String[] args) throws IOException {

        try {
            DIEngine.main();
            ServerSocket serverSocket = new ServerSocket(TCP_PORT);
            System.out.println("Server is running at http://localhost:"+TCP_PORT);
            List<Path> paths1;
            try (Stream<Path> paths = Files.walk(Paths.get("./src"))) {
                paths1 = paths
                        .filter(Files::isRegularFile)
                        .collect(Collectors.toList());
            }

            for (Path path: paths1 ) {
                String[] tem;
                String tem1;
                String separator = "\\";
                StringBuilder str = new StringBuilder();
                tem = path.normalize().toString().replaceAll(Pattern.quote(separator), "\\\\").split("\\\\");
                tem1 = tem[tem.length-1].split("\\.")[0];
                for (int i = Arrays.asList(tem).lastIndexOf("java")+1; i<tem.length-1; i++){
                    str.append(tem[i]+ ".");
                }
                str.append(tem1);
                Class temClass = Class.forName(str.toString());
                if (temClass.isAnnotationPresent(Controller.class)){
                    java.lang.reflect.Method[] methods = temClass.getDeclaredMethods();
                    for (java.lang.reflect.Method met: methods) {
                        if(met.isAnnotationPresent(framework.annotations.Path.class)){
                            framework.annotations.Path p = met.getAnnotation(framework.annotations.Path.class);
                            if (met.isAnnotationPresent(Get.class)){
                                if (map.containsKey("GET " + p.value())){
                                    throw new Exception("Path already exists");
                                }
                                map.put("GET " + p.value(), met);
                            }else{
                                if (map.containsKey("POST " + p.value())){
                                    throw new Exception("Path already exists");
                                }
                                map.put("POST " + p.value(), met);
                            }
                        }
                    }
                }
            }

            for (Map.Entry<String, Method> me : map.entrySet()) {
                System.out.print(me.getKey() + " : ");
                System.out.println(me.getValue());
            }

            while(true){
                Socket socket = serverSocket.accept();
                new Thread(new ServerThread(socket)).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
