# What should be avoided

## Waiting in tests

Since tests should be fast and efficient, avoid using blocking calls like `sleep` in your test code. Using
`Thread.sleep` introduces unnecessary delays and can lead to flaky tests.

For example, instead of writing:

```java
Thread.sleep(1000);
```

It's preferable to use a more robust waiting mechanism. One such alternative is provided by Awaitility, which allows you
to wait for a condition to be met within a specified timeout:

```java
await()
    .atMost(1,SECONDS)
    .until(() ->{
        // some condition
        });
```

## Avoid using `verify` unless absolutely necessary

Using `verify` in your tests can lead to brittle tests that are tightly coupled to the implementation details of the
code being tested. This makes your tests less maintainable and more likely to break with minor changes in the
implementation.

Let’s consider the following example:

```java
// given
User user = createExpectedUser();

when(createUserFactory.createUser("login", "password"))
        .thenReturn(user);

// when
User createdUser = createUserService.createUser(createCreateUserCommand());

// then
verify(createUserFactory).createUser(any()); // this line is not needed

assertCreatedUser(createdUser);
```

In this example, verifying that `createUserFactory` was called is not necessary. If the `createUserFactory.createUser`
method is not called, the test will fail due to the `assertCreatedUser` check. From the test’s perspective, it doesn’t
matter whether the method was called or not—the test should focus on the observable behavior of the system under test,
not on the internal implementation details or interactions with dependencies.

## Avoid `if` logic in test code

Using `if` statements in your test code can lead to complex and hard-to-read tests. Tests should always be straightforward and easy to understand, focusing exclusively on the behavior of the code under test.

Here’s an example of a test containing `if` logic:

```java

@ParameterizedTest
@EnumSource(UserType.class)
void shouldGetUserPermissions(UserType type) {
    // given
    User user = createUser(type);

    // when
    Permissions permissions = useCase.getPermissions(user);

    // then
    if (UserType.ADMIN.equals(type)) {
        assertHasWritePermissions(permissions);
    } else {
        assertHasReadOnlyPermissions(permissions);
    }
}

private static User createUser(UserType type) {
    return switch (type) {
        case ADMIN -> createAdminUser();
        case MODERATOR -> createModeratorUser();
        // ... other cases
    };
}
```

In most cases, the presence of `if` logic in tests indicates a poor use of parameterized tests. In this example, the test could be simplified and made more readable by using separate test methods for each `UserType`:

```java
@Test
void shouldAdministratorHaveWritePermission() {
    // given
    User user = createUser(UserType.ADMIN);
    
    // when
    Permissions permissions = useCase.getPermissions(user);
    
    // then
    assertHasWritePermissions(permissions);
}

@Test
void shouldUserHaveReadOnlyPermission() {
    // given
    User user = createUser(UserType.MODERATOR);
    
    // when
    Permissions permissions = useCase.getPermissions(user);
    
    // then
    assertHasReadOnlyPermissions(permissions);
}
```

## Avoid using any or wildcard matchers in tests

Mocking libraries like Mockito allow you to use `any()` to match arguments,
but this can create overly broad tests that fail to verify specific behavior.

Consider this code:

```java
public UserIdentifier create(Command command) {
    User user = userFactory.create(command.firstName(), command.lastName());
    insertUserPort.insert(user);

    return user.identifier();
}
```

and the corresponding test with Spock equivalent to Mockito's `any()`:

```groovy
def "Should create user"() {
    given: "Command of user creation"
    def command = new CreateUserUseCase.Command(FIRST_NAME, LAST_NAME)

    this.userFactory.create(_ as String, _ as String) >> new User(new UserIdentifier(GENERATED_IDENTIFIER), FIRST_NAME, LAST_NAME)

    when: "User is created"
    def identifier = createUserUseCase.create(command)
    
    // rest of test ommitted
}
```

In this test,
using `_ as String` (which is equivalent to `any()` in Mockito)
does not verify that the correct values are passed to the `create` method of `userFactory`.
This can lead to false positives,
where the test passes even if the method is called with incorrect arguments.
It is better to verify that specific values are provided as arguments.

```groovy
this.userFactory.create(FIRST_NAME LAST_NAME) >> new User(new UserIdentifier(GENERATED_IDENTIFIER), FIRST_NAME, LAST_NAME)
```

## Avoid using lenient mocks

Lenient mocks can negatively impact the maintainability of tests.
They allow unused mocks or unnecessary stubbing to remain in the test suite,
making it difficult to understand which mocks are actually required.
In many tests that use lenient mocks, I have seen numerous mocks,
many of which were needed in the past but are no longer relevant.
This leads to confusion and makes it harder to determine the true purpose of the test.

Example:
```java
    @Mock
    private UserFactory userFactory;

    @Mock
    private InsertUserPort insertUserPort;

    @Mock
    private DependentClassThatThisServiceIsNotLongerUse dependentClass;

    @InjectMocks
    private CreateUserService createUserService;

    @BeforeEach
    void setUp() {
        lenient().when(dependentClass.doSomething()).thenReturn(1);
    }

    @Test
    void shouldCreateUser() {
        // Given
        CreateUserUseCase.Command command = new CreateUserUseCase.Command(FIRST_NAME, LAST_NAME);

        when(userFactory.create(command)).thenReturn(
            new User(new UserIdentifier(GENERATED_IDENTIFIER), FIRST_NAME, LAST_NAME));

        // When
        createUserService.create(command);

        // Then
        var captor = ArgumentCaptor.forClass(User.class);

        verify(insertUserPort).insert(captor.capture());
        // assertions for created user omitted
    }
```

In this example, `DependentClassThatThisServiceNoLongerUses`
is no longer used by the class under test, but the mock and its lenient stubbing remain in the test.
This can be confusing and obscures the real purpose of the test.
Lenient mocks often lead to an excess of unused or irrelevant mocks.
If you remove the `lenient` configuration, the test will fail,
sending a clear signal that this mock or stubbing should be removed.

```text
Unnecessary stubbings detected.
Clean & maintainable test code requires zero unnecessary code.
Following stubbings are unnecessary (click to navigate to relevant line of code):
  1. -> at pl.sejdii.user.application.service.MockitoAntiPatternExampleTest.setUp(MockitoAntiPatternExampleTest.java:40)
```

In Spock, the situation is different: all mocks are lenient by default.
However, you can enforce strictness in your tests by specifying `0 * _` interactions.
Since leniency is a design feature of Spock, it is generally accepted as part of the framework,
and it is not recommended to use `0 * _` in every test.
