package com.hmtmcse.gs.data

import com.hmtmcse.gs.GsRelationalEntityRequest
import com.hmtmcse.gs.model.CustomRequestParamProcessor

class GsApiRequestProperty extends GsRequestResponseProperty {

    public Boolean isRequired = false
    public String errorMessage = null
    public String helpText = null
    public GsRelationalEntityRequest relationalEntity = null
    public Boolean isTypeCast = false
    public String defaultValue = null
    public CustomRequestParamProcessor customRequestParamProcessor = null


    GsApiRequestProperty() {}

    GsApiRequestProperty(String name, String alias) {
        super.name = name
        super.alias = alias
    }

    GsApiRequestProperty(String name) {
        super.name = name
    }

    Boolean getIsRequired() {
        return isRequired
    }

    GsApiRequestProperty required() {
        this.isRequired = true
        return this
    }

    void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired
    }


    String getErrorMessage() {
        return errorMessage
    }

    GsApiRequestProperty setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage
        return this
    }

    String getHelpText() {
        return helpText
    }

    GsApiRequestProperty setHelpText(String helpText) {
        this.helpText = helpText
        return this
    }

    GsApiRequestProperty setName(String name) {
        super.name = name
        return this
    }

    GsApiRequestProperty setAlias(String alias) {
        super.alias = alias
        return this
    }

    GsApiRequestProperty setDataType(String dataType) {
        super.dataType = dataType
        return this
    }

    GsApiRequestProperty setExample(String example) {
        super.example = example
        return this
    }

    GsApiRequestProperty setFormat(String format) {
        super.format = format
        return this
    }

    GsApiRequestProperty setDescription(String description) {
        super.description = description
        return this
    }

    GsApiRequestProperty setDefaultValue(String defaultValue) {
        super.defaultValue = defaultValue
        return this
    }

    String getRequestedKey() {
        return alias != null ? alias : name
    }



    Boolean getIsTypeCast() {
        return isTypeCast
    }

    GsApiRequestProperty typeCast() {
        this.isTypeCast = true
        return this
    }

    @Override
    String getDefaultValue() {
        return defaultValue
    }
}
