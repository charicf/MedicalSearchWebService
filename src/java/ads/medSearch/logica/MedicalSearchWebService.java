/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ads.medSearch.logica;

import java.util.ArrayList;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author Charic D. Farinango
 */
@WebService(serviceName = "MedicalSearchWebService")
public class MedicalSearchWebService {

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "getMedicalTopics")
    public ArrayList<String> getMedicalTopics(@WebParam(name = "keyword") String keyword) {
        return MedicalSearch.getMedicalTopics(keyword);
    }
}
