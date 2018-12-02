package com.hmtmcse.gs

import com.hmtmcse.gs.data.GsCriteriaData
import com.hmtmcse.gs.data.GsMapKeyValue
import com.hmtmcse.gs.data.GsParamsPairData
import com.hmtmcse.swagger.definition.SwaggerConstant
import com.hmtmcse.swagger.definition.SwaggerProperty

class GsDataFilterHandler {

    private static String inType = null

    Closure readCriteriaProcessor(GsParamsPairData gsParamsPairData, Boolean andOr = false, String details = null){
        switch (gsParamsPairData.httpMethod) {
            case GsConstant.DELETE:
            case GsConstant.POST:
                if (gsParamsPairData.params && gsParamsPairData.params.where){
                    return createCriteriaBuilder(gsParamsPairData.params.where, andOr, details)
                }
                return {}
            case GsConstant.GET:
                return readGetMethodCriteriaProcessor(gsParamsPairData)
        }
    }


    Closure readGetMethodCriteriaProcessor(GsParamsPairData gsParamsPairData) {
        Map params = gsParamsPairData.params
        if (params && params.propertyName && params.propertyValue != null) {
            return {
                eq(params.propertyName, params.propertyValue)
            }
        }
        return {}
    }


    Map readPaginationWithSortProcessor(GsParamsPairData gsParamsPairData) {
        Map refineParams = [:]
        Map params = gsParamsPairData.params
        refineParams.max = params.max ?: GsConfigHolder.itemsPerPage()
        refineParams.offset = params.offset ?: 0
        if (!gsParamsPairData.httpMethod.equals(GsConstant.GET)) {
            return refineParams
        }
        if (!params.orderProperty || !params.order) {
            refineParams.sort = GsConfigHolder.sortColumn()
            refineParams.order = GsConfigHolder.sortOrder()
        } else {
            refineParams.sort = params.orderProperty
            refineParams.order = params.order
        }
        return refineParams
    }



    public GsParamsPairData getParamsPair(Map params, Map domainFieldsType = null) {
        GsParamsPairData gsParamsPairData = new GsParamsPairData()
        gsParamsPairData.rawParams = params
        if (params.gsHttpRequestMethod) {
            switch (params.gsHttpRequestMethod.toLowerCase()) {
                case GsConstant.POST:
                    gsParamsPairData.httpMethod = GsConstant.POST
                    gsParamsPairData.params = params.gsApiData ?: [:]
                    return gsParamsPairData.initFilteredGrailsParams()
                case GsConstant.DELETE:
                    gsParamsPairData.httpMethod = GsConstant.DELETE
                    gsParamsPairData.params = params.gsApiData ?: [:]
                    return gsParamsPairData.initFilteredGrailsParams()
                case GsConstant.PUT:
                    gsParamsPairData.httpMethod = GsConstant.PUT
                    gsParamsPairData.params = params.gsApiData ?: [:]
                    return gsParamsPairData.initFilteredGrailsParams()
                case GsConstant.GET:
                    gsParamsPairData.httpMethod = GsConstant.GET
                    gsParamsPairData.params = domainFieldsType == null ? params : GsReflectionUtil.castFromDomainSwaggerMap(params, domainFieldsType)
                    return gsParamsPairData.initFilteredGrailsParams()
            }
        }
        return gsParamsPairData
    }


    public static GsDataFilterHandler instance(){
        return new GsDataFilterHandler()
    }





    public Closure buildCriteria(GsCriteriaData criteriaData){

        Closure criteria = {

        }
        return criteria
    }


    public Closure createCriteriaBuilder(Map where, Boolean andOr = false, String details = null) {
        GsMapKeyValue gsMapKeyValue
        Closure criteria = {
            def keyValueCriteriaBuilder = { Map nestedWhere ->
                gsMapKeyValue = getMapKeyValue(nestedWhere, GsConstant.EQUAL)
                if (gsMapKeyValue) {
                    eq(gsMapKeyValue.key, gsMapKeyValue.value)
                }

                if (details == null){
                    gsMapKeyValue = getMapKeyValue(nestedWhere, GsConstant.NOT_EQUAL)
                    if (gsMapKeyValue) {
                        ne(gsMapKeyValue.key, gsMapKeyValue.value)
                    }

                    gsMapKeyValue = getMapKeyValue(nestedWhere, GsConstant.LESS_THAN)
                    if (gsMapKeyValue) {
                        lt(gsMapKeyValue.key, gsMapKeyValue.value)
                    }


                    gsMapKeyValue = getMapKeyValue(nestedWhere, GsConstant.LESS_THAN_EQUAL)
                    if (gsMapKeyValue) {
                        le(gsMapKeyValue.key, gsMapKeyValue.value)
                    }


                    gsMapKeyValue = getMapKeyValue(nestedWhere, GsConstant.GETTER_THAN)
                    if (gsMapKeyValue) {
                        gt(gsMapKeyValue.key, gsMapKeyValue.value)
                    }


                    gsMapKeyValue = getMapKeyValue(nestedWhere, GsConstant.GETTER_THAN_EQUAL)
                    if (gsMapKeyValue) {
                        ge(gsMapKeyValue.key, gsMapKeyValue.value)
                    }

                    gsMapKeyValue = getMapKeyValue(nestedWhere, GsConstant.LIKE)
                    if (gsMapKeyValue) {
                        like(gsMapKeyValue.key, gsMapKeyValue.value)
                    }
                }
            }

            gsMapKeyValue = getMapKeyValue(where, GsConstant.ORDER)
            if (gsMapKeyValue && (gsMapKeyValue.value.equals(GsConstant.ASC) || gsMapKeyValue.value.equals(GsConstant.DESC))) {
                order(gsMapKeyValue.key, gsMapKeyValue.value)
            }else {
                order(GsConfigHolder.sortColumn(), GsConfigHolder.sortOrder())
            }

            if (where[GsConstant.SELECT] || where[GsConstant.COUNT]) {
                projections {
                    if (where[GsConstant.COUNT]){
                        count()
                    }else if (where[GsConstant.SELECT]){
                        where[GsConstant.SELECT]?.each {
                            property(it)
                        }
                    }
                }
            }

            if (where[GsConstant.AND]) {
                and {
                    where[GsConstant.AND].each {
                        keyValueCriteriaBuilder.call(it)
                    }
                }
            }

            if (where[GsConstant.OR]) {
                or {
                    where[GsConstant.OR].each {
                        keyValueCriteriaBuilder.call(it)
                    }
                }
            }
            keyValueCriteriaBuilder.call(where)
        }
        return criteria
    }


    GsMapKeyValue getMapKeyValue(Map map, String key){
        GsMapKeyValue gsMapKeyValue = new GsMapKeyValue()
        if (!map[key]){
            return null
        }
        map = map[key]
        map.keySet().each {
            gsMapKeyValue.key = it
        }
        gsMapKeyValue.value = map.get(gsMapKeyValue.key)
        return gsMapKeyValue
    }

    public Closure conditonBuilder(Map params){
        Closure conditions = {
            if (params[GsConstant.SELECT]){
            }
        }
        return conditions
    }


    public static SwaggerProperty getRequestParams(Boolean isList = true, Boolean isJson = false , List allowedProperty = []){
        SwaggerProperty swaggerProperty = new SwaggerProperty()
        if (isList){
            swaggerProperty.property(GsConstant.OFFSET, SwaggerConstant.SWAGGER_DT_INTEGER).addToListWithType(inType)
            swaggerProperty.property(GsConstant.MAX, SwaggerConstant.SWAGGER_DT_INTEGER).addToListWithType(inType)
            if (!isJson){
                swaggerProperty.property(GsConstant.ORDER_PROPERTY, SwaggerConstant.SWAGGER_DT_STRING).addToListWithType(inType)
                swaggerProperty.property(GsConstant.ORDER, SwaggerConstant.SWAGGER_DT_STRING).addToListWithType(inType)
            }
        }
        if (!isJson){
            swaggerProperty.property(GsConstant.PROPERTY_NAME, SwaggerConstant.SWAGGER_DT_STRING).addToListWithType(inType)
            swaggerProperty.property(GsConstant.PROPERTY_VALUE, SwaggerConstant.SWAGGER_DT_STRING).addToListWithType(inType)

            if (allowedProperty.size()) {
                swaggerProperty.otherProperty(GsConstant.ALLOWED_PROPERTY, allowedProperty?.name?.join(", ")).addToListWithType(inType)
            }
        }
        return swaggerProperty
    }


    public static SwaggerProperty swaggerWhere(Boolean isList = true, List allowedProperty = []) {
        SwaggerProperty property = new SwaggerProperty()
        property.property(GsConstant.PROPERTY_NAME, SwaggerConstant.SWAGGER_DT_STRING)
                .example(GsConstant.PROPERTY_VALUE)
        if (allowedProperty.size()) {
            property.otherProperty(GsConstant.ALLOWED_PROPERTY, allowedProperty?.name?.join(", "))
        }

        SwaggerProperty conditions = new SwaggerProperty()
        String name = GsConstant.EQUAL
        name += "|" + GsConstant.AND
        name += "|" + GsConstant.OR


        SwaggerProperty where = new SwaggerProperty().property(GsConstant.WHERE)
        if (isList){
            where = getRequestParams(isList, true)

            name += "|" + GsConstant.ORDER
//            name += "|" + GsConstant.BETWEEN
            name += "|" + GsConstant.NOT_EQUAL
            name += "|" + GsConstant.LESS_THAN
            name += "|" + GsConstant.LESS_THAN_EQUAL
            name += "|" + GsConstant.GETTER_THAN
            name += "|" + GsConstant.GETTER_THAN_EQUAL
//            name += "|" + GsConstant.IN_LIST
            name += "|" + GsConstant.LIKE
        }else{
            where.description(GsConfigHolder.multipleMatchInDetails())
        }

        conditions.objectProperty(name, property)
        where.objectProperty(GsConstant.WHERE, conditions)
        return where
    }



    public static SwaggerProperty swaggerPostReadRequest(Boolean isList = true, List allowedProperty = []) {
        return swaggerWhere(isList, allowedProperty)
    }

    public static SwaggerProperty swaggerGetReadRequest(Boolean isList = true, List allowedProperty = []) {
        inType = SwaggerConstant.IN_QUERY
        return getRequestParams(isList, false, allowedProperty)
    }


    public static SwaggerProperty swaggerPostWriteRequest(List allowedProperty = []) {
        return swaggerWhere(false, allowedProperty)
    }

}
