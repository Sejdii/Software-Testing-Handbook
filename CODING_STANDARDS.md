# Coding Standards: Testing

## Scope and Terms

These rules apply to all automated tests.

| Term | Meaning |
|------|---------|
| Sociable unit test | A unit test that uses real implementations of collaborators. It uses in-memory substitutes for external systems. |
| Solitary unit test | A unit test that replaces all collaborators with mocks. |
| Module test | A test of a complete use case through an incoming adapter. It includes real business logic and real outgoing adapters. |
| Property-based test | A test that checks a general property over many generated inputs instead of only hand-picked examples. |
| Differential test | A test that compares the observable behavior of an existing implementation with a replacement on the same inputs. |

## Verification Principles

- Verification should demonstrate that the requested behavior works, not only that individual components pass their tests.
- The verification scope should match the affected behavior and risk of the change, with focused tests for individual behaviors and real end-to-end evidence for complete features.
- Tests and verification harnesses should be fast, reliable, and diagnostic. A failure should identify the failed behavior and provide enough evidence to investigate it.
- Passing unit tests alone isn't evidence that a complete feature works.

## Unit Tests

### Required Properties (F.I.R.S.T.)

- A unit test should be fast.
- A unit test should be independent. It shouldn't depend on other tests or on test order.
- A unit test should be repeatable. It should give the same result in each environment.
- A unit test should be self-validating. It shouldn't need manual inspection.

### Preferred Style: Sociable

- Unit tests should be sociable. A solitary unit test is acceptable only when a sociable test is not practical.
- A sociable unit test should call the public API of the unit. It shouldn't call internal classes directly.
- The unit should be tested as a group of classes. There shouldn't be one test class for each production class.
- Collaborators that are inside the unit should be real implementations.
- An external system that the test needs as state (for example, a repository) should be replaced by a simple in-memory substitute. The substitute should be updated when the real adapter changes.
- A test shouldn't fail after a refactoring that keeps the observable behavior. If it does, the test is coupled to the implementation and should be changed.
- A mixed style (real implementations for some collaborators, mocks for others) is preferred to a test that mocks all collaborators.

### When to Use a Mock

A collaborator should be a real implementation when:

- it belongs only to the class under test (example: a strategy used by only one client),
- it is an aggregate or other domain object,
- a mock would copy its logic (example: a mapper),
- it is a utility class.

A collaborator should be a mock or an in-memory substitute when:

- it is shared by many classes and is not part of the unit,
- it does side effects: database, HTTP call, or message broker. A repository should be an in-memory substitute. A "fire and forget" message should be a simple mock.

A mapper that needs mocks for its own dependencies is a design problem. Mappers should be kept simple.

### One Test, One Behavior

- A test should verify exactly one behavior.
- A test shouldn't combine two independent behaviors. Behaviors are independent when they can change for different reasons. Example: "create user" and "send welcome email".
- The test name should state the behavior. Example: `shouldReserveRoom`.
- The test should have three separate, clear parts: given, when, then.

### What Not to Unit Test

- Adapters for external systems shouldn't be unit tested. Integration tests cover them.
- Configuration classes without logic shouldn't be unit tested.
- Simple getters and setters without logic shouldn't be tested.

## Integration Tests

- Integration tests should verify the interaction with an external system. They shouldn't verify business logic.
- Each adapter with non-trivial logic should have its own integration test.
- The test should use the same type of external system as production. A container with a real database is preferred to an in-memory database.
- Database adapter tests should verify custom queries, entity mappings, and schema migrations.
- Tests shouldn't leave data that changes other tests. They should use a rollback or an explicit cleanup.
- Message broker adapter tests should verify the content of the sent message, including the topic and the format of the payload.
- A test of a simple message-sending adapter may be omitted when a module test covers it.
- Incoming web adapter tests should verify URL mapping, request parsing, response status, headers, and body, input validation, error responses, and authorization.
- An incoming adapter test should replace the use case with a mock. It shouldn't test business logic.
- Tests shouldn't verify the internal behavior of the framework.

## Module Tests

- A module test should verify a complete use case from the client point of view: from the incoming request to the response and all side effects.
- Module tests should exist only for critical business use cases, main happy paths, and complex integrations between components.
- Prefer unit tests for edge cases. Use integration or module tests when meaningful verification requires real adapters or interactions across system boundaries.
- Module tests shouldn't be the only test type. They are slower than unit tests, couple the tests to the adapters, and make edge cases hard to test.
- A module test should verify all results of the use case:
    - the response (status, headers, body)
    - the persisted state
    - the published messages
    - other side effects (for example, files or calls to external APIs)
- The test should use the same type of database and message broker as production, or a close substitute.
- Test data (for example, the database state) should be prepared explicitly in each test or test class, not inherited from other tests.
- Code that sends requests and checks results should be in small helper methods, so the test stays readable.
- Asynchronous processes should be tested with a wait for a condition, not with a fixed delay.

## Property-Based Tests

- Use property-based tests where the behavior has a general constraint that should hold across many inputs, especially for parsers, serializers, transformations, and state transitions.
- State the property explicitly. Examples include round-trip encoding, domain invariants, idempotency, and equivalence to a simple reference implementation.
- Use a property-testing library to generate varied inputs, including boundary values. Don't derive the expected result by copying the implementation under test.
- Preserve the seed or minimized failing input so a failure can be reproduced. Keep a focused regression case for a discovered bug when appropriate.
- Use property-based tests alongside example-based tests, not as a replacement for verifying concrete user flows.

## Differential Tests

- For behavior-preserving migrations, rewrites, and optimizations, differential tests should be considered according to regression risk when a runnable baseline or previously captured baseline outputs are available and comparison is practical. Otherwise, regression tests should verify established behavior and acceptance criteria.
- Where differential tests are used, the baseline and replacement should be evaluated on identical inputs: generated cases or an approved, sanitized sample of production traffic.
- Comparisons should cover observable outputs and relevant side effects. Executions should be isolated so one implementation's state changes don't affect the other.
- Normalization should remove only differences irrelevant to the required behavior, such as insignificant formatting. It must not hide meaningful ordering, errors, or data.
- Intentional differences should be documented and approved. An unexplained difference is a possible regression, not an automatic update to the expected result.
- Review evidence should identify the baseline revision or captured outputs, the inputs compared, and the results. The reviewer shouldn't need to reconstruct the original implementation.

## Maintaining the Verification System

- A change must not be able to weaken the rules that decide whether that same change passes.
- Tests and rules may evolve, but changes to gating policy need independent approval and must not silently relax the gate for their own change.
- Prune redundant tests, obsolete rules, and low-value verification steps. Maintain the verification harness like production code.

## Practices to Avoid

### Waiting

- A test shouldn't use fixed delays (`sleep`).
- Fix: wait for a condition with a timeout.

### Ignoring Flaky Tests

- A test that sometimes passes without a relevant code change is a reliability defect. Don't rerun it until it passes and call the change verified.
- Don't skip a flaky test, add fixed delays, or weaken its assertions to hide the failure.
- Fix: preserve failure evidence, investigate the root cause, and verify the repair.
- If the root cause remains unresolved, require human approval before quarantining or deleting the test. Record the reason, the coverage lost, and follow-up ownership; make any quarantine visible in verification results.

### Interaction Verification

- A test shouldn't verify calls on mocks, unless the call is the observable result (for example, a "fire and forget" message).
- A test shouldn't verify a call when a result assertion already fails if the call does not occur.
- Fix: verify the observable result: a returned value or a changed state.

### Conditional Logic

- Individual example-based test scenarios shouldn't contain `if`, `switch`, or loops. Property-based generators and shared verification harnesses may use control flow to generate inputs or drive a flow, but shouldn't conditionally skip required assertions.
- A parameterized test shouldn't be used when each parameter needs a different assertion.
- Fix: write one test for each case.

### Wildcard Matchers

- A test shouldn't use wildcard matchers (`any()`, `_ as Type`) for arguments of mocked calls. A wildcard hides wrong arguments.
- Fix: use the exact expected values.

### Lenient and Unused Mocks

- A test shouldn't use lenient mocks.
- A test shouldn't contain a mock, stub, or field that it does not use.
- A test shouldn't have a "no other interactions" check added by default.
- Fix: remove the unused mock, stub, or field. In frameworks where mocks are lenient by default, check that each stub is necessary.
