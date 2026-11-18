# Coding Standards: Testing

## Scope and Terms

These rules apply to all automated tests.

| Term | Meaning |
|------|---------|
| Sociable unit test | A unit test that uses real implementations of collaborators. It uses in-memory substitutes for external systems. |
| Solitary unit test | A unit test that replaces all collaborators with mocks. |
| Module test | A test of a complete use case through an incoming adapter. It includes real business logic and real outgoing adapters. |

## Unit Tests

### Required Properties (F.I.R.S.T.)

- A unit test MUST be fast.
- A unit test MUST be independent. It MUST NOT depend on other tests or on test order.
- A unit test MUST be repeatable. It MUST give the same result in each environment.
- A unit test MUST be self-validating. It MUST NOT need manual inspection.
- Write tests together with the production code. Do not postpone tests to a separate task.

### Preferred Style: Sociable

- Prefer sociable unit tests. Use solitary unit tests only when a sociable test is not practical.
- A sociable unit test MUST call the public API of the unit. It MUST NOT call internal classes directly.
- Test the unit as a group of classes. Do not write one test class for each production class.
- Use real implementations for collaborators that are inside the unit.
- Use an in-memory substitute for an external system when the test needs state (for example, a repository). Keep the substitute simple. Update the substitute when the real adapter changes.
- A test MUST NOT fail after a refactoring that keeps the observable behavior. If it fails, the test is coupled to the implementation. Change the test.
- Prefer a mixed style over a test that mocks all collaborators. A mixed style uses real implementations for some collaborators and mocks for others.

### When to Use a Mock

Use a real implementation in these cases:

- The dependency belongs only to the class under test. Example: a strategy used by only one client.
- The dependency is an aggregate or other domain object.
- A mock would copy the logic of the dependency. Example: a mapper.
- The dependency is a utility class.

Use a mock or an in-memory substitute in these cases:

- The dependency is shared by many classes and is not part of the unit.
- The dependency does side effects: database, HTTP call, or message broker. Prefer an in-memory substitute for a repository. Use a simple mock for a "fire and forget" message.

If a mapper is complex and needs mocks for its own dependencies, change the design of the mapper. Keep mappers simple.

### One Test, One Behavior

- Each test MUST verify exactly one behavior.
- Do not put two independent behaviors in one test. Two behaviors are independent when they can change for different reasons. Example: "create user" and "send welcome email".
- Give each test a name that states the behavior. Example: `shouldReserveRoom`.
- Use the structure given, when, then. Keep the three parts separate and clear.

### What Not to Unit Test

- Do not unit test adapters for external systems. Use integration tests.
- Do not unit test configuration classes that have no logic.
- Do not test simple getters and setters that have no logic.

## Integration Tests

- Integration tests verify the interaction with an external system. They do not verify business logic.
- Test each adapter with its own integration test when the adapter has non-trivial logic.
- Use the same type of external system as in production. Prefer a container with a real database to an in-memory database.
- Database adapter tests MUST verify custom queries, entity mappings, and schema migrations.
- Make sure the tests do not leave data that changes other tests. Use a rollback or an explicit cleanup.
- Message broker adapter tests MUST verify the content of the sent message, including the topic and the format of the payload.
- A test of a simple message-sending adapter MAY be omitted when a module test covers it.
- Incoming web adapter tests MUST verify these items: URL mapping, request parsing, response status, headers, and body, input validation, error responses, and authorization.
- In an incoming adapter test, replace the use case with a mock. Do not test business logic in this test.
- Do not test the internal behavior of the framework.

## Module Tests

- A module test MUST verify a complete use case from the client point of view: from the incoming request to the response and all side effects.
- Write module tests only for critical business use cases, main happy paths, and complex integrations between components.
- Do not use module tests for edge cases. Use unit tests.
- Do not use module tests as the only test type. Module tests are slower than unit tests. They couple the tests to the adapters. They make edge cases hard to test.
- A module test MUST verify all results of the use case:
    - the response (status, headers, body)
    - the persisted state
    - the published messages
    - other side effects (for example, files or calls to external APIs)
- Use the same type of database and message broker as in production, or a close substitute.
- Prepare the test data (for example, the database state) explicitly in each test or test class.
- Put the code that sends requests and checks results in small helper methods. This keeps the test readable.
- Test asynchronous processes with a wait for a condition. Do not use a fixed delay.

## Practices to Avoid

### Waiting

- Do not use fixed delays (`sleep`) in tests. Use a wait for a condition with a timeout.

### Interaction Verification

- Do not verify calls on mocks, unless the call is the observable result (for example, a "fire and forget" message).
- Verify the observable result: a returned value or a changed state.
- Do not verify a call when a result assertion already fails if the call does not occur.

### Conditional Logic

- Do not use `if`, `switch`, or loops in test code.
- Do not use a parameterized test when each parameter needs a different assertion. Write one test for each case.

### Wildcard Matchers

- Do not use wildcard matchers (`any()`, `_ as Type`) for arguments of mocked calls.
- Use the exact expected values. A wildcard hides wrong arguments.

### Lenient and Unused Mocks

- Do not use lenient mocks.
- Remove each mock, stub, and field that the test does not use.
- In frameworks where mocks are lenient by default, check that each stub is necessary. Do not add a "no other interactions" check to every test.
