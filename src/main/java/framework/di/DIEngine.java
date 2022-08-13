package framework.di;

import framework.annotations.*;
import framework.request.enums.Beans;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.*;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DIEngine {

    public static Map<String, Object> mapObj = new HashMap<>();
    public static Map<String, Class> dependencyContainer = new HashMap<>();
    private static Stack<Object> stack = new Stack<>();

    public static void main() throws Exception {
        setDependencyContainer();
        List<Path> paths1;
        try (Stream<Path> paths = Files.walk(Paths.get("./src"))) {
            paths1 = paths
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }

        for (Path path : paths1) {
            String[] tem;
            String tem1;
            String separator = "\\";
            StringBuilder str = new StringBuilder();
            tem = path.normalize().toString().replaceAll(Pattern.quote(separator), "\\\\").split("\\\\");
            tem1 = tem[tem.length - 1].split("\\.")[0];
            for (int i = Arrays.asList(tem).lastIndexOf("java") + 1; i < tem.length - 1; i++) {
                str.append(tem[i]).append(".");
            }
            str.append(tem1);
            Class temClass = Class.forName(str.toString());
            Object obj;
            if (temClass.isAnnotationPresent(Controller.class)) {

                if (!mapObj.containsKey(temClass.toString())) {
                    mapObj.put(temClass.toString(), temClass.getDeclaredConstructor().newInstance());
                }
                obj = mapObj.get(temClass.toString());

                java.lang.reflect.Field[] fields = temClass.getDeclaredFields();
                for (java.lang.reflect.Field field : fields) {
                    if (field.isAnnotationPresent(framework.annotations.Autowired.class)) {
                        autowire(obj, field, temClass);

                    }
                }
            }
        }


    }

    public static void autowire(Object o, Field field, Class cla) throws Exception {
        field.setAccessible(true);
        Class cl = null;
        if(field.getType().isInterface()){
            if(field.isAnnotationPresent(Qualifier.class)){
                Qualifier q = field.getAnnotation(Qualifier.class);
                if(dependencyContainer.containsKey(q.value())){
                    cl = dependencyContainer.get(q.value());
                }else{
                    throw new Exception("Interface isn't implemented");

                }
            }else{
                throw new Exception("Interface isn't annotated with Qualifier");
            }
        }else{
            cl = Class.forName(field.getType().toString().split(" ")[1]);
        }
        java.lang.reflect.Field[] fields = cl.getDeclaredFields();
        Object obj = o;
        Object obj1 = null;

        boolean singleton;
        if (cl.isAnnotationPresent(Bean.class)) {
            Bean annotation = (Bean) cl.getAnnotation(Bean.class);
            if (annotation.scope() == Beans.singleton) {
                singleton = true;
            } else {
                singleton = false;
            }
        } else if (cl.isAnnotationPresent(Service.class)) {
            singleton = true;
        } else if (cl.isAnnotationPresent(Component.class)) {
            singleton = false;
        } else {
            throw new Exception("Annotation isn't Present");
        }

        if (singleton) {
            if (!mapObj.containsKey(cl.toString())) {
                mapObj.put(cl.toString(), cl.getDeclaredConstructor().newInstance());
            }
            obj1 = mapObj.get(cl.toString());
        } else {
            obj1 = cl.getDeclaredConstructor().newInstance();
        }


        for (java.lang.reflect.Field f : fields) {
            if (f.isAnnotationPresent(framework.annotations.Autowired.class)) {
                autowire(obj1, f, cl);
            }
        }
        framework.annotations.Autowired a = field.getAnnotation(framework.annotations.Autowired.class);
        if (a.verbose()) {
            System.out.println("Initialize " + field.getType().toString() + " " + field.getName() + " in " + cla.getName() + " on "
                    + LocalDateTime.now() + " with " + obj1.hashCode());
        }


        field.set(obj, obj1);

    }

    public static void setDependencyContainer() throws Exception {
        List<Path> paths1;
        try (Stream<Path> paths = Files.walk(Paths.get("./src"))) {
            paths1 = paths
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }
        for (Path path : paths1) {
            String[] tem;
            String tem1;
            String separator = "\\";
            StringBuilder str = new StringBuilder();
            tem = path.normalize().toString().replaceAll(Pattern.quote(separator), "\\\\").split("\\\\");
            tem1 = tem[tem.length - 1].split("\\.")[0];
            for (int i = Arrays.asList(tem).lastIndexOf("java") + 1; i < tem.length - 1; i++) {
                str.append(tem[i]).append(".");
            }
            str.append(tem1);
            Class temClass = Class.forName(str.toString());
            Object obj;
            if (temClass.isAnnotationPresent(Qualifier.class)) {
                Qualifier q = (Qualifier) temClass.getAnnotation(Qualifier.class);
                if (!dependencyContainer.containsKey(q.value())) {
                    dependencyContainer.put(q.value(), temClass);
                }else{
                    throw new Exception("Qualifier already exists with that value");
                }
            }

        }

    }
}