package mz.co.hi.web.mvc;

import javax.servlet.ServletException;

/**
 * Created by Mario Junior.
 */
public class ConversionFailedException extends MvcException {

    public ConversionFailedException(String controller, String action,Throwable throwable){

        super("Conversion of java.util.Map object passed to view from action <"+action+"> of controller <"+controller+"> to JSON failed.",throwable);

    }

}