package com.hmtmcse.gs

import com.hmtmcse.gs.data.GsApiResponseData
import grails.converters.JSON
import org.grails.web.converters.Converter

class GsRestProcessor<T> implements GsExceptionHandler{

    GsRestfulService gsRestfulService
    public T gsApiDefinition
    public Boolean isDefinition = false
    public String tagName = null
    public String tagDescription = null

    public GsRestProcessor(T gsApiDefinition){
        this.gsApiDefinition = gsApiDefinition
    }


    private jsonResponseTo(GsApiResponseData gsApiResponseData){
        return render(gsApiResponseData.toMap() as JSON)
    }


    private def responseTo(GsApiResponseData gsApiResponseData){
        return jsonResponseTo(gsApiResponseData)
    }

    def gsSuccessMessage(String message){
    }

    def gsFailedMessage(String message){

    }


    def gsSuccessResponse(String message){

    }

    def gsReadResponse(GsApiActionDefinition definition) {
        return isDefinition ? definition : responseTo(gsRestfulService.gsRead(definition))
    }

    def gsReadListResponse(GsApiActionDefinition definition){}

    def gsCreateResponse(GsApiActionDefinition definition){}

    def gsUpdateResponse(GsApiActionDefinition definition){}

    def gsDeleteResponse(GsApiActionDefinition definition){}

    def list(GsApiActionDefinition definition){
        if (isDefinition){
            return definition
        }
      return render(gsRestfulService.gsReadList(definition, params) as JSON)
    }

    def renderAsJson(def data){
        if (!isDefinition){
            return render(data as JSON)
        }
    }

    def exception(String message){
        throw new GsException(message)
    }

    def exception(){
        throw new GsException()
    }

    def get(GsApiActionDefinition definition){
        JSON.registerObjectMarshaller(definition.domain){
            def output = [:]
            output['name'] = it.name
            return output
        }
        return  render(definition.domain.list() as JSON)
    }

    public void swaggerInit(){}

}
