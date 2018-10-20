package com.hmtmcse.gs

import com.hmtmcse.gs.data.GsApiResponseData
import com.hmtmcse.gs.data.GsApiResponseProperty
import grails.converters.JSON


class GsRestfulService {


    GsApiResponseData gsRead(GsApiActionDefinition definition){
        return GsApiResponseData.failed("Failed")
    }


    def gsReadList(GsApiActionDefinition definition, Map params){
        GsApiResponseData responseData = GsApiResponseData.failed(GsConfigHolder.failedMessage())
        try{
            responseData.isSuccess = true
            registerJsonMarshaller(definition)
            params = processListParamsData(params)
            Map response = [:]
            response.total = definition.domain.createCriteria().count(){}
            response.data = definition.domain.createCriteria().list(params){}
            responseData = GsApiResponseData.successResponse(response)
        }catch(Exception e){
            println(e.getMessage())
            responseData = GsApiResponseData.failed(GsConfigHolder.failedMessage())
        }
        return responseData.toMap()
    }


    def resolveCondition(){

    }


    def gsCreate(GsApiActionDefinition definition, Map params){

    }

    def gsUpdate(GsApiActionDefinition definition, Map params){}

    def gsDelete(GsApiActionDefinition definition, Map params){}

    def gsDetails(GsApiActionDefinition definition, Map params){}

    def gsCustomQuery(GsApiActionDefinition definition, Map params){}

    def gsCustomQueryAndResponse(GsApiActionDefinition definition, Map params){}


    private void registerJsonMarshaller(GsApiActionDefinition definition){
        JSON.registerObjectMarshaller(definition.domain){
            return mapDomainDataToDefinition(it, definition.getResponseProperties())
        }
    }

    private Map processListParamsData(Map params){
        Map refineParams = [:]
        refineParams.max = params.max ?: GsConfigHolder.itemsPerPage()
        refineParams.offset = params.offset ?: 0
        if (!params.sort) {
            refineParams.sort = GsConfigHolder.sortColumn()
            refineParams.order = GsConfigHolder.sortOrder()
        }
        return refineParams
    }


    private Map mapDomainDataToDefinition(def domain, Map<String, GsApiResponseProperty> responseProperties){
        def map = [:]
        responseProperties.each {String key, GsApiResponseProperty property ->
            map.put(property.getMapKey(), valueFromDomain(key, domain, property))
        }
        return map
    }


    private def valueFromDomain(String key, def domain, GsApiResponseProperty gsApiResponseProperty){
        try{
          return domain[key]
        }catch(Exception e){
            return gsApiResponseProperty.getDefaultValue()
        }
    }
}
