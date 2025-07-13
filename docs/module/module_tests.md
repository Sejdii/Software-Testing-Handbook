# Module Tests

## Definition and Purpose

Module tests verify complete use cases from the client's perspective. They validate the entire request flow - from 
the incoming request through application logic to the outgoing response, including side effects like persisted database 
state and outgoing messages. These tests ensure that all layers of the application work together correctly, providing 
strong confidence that the feature functions as expected in production.

## When to Use Module Tests

Developers should create module tests selectively for:

- The most critical business use cases
- Primary happy path scenarios
- Use cases where integration between components is complex

Edge cases are typically better covered by lower-level tests due to the additional setup complexity required at the module level.

## Structure of a Module Test

The following example demonstrates a well-structured module test for a room reservation feature:

```java
class ReserveRoomRestControllerModuleTest extends ModuleTest {

    // fields ommitted for clarity

    @Test
    @Sql("insert-room.sql")
    void shouldReserveRoom() throws Exception {
        // given
        String jsonRequestBody = getJsonRequestBody();

        // when
        ResultActions resultActions = reserveRoom(jsonRequestBody);

        // then
        resultActions.andExpect(status().isCreated());
        assertReservationIsCreated();

        String reservationIdentifier = getIdentifierFromResponse(resultActions);
        assertRoomReservedMessageWasSend(reservationIdentifier);
    }
}
```

Module tests can also validate asynchronous processes, such as event handling:

```java
class UserCreatedEventListenerModuleTest extends ModuleTest {

    // fields ommitted for clarity

    @Test
    void shouldCreateNewReservationParticipant() {
        // given
        UserCreatedEvent event = new UserCreatedEvent(FIRST_NAME, SURNAME);

        // when
        sendMessage(event);

        // then
        await().until(() -> !getAllReservationParticipantsFromDb().isEmpty());

        assertThat(getAllReservationParticipantsFromDb())
                .hasSize(1)
                .element(0)
                .satisfies(UserCreatedEventListenerModuleTest::assertCreatedReservationParticipant);
    }
}
```

Typically, module tests extend a base class that configures the test environment:

```java
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureMockMvc
public abstract class ModuleTest {}
```

## Test Data Setup

Preparing the test environment, particularly database state, is crucial for module tests. Spring's `@Sql` annotation 
offers a convenient approach:

```java
@Test
@Sql("insert-room.sql") // Executes SQL script before the test
void shouldReserveRoom() {
    // test implementation
}
```

The annotation can also be applied at the class level to execute before each test method.

## Simulating User Interactions

### HTTP Requests

For REST API testing, developers can use frameworks like `MockMvc`:

```java
private ResultActions reserveRoom(String jsonRequestBody) throws Exception {
    return mockMvc.perform(
        post("/rooms/{roomIdentifier}/reservations", ROOM_IDENTIFIER)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequestBody));
}
```

Alternatively, real HTTP endpoints can be exposed using:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
```

With `TestRestTemplate` or `TestWebClient` for request handling. While slightly less convenient, this approach more accurately 
represents production conditions.

### Event Messages

Event-driven workflows can be tested by publishing events to message brokers:

```java
private void sendMessage(UserCreatedEvent event) {
    template.send("userCreated", event);
}
```

Extracting interaction logic into dedicated methods improves test readability and maintainability.

## Comprehensive Assertions

Module tests should verify all aspects of the use case, including:

1. **Response validation** - status codes, headers, and body content
2. **Database state** - persisted entities and their relationships
3. **Published messages** - events sent to message brokers
4. **Other side effects** - file creation, external API calls, etc.

For HTTP responses:

```java
// Clear separation of when/then improves readability
// when
ResultActions resultActions = reserveRoom(jsonRequestBody);

// then
resultActions.andExpect(status().isCreated());
```

For database state verification:

```java
private void assertReservationIsCreated() {
    Room room = findRoomPort.find(new RoomIdentifier(ROOM_IDENTIFIER)).orElseThrow();
    assertThat(room.getActiveReservations())
        .hasSize(1)
        .element(0)
        .satisfies(
            reservation -> {
              assertThat(reservation.getReservationOwnerIdentifier().value()).isEqualTo("EMP0001");
              assertThat(reservation.getPeriod()).isEqualTo(createPeriodBetween(12, 13));
              assertThat(reservation.getNumberOfParticipants()).isEqualTo(4);
            });
  }
```

For message validation:

```java
private void assertRoomReservedMessageWasSend(String reservationIdentifier) {
    ConsumerRecord<String, String> roomReservedMessage =
        kafkaTemplate.receive("roomReserved", 0, 0);
    ReservationPeriod reservationPeriod = createPeriodBetween(12, 13);
    assertThat(roomReservedMessage).isNotNull();
    JsonAssert.comparator(JSONCompareMode.STRICT)
        .assertIsMatch(
            roomReservedMessage.value(),
            """
                    {
                      "roomIdentifier": "aedd07ed-efd5-4720-99b0-7ae731df43ff",
                      "reservationIdentifier": "%s",
                      "startTime": "%s",
                      "endTime": "%s"
                    }
                """
                .formatted(
                    reservationIdentifier,
                    reservationPeriod.from().format(DateTimeFormatter.ISO_DATE_TIME),
                    reservationPeriod.to().format(DateTimeFormatter.ISO_DATE_TIME)));
}
```

For asynchronous processes, tools like `Awaitility` help ensure proper test timing.

## Organizational Approaches

Teams typically adopt one of two approaches for organizing module tests:

1. **Co-location with adapters** - Tests reside in the same package as the incoming adapter they test
2. **Dedicated module** - Tests live in a separate package or module dedicated to module testing

Both approaches are valid, with the choice depending on team preferences and project organization.

## Why Not Rely Solely on Module Tests?

While it can be enticing to focus exclusively on module tests—given that they validate complete use cases,
verify integrated behaviors, and benefit from fast execution on modern hardware—there are important reasons to
maintain a balanced testing strategy. Module tests, though powerful, should not be the only layer of automated tests
in your application. Consider the following reasons:

- **Erosion of Hexagonal Architecture Principles:**
  The core of hexagonal (or clean) architecture is the clear separation between business logic and external systems.
  Module tests inherently couple your test logic with concrete adapters and infrastructure, making it harder to safely
  evolve or swap these dependencies. For example, if your project needs to transition from a NoSQL database to a
  relational one, a module-test-only approach would require extensive rewrites of your entire test suite. In contrast,
  a healthy test pyramid with comprehensive unit tests ensures that the core business logic remains protected and
  easily adaptable, leveraging the main advantage of hexagonal design.
- **Scaling Concerns and Execution Time:**
  Module tests, by their nature, tend to execute slower than unit tests, especially as the test suite grows with the project.
  While initial execution on small projects may be quick, larger projects will experience significant slowdowns,
  negatively impacting feedback loops and developer productivity. At scale, teams often need to revisit and streamline
  their test suites, which can be an expensive and time-consuming task if lower-level tests have been neglected.
- **Inefficiency for Test-Driven Development (TDD):**
  The relatively slow feedback loop of module tests makes them a poor fit for TDD practice. Unit tests provide immediate
  feedback, enabling rapid cycles of development and refactoring. Relying only on module tests hinders the fluid
  workflow that makes TDD so effective.
- **Complexity of Test Setup for Coverage:**
  Achieving granular coverage and testing edge cases is significantly more demanding at the module level. Setting up
  test fixtures, especially for complex database states or message broker interactions, becomes cumbersome and
  increases the maintenance burden. Unit tests typically allow you to validate edge cases with far less overhead and more precise control.

## References

- [Integration tests are needed and simple by Piotr Przybył](https://youtu.be/SxJG4uZdvj8?si=07KdaHjQN74dkuRk)
- [How to call a REST API in integration tests](https://blog.allegro.tech/2025/05/how-to-call-rest-api-in-tests.html)
