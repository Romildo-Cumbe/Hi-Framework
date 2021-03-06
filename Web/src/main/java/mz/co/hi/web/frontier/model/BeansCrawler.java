package mz.co.hi.web.frontier.model;


import mz.co.hi.web.HiCDI;
import mz.co.hi.web.meta.Frontier;
import mz.co.hi.web.config.AppConfigurations;

import javax.servlet.ServletException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mario Junior.
 */
public class BeansCrawler {

    private static BeansCrawler instance = null;

    private static Map<String,Boolean> ignoreMethods = new HashMap<String, Boolean>();

    static {


        Method[] methods = Blank.class.getMethods();
        for(Method method : methods){

            ignoreMethods.put(method.getName(),true);

        }


    }


    public static  BeansCrawler getInstance(){

        if(instance==null){

            instance = new BeansCrawler();

        }

        return instance;

    }

    private BeansCrawler(){



    }

    public FrontierClass[] crawl(List<Class> beansList) throws ServletException{

        FrontierClass[] beanClasses =  null;

        try {

            List<FrontierClass> beanClassList = new ArrayList<FrontierClass>();

            String simpleName = "";

            for (Class beanClass : beansList) {

                String beanClassName = beanClass.getCanonicalName();

                try {

                    beanClass = Class.forName(beanClassName.toString());
                    simpleName = beanClass.getSimpleName();

                    HiCDI.shouldHaveCDIScope(beanClass);


                    Annotation annotation = beanClass.getAnnotation(Frontier.class);

                    if(annotation!=null){


                        Frontier frontierA = (Frontier) annotation;
                        if(frontierA.name().trim().length()>0){

                            simpleName = frontierA.name().trim();

                        }

                    }



                }catch (ClassNotFoundException ex){

                    throw new ServletException("Frontier class <"+beanClassName.toString()+"> could not be found");

                }


                Constructor beanConstructor = beanClass.getConstructor(null);


                FrontierClass bean = new FrontierClass(beanClassName.toString(),simpleName);
                Method[] beanMethods = beanClass.getMethods();

                for (Method method : beanMethods) {

                    if(ignoreMethods.containsKey(method.getName())){

                        continue;

                    }


                    //Why obligate the frontiers to return Maps?
                    /*
                    if(method.getReturnType()!=Map.class){


                        continue;

                    }*/

                    String methodName = method.getName();

                    Parameter[] parameters = method.getParameters();
                    Class[] paramTypes = method.getParameterTypes();

                    FrontierMethod beanMethod = new FrontierMethod(methodName,method);

                    int i = 0;
                    for (Parameter parameter : parameters) {

                        Class paramType = paramTypes[i];
                        String paraName = parameter.getName();

                        MethodParam methodParam = new MethodParam(paraName,paramType);
                        beanMethod.addParam(methodParam);
                        i++;

                    }

                    bean.addMethod(beanMethod);


                }

                beanClassList.add(bean);


            }


            beanClasses = new FrontierClass[beanClassList.size()];
            beanClassList.toArray(beanClasses);

        }catch (NoSuchMethodException ex){

            throw new ServletException("Error during Frontier configuration process.",ex);

        }

        return beanClasses;


    }


}
