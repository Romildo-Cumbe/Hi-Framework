package mz.co.hi.web.frontier;

import mz.co.hi.web.RequestContext;
import mz.co.hi.web.frontier.model.FrontierClass;
import mz.co.hi.web.frontier.model.FrontierMethod;
import mz.co.hi.web.frontier.model.MethodParam;
import mz.co.hi.web.meta.RestrictAccess;

import javax.validation.ConstraintViolationException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created by Mario Junior.
 */
public class FrontierInvoker {


    private RequestContext requestContext;
    private FrontierClass frontier;
    private FrontierMethod method;
    private Map params;
    private Object returnedObject;

    public FrontierInvoker(RequestContext requestContext, FrontierClass frontierClass, FrontierMethod method, Map params){

        this.requestContext = requestContext;
        this.frontier = frontierClass;
        this.method = method;
        this.params = params;

    }


    private boolean checkPermission(RestrictAccess restrictAccess){

        boolean allowed = true;

        for(String role : restrictAccess.value()){
            System.out.println("Role : "+role);
            if(!requestContext.getRequest().isUserInRole(role)){
                allowed = false;
                break;
            }

        }


        return allowed;

    }

    public boolean userHasPermission(){

        boolean accessGranted = true;

        Annotation annotationClazz = frontier.getObject().getClass().getAnnotation(RestrictAccess.class);
        if(annotationClazz!=null){

            if(!checkPermission((RestrictAccess) annotationClazz))
                return false;

        }


        Annotation annotationMethod = method.getMethod().getAnnotation(RestrictAccess.class);
        if(annotationMethod!=null)
            accessGranted = checkPermission((RestrictAccess) annotationMethod);


        return accessGranted;


    }


    public boolean invoke() throws Exception {

        MethodParam methodParams[] = method.getParams();
        Object[] invocationParams = new Object[params.size()];



        int i = 0;
        for(MethodParam methodParam: methodParams){


            invocationParams[i] = params.get(methodParam.getName());
            i++;

        }


        Object refreshedObj = frontier.getObject();

        try {

            returnedObject = method.getMethod().invoke(refreshedObj, invocationParams);


        }catch (Exception ex){

            if(ex instanceof InvocationTargetException){

                Throwable throwable = ex.getCause();

                if(throwable instanceof ConstraintViolationException){


                    throw (ConstraintViolationException) throwable;


                }

            }

        }

        return true;
    }

    public Object getReturnedObject() {

        return returnedObject;

    }
}
