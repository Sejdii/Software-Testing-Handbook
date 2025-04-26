package pl.sejdii.user.application.service

import pl.sejdii.user.application.domain.User
import pl.sejdii.user.application.domain.UserIdentifier
import pl.sejdii.user.application.port.in.CreateUserUseCase
import pl.sejdii.user.application.port.out.InsertUserPort
import spock.lang.Specification

class SolitaryExampleSpecification extends Specification {

    def "Should test code solitary"() {
        given:
        def userFactory = Mock(UserFactory)
        def insertUserPort = Mock(InsertUserPort)

        def command = new CreateUserUseCase.Command("firstName", "lastName")

        userFactory.create(command) > new User(new UserIdentifier("EMP0001"),"firstName", "lastName")

        def createUserService = new CreateUserService(userFactory, insertUserPort)

        when:
        def identifier = createUserService.create(command)

        then:
        identifier.value() == "EMP0001"
    }
}
