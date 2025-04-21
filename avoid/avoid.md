# What should be avoided

## Waiting in tests

Since unit tests should be fast and efficient, avoid using blocking calls like `sleep` in your test code. Using
`Thread.sleep` introduces unnecessary delays and can lead to flaky tests.

For example, instead of writing:

```java
Thread.sleep(1000);
```

It's preferable to use a more robust waiting mechanism. One such alternative is provided by Awaitility, which allows you
to wait for a condition to be met within a specified timeout:

```java
await().

atMost(1,SECONDS)
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
verify(createUserFactory).createUser(any());

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