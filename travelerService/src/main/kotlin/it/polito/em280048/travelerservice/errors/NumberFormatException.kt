package it.polito.em280048.travelerservice.errors

import graphql.ErrorClassification
import graphql.GraphQLError
import graphql.language.SourceLocation
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class NumberFormatException: GraphQLError {
    var status = HttpStatus.BAD_REQUEST
    private val locations: MutableList<SourceLocation>? = null

    override fun getMessage(): String? {
        return "Bad Request"
    }

    override fun getLocations(): MutableList<SourceLocation>? {
        return locations
    }

    override fun getErrorType(): ErrorClassification? {
        return null
    }
}